package com.cleartrip.bootcamp_ecommerce.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private Long userId;
    private Double totalAmount;
    private String shippingAddress;
    private List<OrderItemRequest> orderItems;
}

