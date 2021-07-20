# 3.Working with data

>**This chapter covers**
> - <small>Using Spring’s JdbcTemplate
> - Creating Spring Data JDBC repositories
> - Declaring JPA repositories with Spring Data</small>

### JDBC Template codes
```java
package com.taco.cloud.dao.impl;

import com.taco.cloud.dao.IngredientRepository;
import com.taco.cloud.entity.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcIngredientRepository implements IngredientRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcIngredientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Ingredient> findAll() {
        return jdbcTemplate.query(
                "select id, name, type from Ingredient",
                this::mapRowToIngredient);
    }


    @Override
    public Optional<Ingredient> findById(String id) {
        List<Ingredient> results = jdbcTemplate.query(
                "select id, name, type from Ingredient where id=?",
                this::mapRowToIngredient,
                id);
        return results.size() == 0 ?
                Optional.empty() :
                Optional.of(results.get(0));
    }

    @Override
    public Ingredient save(Ingredient ingredient) {
        jdbcTemplate.update(
                "insert into Ingredient (id, name, type) values (?, ?, ?)",
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getType().toString());
        return ingredient;
    }

    private Ingredient mapRowToIngredient(ResultSet row, int rowNum)
            throws SQLException {
        return new Ingredient(
                row.getString("id"),
                row.getString("name"),
                Ingredient.Type.valueOf(row.getString("type")));
    }
}

```

### Defining a schema and preloading data

![img.png](../img/img3.png)
- <kbd>Taco_Order</kbd> — Holds essential order details
- <kbd>Taco</kbd> — Holds essential information about a taco design
- <kbd>Ingredient_Ref</kbd> — Contains one or more rows for each row in Taco, mapping the taco to the ingredients for that taco
- <kbd>Ingredient</kbd> — Holds ingredient information

### Annotating the domain as entities

Entity needs <kbd>@Entity</kbd> and <kbd>@Id</kbd> to make sure is an entity.
<kbd>@GeneratedValue(strategy = GenerationType.AUTO)</kbd> let id type is auto.
```java
 package tacos;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
public class Taco {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min=5, message="Name must be at least 5 characters long")
    private String name;

    private Date createdAt = new Date();

    @Size(min=1, message="You must choose at least 1 ingredient")
    @ManyToMany()
    private List<Ingredient> ingredients = new ArrayList<>();

    public void addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
    }

}
```

### Declaring JPA repositories

So we do not need to write implement codes.When spring start up Spring Data Jpa will So we do not need to write implement codes.
When spring start up,Spring Data Jpa will generate by auto

```java
package com.taco.cloud.dao;

import com.taco.cloud.entity.TacoOrder;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<TacoOrder, Long> {
}
```

### Customizing repositories
If we need other method except regular CRUD, we just add method like this into your repository.
```java
List<TacoOrder> findByDeliveryZip(String deliveryZip);
```