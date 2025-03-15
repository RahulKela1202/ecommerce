package com.cleartrip.bootcamp_ecommerce.services;

import com.cleartrip.bootcamp_ecommerce.models.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {
    String addNewProduct(Product product, int stockQuantity);
    Product updateProduct(Long id, Map<String, Object> updates);
    String deleteProduct(Long id);
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    List<Product> searchProductsByName(String name);
    List<Product> searchProductsByCategory(String category);
    List<Product> getFilteredProducts(String category, BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> getSortedProducts(String sortBy, String sortDirection);
}
