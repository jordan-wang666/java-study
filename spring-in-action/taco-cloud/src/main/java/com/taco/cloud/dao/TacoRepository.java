package com.taco.cloud.dao;

import com.taco.cloud.entity.Taco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TacoRepository extends JpaRepository<Taco, Long> {
}
