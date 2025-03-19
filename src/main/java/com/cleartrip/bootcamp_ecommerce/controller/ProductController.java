package com.cleartrip.bootcamp_ecommerce.controller;

import com.cleartrip.bootcamp_ecommerce.dto.ApiResponse;
import com.cleartrip.bootcamp_ecommerce.models.Product;
import com.cleartrip.bootcamp_ecommerce.services.ProductService;
import com.cleartrip.bootcamp_ecommerce.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getInventory(HttpServletRequest request){
        if (!CookieUtils.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error",null,"Access Denied"));
        }
        return ResponseEntity.ok(new ApiResponse<>("success",productService.getInventory(),"Retrieved All Product"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getById(HttpServletRequest request,@PathVariable Long id){
        if (!CookieUtils.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error",null,"Access Denied"));
        }
        return ResponseEntity.ok(new ApiResponse<>("success",productService.getById(id),"Retrieved All Product"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Product>>>  getAllProducts(HttpServletRequest request,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "5") int size)  {
        if (!CookieUtils.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error",null,"Access Denied"));
        }
        return ResponseEntity.ok(new ApiResponse<>("success",productService.getAll(page,size),"Retrieved All Product"));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody Product product, HttpServletRequest request) {
        if (!CookieUtils.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error",null,"Access Denied"));
        }
        productService.create(product);
        return ResponseEntity.ok(new ApiResponse<>("success",product,"Created new Product"));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> updates, HttpServletRequest request) {
        if (!CookieUtils.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error",null,"Access Denied"));
        }
        return ResponseEntity.ok(new ApiResponse<>("success",productService.update(id,updates),"Updated Product"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> deleteProduct(@PathVariable Long id,HttpServletRequest request) {
        if (!CookieUtils.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error",null,"Access Denied"));
        }
        return ResponseEntity.ok(new ApiResponse<>("success",productService.delete(id),"Deleted Product"));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<Product>>> getFiltered(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Product> products = productService.getFiltered(
                category,
                name,
                minPrice,
                maxPrice,
                sortBy,
                sortDir,
                page,
                size
        );
        return ResponseEntity.ok(new ApiResponse<>("success", products, "Products retrieved successfully"));
    }

}
