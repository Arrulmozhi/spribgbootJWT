package com.javainuse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javainuse.model.Item;
import com.javainuse.model.Order;
import com.javainuse.repository.OrderRepository;

import java.util.List;
import java.util.Optional;


@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

   public void saveOrder(Order order) {
            orderRepository.save(order);
        }

        public Optional<Order> getOrder(String id) {
            return orderRepository.findByid(id);
        }

        public List<Order> getOrderByRefId(String refId) {
            return orderRepository.findByRefId(refId);
        }
        public List<Order> getOrders(String userEmail) {
            return orderRepository.findByUserEmail(userEmail);
        }

        public List<Order> getAllOrders() {
        	 return orderRepository.findAll();
        }
		public String placeOrder(Order orderRequest, String userEmail) {
			saveOrder(orderRequest);
			return orderRequest.getId();
		}


		public double calculateCost(Order orderRequest) {
			double calculatedCost = orderRequest.getItems().stream()
	                .mapToDouble(item -> item.getQuantity() * orderRequest.getCost())
	                .sum();
			return orderRequest.getCost();
		}

		public boolean isValidEmail(String requesterEmail) {
			
			if(requesterEmail != "")
			{
				return true;
			}
			return false;
		}

		public Optional<Order> getOrderByRefId(String id, String requesterEmail) {
			return orderRepository.findByRefIdAndUserEmail(id,requesterEmail);
		}

		public Optional<Order> getOrder(String id, String requesterEmail) {
			
			return orderRepository.findByIdAndUserEmail(id,requesterEmail);
		}

	

}
