package com.cleartrip.bootcamp_ecommerce.services;


import com.cleartrip.bootcamp_ecommerce.dto.OrderRequest;
import com.cleartrip.bootcamp_ecommerce.models.Order;
import com.cleartrip.bootcamp_ecommerce.models.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order checkout(Long userId, String shippingAddress);
    List<Order> getAll();
    Order getById(Long id);
    List<Order> getByUserId(Long id);
    Order create(OrderRequest orderRequest);
    Order updateStatus(Long id, OrderStatus status);
}
