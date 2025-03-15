package com.cleartrip.bootcamp_ecommerce.controller;

import com.cleartrip.bootcamp_ecommerce.dto.CartRequest;
import com.cleartrip.bootcamp_ecommerce.dto.OrderRequest;
import com.cleartrip.bootcamp_ecommerce.models.Order;
import com.cleartrip.bootcamp_ecommerce.services.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping("/new")
    public Order createNewOrder(@RequestBody OrderRequest order){
        return orderService.createOrder(order);
    }

    @PostMapping("/cart/checkout")
    public Order cartCheckout(@RequestBody CartRequest cartRequest, HttpServletRequest request){
        HttpSession session = request.getSession(false);
        Long id = (Long) session.getAttribute("userId");
        return orderService.checkout(id,cartRequest.getShippingAddress());
    }

    @GetMapping
    public List<Order> getAllOrder(){
        return orderService.getAllOrder();
    }

    @GetMapping("/{id}")
    public Optional<Order> getOrderById(@PathVariable Long id){
        return orderService.getOrderById(id);
    }

    @GetMapping("user/{id}")
    public List<Order> getOrderByUserId(@PathVariable Long id){
        return orderService.getOrderByUserId(id);
    }
}
