# 7.Consuming REST services

> **This chapter covers**
> - Using RestTemplate to consume REST APIs
> - Navigating hypermedia APIs with Traverson

A Spring application can consume a REST API with

- RestTemplate—A straightforward, synchronous REST client provided by the core Spring Framework.
- Traverson—A hyperlink-aware, synchronous REST client provided by Spring HATEOAS. Inspired from a JavaScript library of
  the same name.
- WebClient—A reactive, asynchronous REST client introduced in Spring 5.

### Consuming REST endpoints with RestTemplate

| Method | Description |
|  ----  | ----  |
|delete(...) |Performs an HTTP DELETE request on a resource at a specified URL
|exchange(...) |Executes a specified HTTP method against a URL, returning a ResponseEntity containing an object mapped from the response body
|execute(...) |Executes a specified HTTP method against a URL, returning an object mapped from the response body
|getForEntity(...)|Sends an HTTP GET request, returning a ResponseEntity containing an object mapped from the response body
|getForObject(...)|Sends an HTTP GET request, returning an object mapped from a response body
|headForHeaders(...)|Sends an HTTP HEAD request, returning the HTTP headers for the specified resource URL
|optionsForAllow(...)|Sends an HTTP OPTIONS request, returning the Allow header for the specified URL
|patchForObject(...)|Sends an HTTP PATCH request, returning the resulting object mapped from the response body
|postForEntity(...)|POSTs data to a URL, returning a ResponseEntity containing an object mapped from the response body
|postForLocation(...)|POSTs data to a URL, returning the URL of the newly created resource
|postForObject(...)|POSTs data to a URL, returning an object mapped from the response body
|put(...)|PUTs resource data to the specified URL

With the exception of TRACE, RestTemplate has at least one method for each of the standard HTTP methods. In addition,
execute() and exchange() provide lower-level, general-purpose methods for sending requests with any HTTP method.

Most of the methods in table 7.1 are overloaded into three method forms:

- One accepts a String URL specification with URL parameters specified in a variable argument list.
- One accepts a String URL specification with URL parameters specified in a Map<String,String>.
- One accepts a java.net.URI as the URL specification, with no support for parameterized URLs.

### GETting resources

```java
class Get {
    public Ingredient getIngredientById(String ingredientId) {
        return rest.getForObject("http://localhost:8080/ingredients/{id}",
                Ingredient.class, ingredientId);
    }
}
```

```java
class Get {
    public Ingredient getIngredientById(String ingredientId) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("id", ingredientId);
        return rest.getForObject("http://localhost:8080/ingredients/{id}",
                Ingredient.class, urlVariables);
    }
}
```

```java
class Get {
    public Ingredient getIngredientById(String ingredientId) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("id", ingredientId);
        URI url = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8080/ingredients/{id}")
                .build(urlVariables);
        return rest.getForObject(url, Ingredient.class);
    }
}
```

```java
class Get {
    public Ingredient getIngredientById(String ingredientId) {
        ResponseEntity<Ingredient> responseEntity =
                rest.getForEntity("http://localhost:8080/ingredients/{id}",
                        Ingredient.class, ingredientId);
        log.info("Fetched time: " +
                responseEntity.getHeaders().getDate());
        return responseEntity.getBody();
    }
}
```

### PUTting resources

```java
class Put {
    public void updateIngredient(Ingredient ingredient) {
        rest.put("http://localhost:8080/ingredients/{id}",
                ingredient,
                ingredient.getId());
    }
}
```

### DELETEing resources

```java
class Delete {
    public void deleteIngredient(Ingredient ingredient) {
        rest.delete("http://localhost:8080/ingredients/{id}",
                ingredient.getId());
    }
}
```

### POSTing resource data

```java
class Post {
    public Ingredient createIngredient(Ingredient ingredient) {
        return rest.postForObject("http://localhost:8080/ingredients",
                ingredient,
                Ingredient.class);
    }
}
```

```java
class Post {
    public URI createIngredient(Ingredient ingredient) {
        return rest.postForLocation("http://localhost:8080/ingredients",
                ingredient);
    }
}
```

```java
class Post {
    public Ingredient createIngredient(Ingredient ingredient) {
        ResponseEntity<Ingredient> responseEntity =
                rest.postForEntity("http://localhost:8080/ingredients",
                        ingredient,
                        Ingredient.class);
        log.info("New resource created at " +
                responseEntity.getHeaders().getLocation());
        return responseEntity.getBody();
    }
}
```

### Summary

- Clients can use RestTemplate to make HTTP requests against REST APIs.
- Traverson enables clients to navigate an API using hyperlinks embedded in the responses.