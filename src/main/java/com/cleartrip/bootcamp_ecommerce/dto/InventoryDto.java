package com.cleartrip.bootcamp_ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDto {
    private String productName;
    private int stockQuantity;
}
