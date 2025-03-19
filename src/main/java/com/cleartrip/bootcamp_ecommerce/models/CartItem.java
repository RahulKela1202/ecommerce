package com.cleartrip.bootcamp_ecommerce.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Long productId;
    private int quantity;
}
