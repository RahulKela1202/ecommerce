package com.cleartrip.bootcamp_ecommerce.services;

import com.cleartrip.bootcamp_ecommerce.dto.InventoryDto;
import com.cleartrip.bootcamp_ecommerce.models.Inventory;

import java.util.List;

public interface InventoryService {
    String incStock(Long id, int quantity);
    String decStock(Long id, int quantity);
    String updateStock(Long id,int quantity);
    List<Inventory> getInvetoryDetails();
}
