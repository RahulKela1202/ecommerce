package com.cleartrip.bootcamp_ecommerce.services.implementation;

import com.cleartrip.bootcamp_ecommerce.models.Inventory;
import com.cleartrip.bootcamp_ecommerce.models.Product;
import com.cleartrip.bootcamp_ecommerce.repository.CartItemRepository;
import com.cleartrip.bootcamp_ecommerce.repository.InventoryRepository;
import com.cleartrip.bootcamp_ecommerce.repository.OrderItemsRepository;
import com.cleartrip.bootcamp_ecommerce.repository.ProductRepository;
import com.cleartrip.bootcamp_ecommerce.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImplementation implements ProductService {
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemsRepository orderItemsRepository;

    @Autowired
    public ProductServiceImplementation(ProductRepository productRepository,
                                        InventoryRepository inventoryRepository,
                                        CartItemRepository cartItemRepository,
                                        OrderItemsRepository orderItemsRepository){
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderItemsRepository = orderItemsRepository;
    }

    @Override
    public String addNewProduct(Product product, int stockQuantity){
        try {
            Product savedProduct = productRepository.save(product);
            Inventory inventory = new Inventory();
            inventory.setProduct(savedProduct);
            inventory.setStockQuantity(stockQuantity);
            inventoryRepository.save(inventory);
            return "Product registered successfully!";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public List<Product> getSortedProducts(String sortBy, String sortDirection,int page,int size) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        return productRepository.findAll(sort).stream()
                .skip((long) page * size)  // Skip previous pages
                .limit(size)               // Limit results to page size
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getFilteredProducts(String category, BigDecimal minPrice, BigDecimal maxPrice,int page,int size) {
        return productRepository.findByFilters(category, minPrice, maxPrice).stream()
                .skip((long) page * size)  // Skip previous pages
                .limit(size)               // Limit results to page size
                .collect(Collectors.toList());
    }

//    @Override
//    public Product updateProduct(Long id, Product updatedProduct) {
//        Product existingProduct = productRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//
//        if(updatedProduct.getName() != null) {
//            existingProduct.setName(updatedProduct.getName());
//        }
//        if(updatedProduct.getDescription() != null){
//            existingProduct.setDescription(updatedProduct.getDescription());
//        }
//        if(updatedProduct.getPrice() != null) {
//            existingProduct.setPrice(updatedProduct.getPrice());
//        }
//        if(updatedProduct.getCategory() != null) {
//            existingProduct.setCategory(updatedProduct.getCategory());
//        }
//        return productRepository.save(existingProduct);
//    }

    @Override
    public Product updateProduct(Long id, Map<String, Object> updates) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> product.setName((String) value);
                case "description" -> product.setDescription((String) value);
                case "price" -> product.setPrice(new BigDecimal(value.toString())); // Ensure BigDecimal conversion
                case "category" -> product.setCategory((String) value);
            }
        });

        return productRepository.save(product);
    }



    @Override
    public String deleteProduct(Long id) {
        if (cartItemRepository.existsByProductId(id)) {
            return "Cannot delete: Product is present in a user's cart.";
        }

        if (orderItemsRepository.existsByProductId(id)) {
            return "Cannot delete: Product has been ordered before.";
        }

        try {
            productRepository.deleteById(id);
            return "Product deleted successfully.";
        } catch (Exception e) {
            return "Error occurred while deleting the product.";
        }
    }


    @Override
    public List<Product> getAllProducts(int page,int size) {
        return productRepository.findAll().stream()
                .skip((long) page * size)  // Skip previous pages
                .limit(size)               // Limit results to page size
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> searchProductsByName(String name,int page,int size) {
        return productRepository.findByName(name).stream()
                .skip((long) page * size)  // Skip previous pages
                .limit(size)               // Limit results to page size
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> searchProductsByCategory(String category,int page,int size) {
        return productRepository.findByCategory(category).stream()
                .skip((long) page * size)  // Skip previous pages
                .limit(size)               // Limit results to page size
                .collect(Collectors.toList());
    }

}
