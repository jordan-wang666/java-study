package com.taco.taco.dao;

import com.taco.taco.bean.Ingredient;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends ReactiveCrudRepository<Ingredient, String> {
}
