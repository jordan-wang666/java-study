# 6. Creating REST services

> **This chapter covers**
> - Defining REST endpoints in Spring MVC
> - Enabling hyperlinked REST resources
> - Automatic repository-based REST endpoints

### Writing RESTful controllers

> **To SPA or not to SPA?**
>
> You developed a traditional multipage application (MPA) with Spring MVC in chapter 2,
> and now you’re replacing that with a single-page application (SPA) based on Angular.
> But I’m not suggesting that SPA is always a better choice than MPA. Because presentation
> is largely decoupled from backend processing in a SPA, it affords the opportunity to develop
> more than one user interface (such as a native mobile application) for the same backend
> functionality. It also opens up the opportunity for integration with other applications that can consume the API.
> But not all appli- cations require that flexibility, and MPA is a simpler design if all you need is to display
> information on a web page.

### Retrieving data from the server

**A RESTful controller for taco design API requests**

```java
package tacos.web.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityLinks;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tacos.Taco;
import tacos.data.TacoRepository;

@RestController
@RequestMapping(path = "/design",
        produces = "application/json")
@CrossOrigin(origins = "*")
public class DesignTacoController {
    private TacoRepository tacoRepo;
    @Autowired
    EntityLinks entityLinks;

    public DesignTacoController(TacoRepository tacoRepo) {
        this.tacoRepo = tacoRepo;
    }

    @GetMapping("/recent")
    public Iterable<Taco> recentTacos() {
        PageRequest page = PageRequest.of(
                0, 12, Sort.by("createdAt").descending());
        return tacoRepo.findAll(page).getContent();
    }
}
```

The <kbd>@RestController</kbd> annotation serves two purposes. First, it’s a stereotype annotation like <kbd>
@Controller</kbd>
and <kbd>@Service</kbd> that marks a class for discovery by component scanning. But most relevant to the discussion of
REST, the <kbd>@RestController</kbd> annotation tells Spring that all handler methods in the controller should have
their return value written directly to the body of the response, rather than being carried in the model to a view for
rendering.

For example, the following command line shows how you might fetch recently created tacos with curl:

```
$ curl localhost:8080/design/recent
```

Or like this if you prefer HTTPie:

```
$ http :8080/design/recent
```

### Updating data on the server

In that sense, `PUT` is really intended to perform a wholesale replacement operation rather than an update operation. In
contrast, the purpose of `HTTP` `PATCH` is to perform a patch or partial update of resource data.

> **There’s more than one way to PATCH**
> The patching approach applied in the patchOrder() method has a couple of limitations:
> - If null values are meant to specify no change, how can the client indicate that a field should be set to null?
>
> - There’s no way of removing or adding a subset of items from a collection. If the client wants to add or remove an entry from a collection, it must send the complete altered collection.
>
> There’s really no hard-and-fast rule about how PATCH requests should be handled or what the incoming data should look like. Rather than sending the actual domain data, a client could send a patch-specific description of the changes to be applied. Of course, the request handler would have to be written to handle patch instructions instead of the domain data.

### Enabling hypermedia

*Hypermedia as the Engine of Application State*, or HATEOAS, is a means of creating self-describing APIs wherein
resources returned from an API contain links to related resources. This enables clients to navigate an API with minimal
understanding of the API’s URLs. Instead, it understands relationships between the resources served by the API and uses
its understanding of those relationships to discover the API’s URLs as it tra- verses those relationships.

```json

{
  "_embedded": {
    "tacoResourceList": [
      {
        "name": "Veg-Out",
        "createdAt": "2018-01-31T20:15:53.219+0000",
        "ingredients": [
          {
            "name": "Flour Tortilla",
            "type": "WRAP",
            "_links": {
              "self": {
                "href": "http://localhost:8080/ingredients/FLTO"
              }
            }
          },
          {
            "name": "Corn Tortilla",
            "type": "WRAP",
            "_links": {
              "self": {
                "href": "http://localhost:8080/ingredients/COTO"
              }
            }
          },
          {
            "name": "Diced Tomatoes",
            "type": "VEGGIES",
            "_links": {
              "self": {
                "href": "http://localhost:8080/ingredients/TMTO"
              }
            }
          },
          {
            "name": "Lettuce",
            "type": "VEGGIES",
            "_links": {
              "self": {
                "href": "http://localhost:8080/ingredients/LETC"
              }
            }
          },
          {
            "name": "Salsa",
            "type": "SAUCE",
            "_links": {
              "self": {
                "href": "http://localhost:8080/ingredients/SLSA"
              }
            }
          }
        ],
        "_links": {
          "self": {
            "href": "http://localhost:8080/design/4"
          }
        }
      }
    ]
  },
  "_links": {
    "recents": {
      "href": "http://localhost:8080/design/recent"
    }
  }
}
```

This particular flavor of HATEOAS is known as HAL (Hypertext Application
Language; http://stateless.co/hal_specification.html), a simple and commonly used for- mat for embedding hyperlinks in
JSON responses.

Although this list isn’t as succinct as before, it does provide some useful informa- tion. Each element in this new list
of tacos includes a property named _links that contains hyperlinks for the client to navigate the API. In this example,
both tacos and ingredients each have self links to reference those resources, and the entire list has a recents link
that references itself.

Should a client application need to perform an HTTP request against a taco in the list, it doesn’t need to be developed
with any knowledge of what the taco resource’s URL would look like. Instead, it knows to ask for the self link, which
maps to http:// localhost:8080/design/4. If the client wants to deal with a particular ingredient, it only needs to
follow the self link for that ingredient.

The Spring HATEOAS project brings hyperlink support to Spring. It offers a set of classes and resource assemblers that
can be used to add links to resources before returning them from a Spring MVC controller. To enable hypermedia in the
Taco Cloud API, you’ll need to add the Spring HATEOAS starter dependency to the build:

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
```

### Adding hyperlinks

`Spring HATEOAS` provides two primary types that represent hyperlinked resources: `Resource` and `Resources`.
The `Resource`
type represents a single resource, whereas `Resources` is a collection of resources. Both types are capable of carrying
links to other resources. When returned from a Spring MVC REST controller method, the links they carry will be included
in the JSON (or XML) received by the client.

<kbd>Resource</kbd> replace to <kbd>EntityModel</kbd>

<kbd>Resources</kbd> replace to <kbd>CollectionModel</kbd>

```java
@GetMapping("/recent")
public CollectionModel<EntityModel<Taco>>recentTacos(){
        PageRequest page=PageRequest.of(
        0,12,Sort.by("createdAt").descending());

        List<Taco> tacos=tacoRepository.findAll(page).getContent();
        CollectionModel<EntityModel<Taco>>recentResources=CollectionModel.wrap(tacos);

        recentResources.add(
        new Link("http://localhost:8080/design/recent","recents"));
        return recentResources;
        }
```

As a consequence, the following snippet of JSON is included in the resource returned from the API request:

```json
{
  "_links": {
    "recents": {
      "href": "http://localhost:8080/design/recent"
    }
  }
}
```

Hardcoding a URL like this is a really bad idea. Unless your Taco Cloud ambitions are limited to only ever running the
application on your own development machines, you need a way to not hardcode a URL with localhost:8080 in it.
Fortunately, Spring HATEOAS provides help in the form of link builders.

The most useful of the Spring HATEOAS link builders is `WebMvcLinkBuilder`. This link builder is smart enough to know
what the hostname is without you having to hardcode it. And it provides a handy fluent API to help you build links
relative to the base URL of any controller.

Using `WebMvcLinkBuilder`, you can rewrite the hardcoded Link creation in `recentTacos()` with the following lines:

```java
 CollectionModel<EntityModel<Taco>>recentResources=CollectionModel.wrap(tacos);
        recentResources.add(
        WebMvcLinkBuilder.linkTo(TacoController.class)
        .slash("recent")
        .withRel("recents"));
```

```java
 @GetMapping("/recent")
public CollectionModel<EntityModel<Taco>>recentTacos(){
        PageRequest page=PageRequest.of(
        0,12,Sort.by("createdAt").descending());

        List<Taco> tacos=tacoRepository.findAll(page).getContent();
        CollectionModel<EntityModel<Taco>>recentResources=CollectionModel.wrap(tacos);
        recentResources.add(
        linkTo(methodOn(TacoController.class).recentTacos())
        .withRel("recents"));
        return recentResources;
        }
```

### Creating resource assemblers

Rather than let `Resources.wrap()` create a Resource object for each taco in the list, you’re going to define a utility
class that converts Taco objects to a new Taco- Resource object. The TacoResource object will look a lot like a Taco,
but it will also be able to carry links. The next listing shows what a `TacoResource` might look like.

```java
package com.taco.cloud.web.api;

import com.taco.cloud.entity.Ingredient;
import com.taco.cloud.entity.Taco;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;

public class TacoResource extends RepresentationModel<TacoResource> {

    @Getter
    private String name;

    @Getter
    private Date createdAt;

    @Getter
    private List<Ingredient> ingredients;

    public TacoResource(Taco taco) {
        this.name = taco.getName();
        this.createdAt = taco.getCreatedAt();
        this.ingredients = taco.getIngredients();
    }
}
```

> **NOTE** Domains and resources: separate or the same? Some Spring developers may choose to combine their domain and resource types into a single type by having their domain types extend ResourceSupport. There’s no right or wrong answer as to which is the correct way. I chose to create a separate resource type so that Taco isn’t unnecessarily cluttered with resource links for use cases where links aren’t needed. Also, by creating a separate resource type, I was able to easily leave the id property out so that it won’t be exposed in the API.


To aid in converting Taco objects to TacoResource objects, you’re also going to create a resource assembler. The
following listing is what you’ll need.

```java
package com.taco.cloud.web.api;

import com.taco.cloud.entity.Taco;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class TacoResourceAssembler extends RepresentationModelAssemblerSupport<Taco, TacoResource> {
    /**
     * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and resource type.
     *
     * @param controllerClass DesignTacoController {@literal DesignTacoController}.
     * @param resourceType    TacoResource {@literal TacoResource}.
     */
    public TacoResourceAssembler(Class<?> controllerClass, Class<TacoResource> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    protected TacoResource instantiateModel(Taco taco) {
        return new TacoResource(taco);
    }


    @Override
    public TacoResource toModel(Taco entity) {
        return createModelWithId(entity.getId(), entity);
    }
}
```

Now tweak the `recentTacos()` method to make use of `TacoResourceAssembler`:

```java
package com.taco.cloud.controller;

import com.taco.cloud.dao.TacoRepository;
import com.taco.cloud.entity.Taco;
import com.taco.cloud.web.api.TacoResource;
import com.taco.cloud.web.api.TacoResourceAssembler;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/taco/model")
public class TacoController {

    @Resource
    private TacoRepository tacoRepository;


    @GetMapping("/recent")
    public CollectionModel<TacoResource> recentTacos() {
        PageRequest page = PageRequest.of(
                0, 12, Sort.by("createdAt").descending());

        List<Taco> tacos = tacoRepository.findAll(page).getContent();
        CollectionModel<TacoResource> recentResources = new TacoResourceAssembler(TacoController.class, TacoResource.class).toCollectionModel(tacos);

        recentResources.add(
                linkTo(methodOn(TacoController.class).recentTacos())
                        .withRel("recents"));
        return recentResources;
    }
}
```

API return JSON:

```json
{
  "_embedded": {
    "tacoResourceList": [
      {
        "name": "taco2",
        "createdAt": "2021-08-11T14:29:42.000+00:00",
        "ingredients": [],
        "_links": {
          "self": {
            "href": "http://localhost:8080/taco/model/2"
          }
        }
      },
      {
        "name": "taco1",
        "createdAt": "2021-08-11T14:29:29.000+00:00",
        "ingredients": [],
        "_links": {
          "self": {
            "href": "http://localhost:8080/taco/model/1"
          }
        }
      }
    ]
  },
  "_links": {
    "recents": {
      "href": "http://localhost:8080/taco/model/recent"
    }
  }
}
```

### Naming embedded relationships

If you take a closer look at returns, you’ll notice that the top-level elements look like this:

```json
{
  "_embedded": {
    "tacoResourceList": [
      ...
    ]
  }
}
```

The `@Relation` annotation can help break the coupling between the JSON field name and the resource type class names as
defined in Java. By annotating `TacoResource` with `@Relation`, you can specify how Spring HATEOAS should name the field
in the resulting JSON:

```java

@Relation(value = "taco", collectionRelation = "tacos")
public class TacoResource extends ResourceSupport {
...
}
```

Return JSON:

```json
{
  "_embedded": {
    "tacos": [
      {
        "name": "taco2",
        "createdAt": "2021-08-11T14:29:42.000+00:00",
        "ingredients": [],
        "_links": {
          "self": {
            "href": "http://localhost:8080/taco/model/2"
          }
        }
      },
      {
        "name": "taco1",
        "createdAt": "2021-08-11T14:29:29.000+00:00",
        "ingredients": [],
        "_links": {
          "self": {
            "href": "http://localhost:8080/taco/model/1"
          }
        }
      }
    ]
  },
  "_links": {
    "recents": {
      "href": "http://localhost:8080/taco/model/recent"
    }
  }
}
```

### Enabling data-backed services

As you saw in chapter 3, Spring Data performs a special kind of magic by automatically creating repository
implementations based on interfaces you define in your code. But Spring Data has another trick up its sleeve that can
help you define APIs for your application.

Spring Data REST is another member of the Spring Data family that automatically creates REST APIs for repositories
created by Spring Data. By doing little more than adding Spring Data REST to your build, you get an API with operations
for each repository interface you’ve defined. To start using Spring Data REST, you add the following dependency to your
build:

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-rest</artifactId>
</dependency>
```

To adjust the base path for the API, set the `spring.data.rest.base-path` property:

```yaml
spring:
  data:
    rest:
      base-path: /api
```

### Adjusting resource paths and relation names

Sometimes, such as with “taco”, it trips up on a word and the pluralized version isn’t quite right. As it turns out,
Spring Data REST pluralized “taco” as “tacoes”, so to make a request for tacos, you must play along and request
/api/tacoes:

```
% curl localhost:8080/api/tacoes
```

You may be wondering how I knew that “taco” would be mispluralized as “tacoes”. As it turns out, Spring Data REST also
exposes a home resource that has links for all exposed endpoints. Just make a GET request to the API base path to get
the goods:

```
$ curl localhost:8080/api
```

The good news is that you don’t have to accept this little quirk of Spring Data REST. By adding a simple annotation to
the Taco class, you can tweak both the relation name and that path:

```java

@Data
@Entity
@RestResource(rel = "tacos", path = "tacos")
public class Taco {
...
}
```

### Paging and sorting

The sort parameter lets you sort the resulting list by any property of the entity. For example, you need a way to fetch
the 12 most recently created tacos for the UI to display. You can do that by specifying the following mix of paging and
sorting parameters:

```
$ curl "localhost:8080/api/tacos?sort=createdAt,desc&page=0&size=12"
```

### Adding custom endpoints

When you write your own API controllers, their endpoints seem somewhat detached from the Spring Data REST endpoints in a
couple of ways:

- Your own controller endpoints aren’t mapped under Spring Data REST’s base path. You could force their mappings to be
  prefixed with whatever base path you want, including the Spring Data REST base path, but if the base path were to
  change, you’d need to edit the controller’s mappings to match.  Any endpoints you define in your own controllers
  won’t be automatically included as hyperlinks in the resources returned by Spring Data REST end- points. This means
  that clients won’t be able to discover your custom endpoints with a relation name. Your own controller endpoints
  aren’t mapped under Spring Data REST’s base path. You could force their mappings to be prefixed with whatever base
  path you want, including the Spring Data REST base path, but if the base path were to change, you’d need to edit the
  controller’s mappings to match.
- Any endpoints you define in your own controllers won’t be automatically included as hyperlinks in the resources
  returned by Spring Data REST end- points. This means that clients won’t be able to discover your custom endpoints with
  a relation name.

Rather than resurrect the `DesignTacoController`, which had several handler methods you won’t need, you’ll create a new
controller that only contains the `recentTacos()` method. RecentTacosController in the next listing is annotated with
`@RepositoryRestController` to adopt Spring Data REST’s base path for its request mappings.

```java
package tacos.web.api;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import tacos.Taco;
import tacos.data.TacoRepository;

@RepositoryRestController
public class RecentTacosController {
    private TacoRepository tacoRepo;

    public RecentTacosController(TacoRepository tacoRepo) {
        this.tacoRepo = tacoRepo;
    }

    @GetMapping(path = "/tacos/recent", produces = "application/hal+json")
    public ResponseEntity<Resources<TacoResource>> recentTacos() {
        PageRequest page = PageRequest.of(
                0, 12, Sort.by("createdAt").descending());
        List<Taco> tacos = tacoRepo.findAll(page).getContent();
        List<TacoResource> tacoResources =
                new TacoResourceAssembler().toResources(tacos);
        Resources<TacoResource> recentResources =
                new Resources<TacoResource>(tacoResources);
        recentResources.add(
                linkTo(methodOn(RecentTacosController.class).recentTacos())
                        .withRel("recents"));
        return new ResponseEntity<>(recentResources, HttpStatus.OK);
    }
}
```

### Adding custom hyperlinks to Spring Data endpoints

By declaring a resource processor bean, however, you can add links to the list of links that Spring Data REST
automatically includes. Spring Data HATEOAS offers `ResourceProcessor`, an interface for manipulating resources before
they’re returned through the API. For your purposes, you need an implementation of `ResourceProcessor` that adds a
`recents` link to any resource of type `PagedResources<Resource <Taco>>` (the type returned for the /api/tacos endpoint)
. The next listing shows a bean declaration method that defines such a `ResourceProcessor`.

```java
 @Bean
public ResourceProcessor<PagedResources<Resource<Taco>>>
        tacoProcessor(EntityLinks links){
        return new ResourceProcessor<PagedResources<Resource<Taco>>>(){
@Override
public PagedResources<Resource<Taco>>process(
        PagedResources<Resource<Taco>>resource){
        resource.add(
        links.linkFor(Taco.class)
        .slash("recent")
        .withRel("recents"));
        return resource;
        }};
        }
```

### Summary

- REST endpoints can be created with Spring MVC, with controllers that follow the same programming model as
  browser-targeted controllers.
- Controller handler methods can either be annotated with @ResponseBody or return ResponseEntity objects to bypass the
  model and view and write data directly to the response body.
- The @RestController annotation simplifies REST controllers, eliminating the need to use @ResponseBody on handler
  methods.
- Spring HATEOAS enables hyperlinking of resources returned from Spring MVC controllers.
- Spring Data repositories can automatically be exposed as REST APIs using Spring Data REST.