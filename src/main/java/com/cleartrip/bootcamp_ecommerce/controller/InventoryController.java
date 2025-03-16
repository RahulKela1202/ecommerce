package com.cleartrip.bootcamp_ecommerce.controller;


import com.cleartrip.bootcamp_ecommerce.dto.ApiResponse;
import com.cleartrip.bootcamp_ecommerce.dto.StockUpdate;
import com.cleartrip.bootcamp_ecommerce.models.Inventory;
import com.cleartrip.bootcamp_ecommerce.services.InventoryService;
import com.cleartrip.bootcamp_ecommerce.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<String>> incStock(@RequestBody StockUpdate stockUpdate, HttpServletRequest request) {
        if (!CookieUtils.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error",null,"Access Denied"));
        }
        return ResponseEntity.ok(new ApiResponse<>("success",inventoryService.incStock(stockUpdate.getId(),stockUpdate.getQuantity()),"Updated Stock"));
    }

    @PatchMapping("/dec")
    public ResponseEntity<ApiResponse<String>> decStock(@RequestBody StockUpdate stockUpdate,HttpServletRequest request) {
        if (!CookieUtils.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error",null,"Access Denied"));
        }
        return ResponseEntity.ok(new ApiResponse<>("success",inventoryService.decStock(stockUpdate.getId(),stockUpdate.getQuantity()),"Updated Stock"));
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateStock(@RequestBody StockUpdate stockUpdate,HttpServletRequest request)  {
        if (!CookieUtils.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error",null,"Access Denied"));
        }
        return ResponseEntity.ok(new ApiResponse<>("success",inventoryService.updateStock(stockUpdate.getId(),stockUpdate.getQuantity()),"Updated Stock"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Inventory>>> getInvetory(HttpServletRequest request) {
        if (!CookieUtils.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error",null,"Access Denied"));
        }
        return  ResponseEntity.ok(new ApiResponse<>("success",inventoryService.getInventoryDetails(),"Inventory Retrieved"));
    }
}
