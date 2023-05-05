package com.javainuse.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javainuse.model.CacheData;
import com.javainuse.model.Item;
import com.javainuse.model.Order;
import com.javainuse.repository.CacheDataRepository;
import com.javainuse.service.OrderService;
import com.javainuse.util.OrderResponse;
import com.javainuse.util.MessageResponse;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1")
public class PlaceOrderController {

  private static final Logger LOGGER = LoggerFactory.getLogger(PlaceOrderController.class);
  
  @Autowired
  private OrderService orderService;

  @Autowired
  private CacheDataRepository cacheDataRepository;
  
   //@Autowired
	///private CacheManager cacheManager;
	//Cache cache = cacheManager.getCache("cachedJwtTkn");
	//String cachedValue = cache.get("cachedJwtTkn", String.class);
  

  
  @PostMapping("/order")
  public ResponseEntity<?> placeOrder(HttpServletRequest request, @RequestBody Order orderRequest) {
    
	  LOGGER.info("INSIDE Postmapping" +request.getContentType());
	  //LOGGER.info("cachedValue :"+cachedValue);
	  // Validate the token by checking the tokenId's presence in the cache
	  

	  String tokenId = getTokenIdFromRequest(request);
	  
	  Optional<CacheData> optionalCacheData = cacheDataRepository.findById("JWT");
	  LOGGER.info("INSIDE Postmapping  optionalCacheData" +optionalCacheData);
	  String cacheToken = "";
	  if (optionalCacheData.isPresent()) {
          cacheToken = optionalCacheData.get().getValue();
          LOGGER.info("INSIDE Postmapping" +cacheToken);
	  }

	  if (!isValidToken(tokenId,cacheToken)) {
		  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
	  }
	  
	  String userEmail = orderRequest.getUserEmail();
    
      // Require a custom header (api_source: "XYZ") in the request
	  String apiSource = request.getHeader("api_source");
      if (apiSource == null || !apiSource.equals("XYZ")) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid API source");
      }
      LOGGER.info("INSIDE Postmapping" + orderRequest);
      // On the controller, validate the request object
    List<String> errors = validateOrderRequest(orderRequest);
    if(errors != null)
    {
    if (!errors.isEmpty() ) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request " + errors);
    }
    }
    
    // Once validated, recalculate the cost for the items on the Service layer
    double recalculatedCost = orderService.calculateCost(orderRequest);
    if (Math.abs(recalculatedCost - orderRequest.getCost()) > 0.001) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mismatch in order cost");
    }
    
    // Push the order details to a MongoDB and return the response
    String orderId = orderService.placeOrder(orderRequest, userEmail);
    return ResponseEntity.ok(new OrderResponse("success", new MessageResponse("Order created successfully")));
  }
  


@GetMapping("/getOrder")
  public ResponseEntity<?> getOrder()
  {
	List<Order> orderList = orderService.getAllOrders();
	  System.out.println("GEt order");
	  return ResponseEntity.ok().body(orderList);
  }

@GetMapping("/{Id}")
public ResponseEntity<?> getOrderDetails(HttpServletRequest request, @PathVariable("Id") String id) {
    
    // Validate API source header
    if (!request.getHeader("api_source").equals("XYZ")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid API source header.");
    }
    System.out.println("ID::"+id);
    // Validate requester email header
    String requesterEmail = request.getHeader("api_requestor");
    //if (!orderService.isValidEmail(requesterEmail)) {
    //    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid requester email.");
    //}
    
    List<Order> order1 = orderService.getOrderByRefId(id);
    
    // Fetch order details from MongoDB
    Optional<Order> order = orderService.getOrder(id);
    if (order1 == null || order1.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body( "Unable to find the order for the given order id.");
    }
    
    return ResponseEntity.ok().body(order1);
}
  
  // Helper methods for token validation and caching
  
  private String getTokenIdFromRequest(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    return null;
  }
  
  private boolean isValidToken(String tokenId,String cacheToken) {
    return tokenId != null && cacheToken.contains(tokenId) ;
  }
  

  
  // Validation logic for OrderRequest
  
  private List<String> validateOrderRequest(Order request) {
	  List<String> errors = new ArrayList<>();
      if (StringUtils.isBlank(request.getRefId())) {
          errors.add("refId cannot be blank");
      }
      if (StringUtils.isBlank(request.getUserEmail())) {
          errors.add("user_email cannot be blank");
      }
      if (CollectionUtils.isEmpty(request.getItems())) {
          errors.add("items cannot be empty");
      } else {
          for (Item itemRequest : request.getItems()) {
              if (StringUtils.isBlank(itemRequest.getItemCode())) {
                  errors.add("item_code cannot be blank");
              }
              if (itemRequest.getQuantity() <= 0) {
                  errors.add("quantity must be greater than 0");
              }
          }
      }
      if (request.getCost() <= 0) {
          errors.add("cost must be greater than 0");
      }
      if (!errors.isEmpty()) {
          return errors;
      }
	return null;
      }


}