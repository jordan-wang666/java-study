package com.taco.webflux.dao;

import com.taco.cloud.entity.Taco;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface TacoRepository extends ReactiveCrudRepository<Taco, UUID> {
}
