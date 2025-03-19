package com.cleartrip.bootcamp_ecommerce.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @JsonIgnore
    @Column(name = "items", columnDefinition = "json")
    private String itemsJson;

    @Transient
    private List<OrderItems> orderItems = new ArrayList<>();

    @Column(name = "amount", nullable = false)
    private double totalAmount;

    @Column(name = "address", nullable = false)
    private String shippingAddress;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public Order(Long userId, List<OrderItems> orderItems, double totalAmount, String shippingAddress, OrderStatus orderStatus) {
        this.userId = userId;
        this.orderItems = orderItems;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.status = orderStatus;
    }


    public List<OrderItems> getOrderItems() {
        if (orderItems.isEmpty() && itemsJson != null) {
            try {
                orderItems = objectMapper.readValue(itemsJson, new TypeReference<List<OrderItems>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error deserializing order items", e);
            }
        }
        return orderItems;
    }

    public void setOrderItems(List<OrderItems> items) {
        this.orderItems = items;
        try {
            this.itemsJson = objectMapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing order items", e);
        }
    }
}

