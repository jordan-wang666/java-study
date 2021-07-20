package com.taco.cloud.dao;

import com.taco.cloud.entity.Ingredient;
import com.taco.cloud.entity.TacoOrder;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends CrudRepository<Ingredient, Long>{
//    List<Ingredient> findAll();
//
//    Optional<Ingredient> findById(String id);
//
//    Ingredient save(Ingredient ingredient);
}
