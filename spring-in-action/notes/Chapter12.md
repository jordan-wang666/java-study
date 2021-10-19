# 12.Persisting data reactively

> **This chapter covers**
> - Spring Data’s reactive repositories
> - Writing reactive repositories for Cassandra and MongoDB
> - Adapting non-reactive repositories for reactive use
> - Data modeling with Cassandra

### Understanding Spring Data’s reactive story

Beginning with the Spring Data Kay release train, Spring Data offered its first support for working with reactive
repositories. This includes support for a reactive programming model when persisting data with Cassandra, MongoDB,
Couchbase, or Redis.

> What’s in a name?
>
> Although Spring Data projects are versioned at their own pace, they’re collectively published in a release train, where each version of the release train is named for a significant figure in computer science.
>
> These names are alphabetical in nature and include names such as Babbage, Codd, Dijkstra, Evans, Fowler, Gosling, Hopper, and Ingalls. At the time this is being written, the most recent release train version is Spring Data Kay, named after Alan Kay, one of the designers of the Smalltalk programming language.

You may have noticed that I failed to mention relational databases or JPA. Unfortu- nately, there’s no support for
reactive JPA. Although relational databases are certainly the most prolific databases in the industry, supporting a
reactive programming model with Spring Data JPA would require that the databases and JDBC drivers involved also support
non-blocking reactive models. It’s unfortunate that, at least for now, there’s no support for working with relational
databases reactively. Hopefully, this situation will be resolved in the near future.

In the meantime, this chapter focuses on using Spring Data to develop repositories that deal in reactive types for those
databases that do support a reactive model. Let’s see how Spring Data’s reactive model compares to its non-reactive
model.

### Working with reactive Cassandra repositories

Cassandra is a distributed, high-performance, always available, eventually consistent, partitioned-row-store, NoSQL
database.

That’s a mouthful of adjectives to describe a database, but each one accurately speaks to the power of working with
Cassandra. To put it in simpler terms, Cassandra deals in rows of data, which are written to tables, which are
partitioned across one-to- many distributed nodes. No single node carries all the data, but any given row may be
replicated across multiple nodes, thus eliminating any single point of failure.

#### Enabling Spring Data Cassandra

Maven dependency

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-cassandra</artifactId>
</dependency>
```

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-cassandra-reactive</artifactId>
</dependency>
```

Database create keyspace

```cassandraql
create keyspace tacocloud
    with replication ={'class':'SimpleStrategy', 'replication_factor':1}
     and durable_writes = true;
```

Now that you’ve created a key space, you need to configure the `spring.data .cassandra.keyspace-name` property to tell
Spring Data Cassandra to use that key space:

```yaml
spring:
  data:
    cassandra:
      keyspace-name: tacocloud
      schema-action: recreate-drop-unused
```

Here, you also set the `spring.data.cassandra.schema-action` to `recreate-drop-unused`. This setting is very useful for
development purposes because it ensures that any tables and user-defined types will be dropped and recreated every time
the application starts. The default value, none, takes no action against the schema and is useful in production settings
where you’d rather not drop all tables whenever an application starts up.

```yaml
spring:
  data:
    cassandra:
      contact-points: localhost
      port: 9042
      username: cassandra
      password: cassandra
      keyspace-name: tacocloud
      local-datacenter: DC1
```

#### Understanding Cassandra data modeling

These are a few of the most important things to understand about Cassandra data modeling:

- Cassandra tables may have any number of columns, but not all rows will neces- sarily use all of those columns.
- Cassandra databases are split across multiple partitions. Any row in a given table may be managed by one or more
  partitions, but it’s unlikely that all partitions will have all rows.
- A Cassandra table has two kinds of keys: partition keys and clustering keys. Hash operations are performed on each
  row’s partition key to determine which parti- tion(s) that row will be managed by. Clustering keys determine the order
  in which the rows are maintained within a partition (not necessarily the order that they may appear in the results of
  a query).
- Cassandra is highly optimized for read operations. As such, it’s common and desir- able for tables to be highly
  denormalized and for data to be duplicated across

#### Mapping domain types for Cassandra persistence

Let’s start with the `Ingredient` class, as it’s the simplest to map for `Cassandra`. The new
Cassandra-ready `Ingredient`
class looks like this:

```java
package tacos;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Table("ingredients")
public class Ingredient {
    @PrimaryKey
    private final String id;
    private final String name;
    private final Type type;

    public static enum Type {
        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
    }
}

```

But don’t let the `Ingredient` mapping fool you. The `Ingredient` class is one of your simplest domain types. Things get
more interesting when you map the Taco class for `Cassandra` persistence.

```java
package tacos;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.rest.core.annotation.RestResource;
import com.datastax.driver.core.utils.UUIDs;
import lombok.Data;

@Data
@RestResource(rel = "tacos", path = "tacos")
@Table("tacos")
public class Taco {
    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    private UUID id = UUIDs.timeBased();
    @NotNull
    @Size(min = 5, message = "Name must be at least 5 characters long")
    private String name;
    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.DESCENDING)
    private Date createdAt = new Date();
    @Size(min = 1, message = "You must choose at least 1 ingredient")
    @Column("ingredients")
    private List<IngredientUDT> ingredients;
}
```

Finally, the `ingredients` property is now a List of `IngredientUDT` objects instead of a List of `Ingredient` objects.
As you’ll recall, `Cassandra` tables are highly denormalized and may contain data that’s duplicated from other tables.
Although the `ingredient` table will serve as the table of record for all available ingredients, the ingredients chosen
for a taco will be duplicated in the `ingredients` column. Rather than simply reference one or more rows in the
ingredients table, the `ingredients` property will contain full data for each chosen ingredient.

But why do you need to introduce a new `IngredientUDT` class? Why can’t you just reuse the `Ingredient` class? Put
simply, columns that contain collections of data, such as the `ingredients` column, must be collections of native
types (integers, strings, and so on) or must be collections of user-defined types.

In `Cassandra`, user-defined types enable you to declare table columns that are richer than simple native types. Often
they’re used as a denormalized analog for relational foreign keys. In contrast to foreign keys, which only hold a
reference to a row in another table, columns with user-defined types actually carry data that may be copied from a row
in another table. In the case of the `ingredients` column in the tacos table, it will contain a collection of data
structures that define the ingredients themselves.

You can’t use the `Ingredient` class as a user-defined type, because the `@Table` annotation has already mapped it as an
entity for persistence in `Cassandra`. There- fore, you must create a new class to define how ingredients will be stored
in the `ingredients` column of the taco table. IngredientUDT (where “UDT” means user- defined type) is the class for the
job:

```java
package tacos;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@UserDefinedType("ingredient")
public class IngredientUDT {
    private final String name;
    private final Ingredient.Type type;
}
```

Although `IngredientUDT` looks a lot like `Ingredient`, its mapping requirements are much simpler. It’s annotated with
`@UserDefinedType` to identify it as a user-defined type in `Cassandra`. But otherwise, it’s a simple class with a few
properties.

You’ll also note that the `IngredientUDT` class doesn’t include an id property. Although it could include a copy of the
id property from the source Ingredient, that’s not necessary. In fact, the user-defined type may include any properties
you wish—it doesn’t need to be a one-to-one mapping with any table definition.

```java
package com.taco.cloud.entity;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Table("tacoorders")
public class TacoOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @PrimaryKey
    private UUID id = Uuids.timeBased();
    private Date placedAt = new Date();

    @Column("user")
    private UserUDT user;

    private String deliveryName;

    private String deliveryStreet;

    private String deliveryCity;

    private String deliveryState;

    private String deliveryZip;

    private String ccNumber;

    private String ccExpiration;

    private String ccCVV;

    @Column("tacos")
    private List<TacoUDT> tacos = new ArrayList<>();

    public void addTaco(Taco taco) {
        this.addTaco(new TacoUDT(taco.getName(), taco.getIngredients()));
    }

    public void addTaco(TacoUDT tacoUDT) {
        this.tacos.add(tacoUDT);
    }
}
```

#### Writing reactive Cassandra repositories

When it comes to writing reactive `Cassandra` repositories, you have the choice of two base interfaces:
`ReactiveCassandraRepository` and `ReactiveCrudRepository`. Which we choose largely depends on how the repository will
be used. `ReactiveCassandraRepository` extends ReactiveCrudRepository to offer a few variations of an `insert()` method,
which is optimized when the object to be saved is new. Otherwise, `ReactiveCassandraRepository` offers the same
operations as `ReactiveCrudRepository`. If you’ll be inserting a lot of data, you might choose
`ReactiveCassandraRepository`. Otherwise, it’s better to stick with `ReactiveCrudRepository`, which is more portable
across other database types.

```java
public interface IngredientRepository
        extends ReactiveCrudRepository<Ingredient, String> {
}
```

```java
public interface TacoRepository
        extends ReactiveCrudRepository<Taco, UUID> {
}
```

```java
public interface UserRepository
        extends ReactiveCassandraRepository<User, UUID> {
    @AllowFiltering
    Mono<User> findByUsername(String username);
}
```

### Writing reactive MongoDB repositories

MongoDB is a another well-known NoSQL database. Whereas Cassandra is a row- store database, MongoDB is considered a
document database. More specifically, MongoDB stores documents in BSON (Binary JSON) format, which can be queried for
and retrieved in a way that’s roughly similar to how you might query for data in any other database.

#### Enabling Spring Data MongoDB

If you’re working with non-reactive MongoDB, you’ll add the following dependency to the build:

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

This dependency is also available from the Spring Initializr by checking the MongoDB check box. But this chapter is all
about writing reactive repositories, so you’ll choose the reactive Spring Data MongoDB starter dependency instead:

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>
```

By default, Spring Data MongoDB assumes that you have a MongoDB server running locally and listening on port 27017. But
for convenience in testing or developing, you can choose to work with an embedded Mongo database instead. To do that,
add the Flapdoodle Embedded MongoDB dependency to your build:

```xml

<dependency>
    <groupId>de.flapdoodle.embed</groupId>
    <artifactId>de.flapdoodle.embed.mongo</artifactId>
</dependency>
```

Embedded databases are fine for development and testing, but once you take your application to production, you’ll want
to be sure you set a few properties to let Spring Data MongoDB know where and how your production Mongo database can be
accessed:

```yaml
spring:
  data:
    mongodb:
      host: mongodb.tacocloud.com
      port: 27018
      username: tacocloud
      password: s3cr3tp455w0rd
      database: tacoclouddb
```

Not all of these properties are required, but they’re available to help point Spring Data MongoDB in the right direction
in the event that your Mongo database isn’t running locally. Breaking it down, here’s what each property configures:

- spring.data.mongodb.host—The hostname where Mongo is running (default: localhost)
- spring.data.mongodb.port—The port that the Mongo server is listening on (default: 27017)
- spring.data.mongodb.username—The username to use to access a secured Mongo database
- spring.data.mongodb.password—The password to use to access a secured Mongo database
- spring.data.mongodb.database—The database name (default: test)

#### Mapping domain types to documents

Spring Data MongoDB offers a handful of annotations that are useful for mapping domain types to document structures to
be persisted in MongoDB. Although Spring Data MongoDB provides a half dozen annotations for mapping, only three of them
are useful for most common use cases:

- `@Id`—Designates a property as the document ID (from Spring Data Commons)
- `@Document`—Declares a domain type as a document to be persisted to MongoDB
- `@Field`—Specifies the field name (and optionally the order) for storing a property in the persisted document

```java
package com.taco.taco.bean;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Document(collection = "ingredients")
public class Ingredient {
    @Id
    private String id;
    private String name;
    private Type type;

    public static enum Type {
        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
    }
}
```

```java

@Data
@RestResource(rel = "tacos", path = "tacos")
@Document
public class Taco {
    @Id
    private String id;
    @NotNull
    @Size(min = 5, message = "Name must be at least 5 characters long")
    private String name;
    private Date createdAt = new Date();
    @Size(min = 1, message = "You must choose at least 1 ingredient")
    private List<Ingredient> ingredients;
}
```

```java

@Data
@Document
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private Date placedAt = new Date();
    @Field("customer")
    private User user;
    // other properties omitted for brevity's sake
    private List<Taco> tacos = new ArrayList<>();

    public void addDesign(Taco design) {
        this.tacos.add(design);
    }
}
```

```java

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@RequiredArgsConstructor
@Document
public class User implements UserDetails {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private final String username;
    private final String password;
    private final String fullname;
    private final String street;
    private final String city;
    private final String state;
    private final String zip;
    private final String phoneNumber;
    // UserDetails method omitted for brevity's sake
}
```

#### Writing reactive MongoDB repository interfaces

Spring Data MongoDB offers automatic repository support similar to what’s provided by Spring Data JPA and Spring Data
Cassandra. When it comes to writing reactive repositories for MongoDB, you have a choice between ReactiveCrudRepository
and ReactiveMongoRepository. The key difference is that ReactiveMongoRepository provides a handful of special `insert()`
methods that are optimized for persisting new documents, whereas ReactiveCrudRepository relies on `save()` methods for
new and existing documents.

```java
package tacos.data;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import tacos.Ingredient;

@CrossOrigin(origins = "*")
public interface IngredientRepository
        extends ReactiveCrudRepository<Ingredient, String> {
}

```

```java
public interface TacoRepository
        extends ReactiveMongoRepository<Taco, String> {
    Flux<Taco> findByOrderByCreatedAtDesc();
}
```

### Summary

- Spring Data supports reactive repositories for Cassandra, MongoDB, Couch- base, and Redis databases.
- Spring Data’s reactive repositories follow the same programming model as non- reactive repositories, except that they
  deal in terms of reactive publishers such as Flux and Mono.
- Non-reactive repositories (such as JPA repositories) can be adapted to work with Mono and Flux, but they ultimately
  still block while data is saved and fetched.
- Working with nonrelational databases demands an understanding of how to model data appropriately for how the database
  ultimately stores the data.