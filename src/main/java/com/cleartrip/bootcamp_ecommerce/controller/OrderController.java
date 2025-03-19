package com.cleartrip.bootcamp_ecommerce.controller;

import com.cleartrip.bootcamp_ecommerce.dto.ApiResponse;
import com.cleartrip.bootcamp_ecommerce.dto.CartRequest;
import com.cleartrip.bootcamp_ecommerce.dto.OrderRequest;
import com.cleartrip.bootcamp_ecommerce.models.Order;
import com.cleartrip.bootcamp_ecommerce.models.OrderStatus;
import com.cleartrip.bootcamp_ecommerce.services.OrderService;
import com.cleartrip.bootcamp_ecommerce.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping("/new")
    public ResponseEntity<ApiResponse<Order>>  createNewOrder(@RequestBody OrderRequest order){
        return ResponseEntity.ok(new ApiResponse<>("success",orderService.create(order),"Created New Order"));
    }

    @PostMapping("/cart/checkout")
    public ResponseEntity<ApiResponse<Order>> cartCheckout(@RequestBody CartRequest cartRequest, HttpServletRequest request){
        HttpSession session = request.getSession(false);
        Long id = (Long) session.getAttribute("userId");
        return ResponseEntity.ok(new ApiResponse<>("success",orderService.checkout(id,cartRequest.getShippingAddress()),"checked out cart"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrder(){
        return ResponseEntity.ok(new ApiResponse<>("success",orderService.getAll(),"Orders Retrieved"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long id){
        return ResponseEntity.ok(new ApiResponse<>("success",orderService.getById(id),"Orders Retrieved"));
    }

    @GetMapping("user/{id}")
    public ResponseEntity<ApiResponse<List<Order>>> getOrderByUserId(@PathVariable Long id){
        return ResponseEntity.ok(new ApiResponse<>("success",orderService.getByUserId(id),"Orders Retrieved"));
    }

    @PatchMapping("/{orderId}/update-status")
    public ResponseEntity<ApiResponse<String>> updateOrderStatus(@PathVariable Long orderId,
                                                    @RequestParam OrderStatus status,
                                                    HttpServletRequest request)
          {
        if (!CookieUtils.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error",null,"Access Denied"));
        }

        orderService.updateStatus(orderId, status);
        return ResponseEntity.ok(new ApiResponse<>("success","Order status updated to " + status,"Updated Order Status"));
    }
}
