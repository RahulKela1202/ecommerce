package com.cleartrip.bootcamp_ecommerce.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @JsonIgnore
    @Column(name = "items", columnDefinition = "json")
    private String itemsJson;

    @Column(name = "amount")
    private double totalAmount;

    @Setter
    @Transient
    private List<CartItem> cartItems = new ArrayList<>();


    public List<CartItem> getCartItems() {
        if (cartItems.isEmpty()) {
            try {
                if (itemsJson == null || itemsJson.isEmpty()) {
                    itemsJson = "[]";
                }
                cartItems = objectMapper.readValue(itemsJson, new TypeReference<List<CartItem>>() {});
            } catch (JsonProcessingException e) {
                cartItems = new ArrayList<>();
            }
        }
        return cartItems;
    }

    public void setItems(List<CartItem> items) {
        try {
            this.cartItems = items;
            System.out.println("Reached");
            this.itemsJson = objectMapper.writeValueAsString(items != null ? items : new ArrayList<>());
            System.out.println(this.itemsJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing cart items", e);
        }
    }
}

