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