package com.cleartrip.bootcamp_ecommerce.services.implementation;

import com.cleartrip.bootcamp_ecommerce.dto.InventoryDto;
import com.cleartrip.bootcamp_ecommerce.models.Inventory;
import com.cleartrip.bootcamp_ecommerce.models.Product;
import com.cleartrip.bootcamp_ecommerce.repository.InventoryRepository;
import com.cleartrip.bootcamp_ecommerce.repository.ProductRepository;
import com.cleartrip.bootcamp_ecommerce.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImplementation implements InventoryService {
    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryServiceImplementation(InventoryRepository inventoryRepository){
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public String incStock(Long id, int quantity){
        try{
            Optional<Inventory> optionalInventory = inventoryRepository.findByProductId(id);
            if(optionalInventory.isEmpty()){
                return "No product Found";
            }
            Inventory inventory = optionalInventory.get();
            inventory.setStockQuantity(inventory.getStockQuantity() + quantity);
            inventoryRepository.save(inventory);
            return "Increased successfully";
        } catch (Exception e) {
            return "Error";
        }
    }

    @Override
    public String decStock(Long id,int quantity){
        try{
            Optional<Inventory> optionalInventory = inventoryRepository.findByProductId(id);
            if(optionalInventory.isEmpty()){
                return "No product Found";
            }
            Inventory inventory = optionalInventory.get();
            inventory.setStockQuantity(inventory.getStockQuantity() - quantity);
            inventoryRepository.save(inventory);
            return "Decreased successfully";
        } catch (Exception e) {
            return "Error";
        }
    }

    @Override
    public String updateStock(Long id,int quantity){
        try{
            Optional<Inventory> optionalInventory = inventoryRepository.findByProductId(id);
            if(optionalInventory.isEmpty()){
                return "No product Found";
            }
            Inventory inventory = optionalInventory.get();
            inventory.setStockQuantity(quantity);
            inventoryRepository.save(inventory);
            return "Decreased successfully";
        } catch (Exception e) {
            return "Error";
        }
    }
    @Override
    public List<Inventory> getInvetoryDetails() {
        return inventoryRepository.findAll();
    }
}
