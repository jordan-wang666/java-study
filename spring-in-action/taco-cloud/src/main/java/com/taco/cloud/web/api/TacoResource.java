package com.taco.cloud.web.api;

import com.taco.cloud.entity.Ingredient;
import com.taco.cloud.entity.Taco;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Date;
import java.util.List;

@Relation(value = "taco", collectionRelation = "tacos")
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
