package com.cleartrip.bootcamp_ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockUpdate {
    private Long id;
    private int quantity;
}
