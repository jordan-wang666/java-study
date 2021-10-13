# Persisting data reactively

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
