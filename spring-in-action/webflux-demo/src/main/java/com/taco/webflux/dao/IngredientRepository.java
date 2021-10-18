package com.taco.webflux.dao;

import com.taco.cloud.entity.Ingredient;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends ReactiveCassandraRepository<Ingredient, String> {
}
