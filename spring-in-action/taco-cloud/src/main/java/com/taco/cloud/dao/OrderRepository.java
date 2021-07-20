package com.taco.cloud.dao;

import com.taco.cloud.entity.TacoOrder;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<TacoOrder, Long> {
//    TacoOrder save(TacoOrder order);
}
