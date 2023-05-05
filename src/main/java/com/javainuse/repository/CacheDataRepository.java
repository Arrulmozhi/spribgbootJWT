package com.javainuse.repository;

import com.javainuse.model.CacheData;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CacheDataRepository extends MongoRepository<CacheData, String> {
    
}
