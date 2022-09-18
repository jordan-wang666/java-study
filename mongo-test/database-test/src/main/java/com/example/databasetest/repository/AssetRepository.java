package com.example.databasetest.repository;

import com.example.databasetest.entity.AssetNumber;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends MongoRepository<AssetNumber, Integer> {
}
