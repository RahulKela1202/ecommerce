package com.cleartrip.bootcamp_ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewProduct {
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private int stockQuantity;
}
