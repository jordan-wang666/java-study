package com.example.webflux.repository;

import com.example.webflux.entity.Asset;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends ReactiveCrudRepository<String, Asset> {
}
