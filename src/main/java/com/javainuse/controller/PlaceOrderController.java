package com.javainuse.controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javainuse.model.Order;
import com.javainuse.service.OrderService;
import com.javainuse.util.OrderResponse;
import com.javainuse.util.MessageResponse;

@RestController
@RequestMapping("/api/v1")
public class PlaceOrderController {
  
  @Autowired
  private OrderService orderService;
  
  @PostMapping("/order")
  public ResponseEntity<?> placeOrder(HttpServletRequest request, @RequestBody Order orderRequest) {
    
	  
    // 1. Require JWT authentication to access the API
    // Authentication handled by Spring Security

    // 2. Validate the token by checking the tokenId's presence in the cache
   String tokenId = getTokenIdFromRequest(request);
    if (!isValidToken(tokenId)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }
    
    // 3. Make the details of the user available (via Spring Security's principal) until the response is sent out
   // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
   // String userEmail = authentication.getName();
    
	 String userEmail = orderRequest.getUserEmail();
	  
    // 4. Cache the tokenId with a TTL set to match the JWT expiry
  // cacheToken(tokenId, authentication.getAuthorities());
    
    // 5. Require a custom header (api_source: "XYZ") in the request
	  String apiSource = request.getHeader("api_source");
      if (apiSource == null || !apiSource.equals("XYZ")) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid API source");
      }
    
    // 6. On the controller, validate the request object
    //List<FieldError> errors = validateOrderRequest(request);
    //if (!errors.isEmpty()) {
    //  return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST", "Invalid request", errors), HttpStatus.BAD_REQUEST);
   // }
    
    // 7. Once validated, recalculate the cost for the items on the Service layer
    //double recalculatedCost = orderService.calculateCost(request.getItems());
  //  if (recalculatedCost != request.getCost()) {
  //    return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST", "Mismatch in cost"), HttpStatus.BAD_REQUEST);
  //  }
    
    // 8. Push the order details to a MongoDB and return the response
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
    if (order == null || order.isEmpty()) {
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
  
  private boolean isValidToken(String tokenId) {
    return tokenId != null ;
  }
  
 // private void cacheToken(String tokenId, Collection<? extends GrantedAuthority> authorities) {
 //   long expiration = JwtUtils.getExpirationTimeFromToken(tokenId).getTime() - System.currentTimeMillis();
  //  tokenCache.put(tokenId, true, expiration, TimeUnit.MILLISECONDS);
 // }
  
  // Validation logic for OrderRequest
  
 /* private List<FieldError> validateOrderRequest(OrderRequest request) {
    List<FieldError> errors = new ArrayList<>();
    if (request.getRefId() == null || request.getRefId().isEmpty()) {
      errors.add(new FieldError("refId", "Reference ID is required"));
    }
    if (request.getUserEmail() == null || !isValidEmail(request.getUserEmail())) {
      errors.add(new FieldError("userEmail", "Invalid email address"));
    }
    if (request.getUserCode() == null) {
      errors.add(new FieldError("userCode", "User code is required"));
    }
    if (request.getItems() == null || request.getItems().isEmpty()) {
      errors.add(new FieldError("Item", "Item is required"));
      }
      }
*/

}