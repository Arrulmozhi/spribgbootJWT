package com.javainuse.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.javainuse.model.Order;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    Optional<Order> findByRefIdAndUserEmail(String refId, String userEmail);

    List<Order> findByRefId(String refId);
	
	Optional<Order> findByid(String id);

	List<Order> findByUserEmail(String userEmail);

	Optional<Order> findByIdAndUserEmail(String id, String requesterEmail);
	

}

