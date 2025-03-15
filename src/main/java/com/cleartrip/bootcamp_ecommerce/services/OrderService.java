package com.cleartrip.bootcamp_ecommerce.services;


import com.cleartrip.bootcamp_ecommerce.dto.OrderRequest;
import com.cleartrip.bootcamp_ecommerce.models.Order;
import com.cleartrip.bootcamp_ecommerce.models.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order checkout(Long userId, String shippingAddress);
    Order createOrder(OrderRequest orderRequest);
    List<Order> getAllOrder();
    Optional<Order> getOrderById(Long id);
    List<Order> getOrderByUserId(Long id);
    void updateOrderStatus(Long orderId, OrderStatus status);
}
