package com.cleartrip.bootcamp_ecommerce.controller;


import com.cleartrip.bootcamp_ecommerce.dto.StockUpdate;
import com.cleartrip.bootcamp_ecommerce.exception.UnauthorizedAccessException;
import com.cleartrip.bootcamp_ecommerce.models.Inventory;
import com.cleartrip.bootcamp_ecommerce.services.InventoryService;
import com.cleartrip.bootcamp_ecommerce.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService){
        this.inventoryService = inventoryService;
    }

    @PatchMapping("/inc")
    public String incStock(@RequestBody StockUpdate stockUpdate, HttpServletRequest request) throws UnauthorizedAccessException {
        if (!CookieUtils.isAdmin(request)) {
            throw new UnauthorizedAccessException("Access Denied");
        }
        return inventoryService.incStock(stockUpdate.getId(),stockUpdate.getQuantity());
    }

    @PatchMapping("/dec")
    public String decStock(@RequestBody StockUpdate stockUpdate,HttpServletRequest request) throws UnauthorizedAccessException {
        if (!CookieUtils.isAdmin(request)) {
            throw new UnauthorizedAccessException("Access Denied");
        }
        return inventoryService.decStock(stockUpdate.getId(),stockUpdate.getQuantity());
    }

    @PatchMapping("/update")
    public String updateStock(@RequestBody StockUpdate stockUpdate,HttpServletRequest request) throws UnauthorizedAccessException {
        if (!CookieUtils.isAdmin(request)) {
            throw new UnauthorizedAccessException("Access Denied");
        }
        return inventoryService.updateStock(stockUpdate.getId(),stockUpdate.getQuantity());
    }

    @GetMapping
    public List<Inventory> getInvetory(HttpServletRequest request) throws UnauthorizedAccessException {
        if (!CookieUtils.isAdmin(request)) {
            throw new UnauthorizedAccessException("Access Denied");
        }
        return inventoryService.getInvetoryDetails();
    }
}
