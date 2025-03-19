package com.cleartrip.bootcamp_ecommerce.services.implementation;

import com.cleartrip.bootcamp_ecommerce.exception.InvalidRequestException;
import com.cleartrip.bootcamp_ecommerce.exception.NotFoundException;
import com.cleartrip.bootcamp_ecommerce.models.Product;
import com.cleartrip.bootcamp_ecommerce.repository.ProductRepository;
import com.cleartrip.bootcamp_ecommerce.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ProductServiceImplementation implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public String create(Product product) {
        if (product.getQuantity() < 0) {  
            throw new InvalidRequestException("Quantity cannot be negative");
        }
        productRepository.save(product);
        return "Product created successfully";
    }

    @Override
    public Page<Product> getFiltered(
            String category, 
            String name, 
            BigDecimal minPrice, 
            BigDecimal maxPrice,
            String sortBy,
            String sortDir,
            int page, 
            int size) {
            
        sortBy = (sortBy == null || sortBy.isEmpty()) ? "id" : sortBy;
        sortDir = (sortDir == null || sortDir.isEmpty()) ? "asc" : sortDir.toLowerCase();
        
        Sort sort = sortDir.equals("asc") ? 
            Sort.by(sortBy).ascending() : 
            Sort.by(sortBy).descending();

        return productRepository.findByFilters(
            category, 
            name, 
            minPrice, 
            maxPrice, 
            PageRequest.of(page, size, sort)
        );
    }


    @Override
    public Product update(Long id, Map<String, Object> updates) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));

        updates.forEach((key, value) -> {
                switch (key) {
                    case "name" -> product.setName((String) value);
                    case "description" -> product.setDescription((String) value);
                    case "price" -> product.setPrice(new BigDecimal(value.toString()));
                    case "category" -> product.setCategory((String) value);
                    case "quantity" -> product.setQuantity((Integer) value);
                    default -> throw new InvalidRequestException("Invalid field: " + key);
                }
        });
        return productRepository.save(product);
    }

    @Override
    public Product delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        productRepository.delete(product);
        return product;
    }


    @Override
    public Page<Product> getAll(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

    @Override
    public List<Map<String, Object>> getInventory() {
        List<Product> products = productRepository.findAll();
        return products.stream()
            .map(product -> {
                Map<String, Object> inventory = new HashMap<>();
                inventory.put("id", product.getId());
                inventory.put("name", product.getName());
                inventory.put("quantity", product.getQuantity());
                return inventory;
            })
            .collect(Collectors.toList());
    }

    @Override
    public String incStock(Long id, int quantity) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product not found"));
            
        int newQuantity = product.getQuantity() + quantity;
        product.setQuantity(newQuantity);
        productRepository.save(product);
        return "Stock increased successfully";
    }

     @Override
    public String decStock(Long id, int quantity) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product not found"));
            
        int newQuantity = product.getQuantity() - quantity;
        if (newQuantity < 0) {
            throw new InvalidRequestException("Insufficient stock");
        }
        product.setQuantity(newQuantity);
        productRepository.save(product);
        return "Stock decreased successfully";
    }


}
