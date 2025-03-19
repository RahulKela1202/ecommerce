package com.cleartrip.bootcamp_ecommerce.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItems {
    @JsonProperty("id")
    private Long productId;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("quantity")
    private int quantity;
    
    @JsonProperty("price")
    private BigDecimal price;

}

