package com.taco.webflux.dao;

import com.taco.cloud.entity.TacoOrder;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;

import java.util.UUID;

public interface OrderRepository extends ReactiveCassandraRepository<TacoOrder, UUID> {
}
