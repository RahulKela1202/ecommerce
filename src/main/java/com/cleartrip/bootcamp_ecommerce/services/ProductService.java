package com.cleartrip.bootcamp_ecommerce.services;

import com.cleartrip.bootcamp_ecommerce.models.Product;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {
    String create(Product product);

    Product update(Long id, Map<String, Object> updates);

    Product getById(Long id);

    Product delete(Long id);

    String incStock(Long id, int quantity);

    String decStock(Long id, int quantity);

    List<Map<String, Object>> getInventory();

    Page<Product> getAll(int page, int size);

    Page<Product> getFiltered(
        String category, 
        String name, 
        BigDecimal minPrice, 
        BigDecimal maxPrice,
        String sortBy,
        String sortDir,
        int page, 
        int size
    );


}
