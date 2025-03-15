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
    List<Product> getAllProducts(int page,int size);
    Optional<Product> getProductById(Long id);
    List<Product> searchProductsByName(String name,int page,int size);
    List<Product> searchProductsByCategory(String category,int page,int size);
    List<Product> getFilteredProducts(String category, BigDecimal minPrice, BigDecimal maxPrice,int page,int size);
    List<Product> getSortedProducts(String sortBy, String sortDirection,int page,int size);
}
