package com.cleartrip.bootcamp_ecommerce.controller;

import com.cleartrip.bootcamp_ecommerce.dto.ApiResponse;
import com.cleartrip.bootcamp_ecommerce.dto.NewProduct;
import com.cleartrip.bootcamp_ecommerce.exception.UnauthorizedAccessException;
import com.cleartrip.bootcamp_ecommerce.models.Product;
import com.cleartrip.bootcamp_ecommerce.services.ProductService;
import com.cleartrip.bootcamp_ecommerce.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>>  getAllProducts(HttpServletRequest request,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "5") int size)  {
        if (!CookieUtils.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error",null,"Access Denied"));
        }
        return ResponseEntity.ok(new ApiResponse<>("success",productService.getAllProducts(page,size),"Retrieved All Product"));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> createProduct(@RequestBody NewProduct newproduct, HttpServletRequest request) {
        if (!CookieUtils.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error",null,"Access Denied"));
        }
        Product product = new Product();
        product.setName(newproduct.getName());
        product.setDescription(newproduct.getDescription());
        product.setPrice(newproduct.getPrice());
        product.setCategory(newproduct.getCategory());

        return ResponseEntity.ok(new ApiResponse<>("success",productService.addNewProduct(product, newproduct.getStockQuantity()),"Created new Product"));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> updates, HttpServletRequest request) {
        if (!CookieUtils.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error",null,"Access Denied"));
        }
        return ResponseEntity.ok(new ApiResponse<>("success",productService.updateProduct(id,updates),"Updated Product"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id,HttpServletRequest request) {
        if (!CookieUtils.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error",null,"Access Denied"));
        }
        return ResponseEntity.ok(new ApiResponse<>("success",productService.deleteProduct(id),"Deleted Product"));
    }

   @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<List<Product>>>  getProductByName(@PathVariable String name,
                                          HttpServletRequest request,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "5") int size)  {
       if (!CookieUtils.isAdmin(request)) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error",null,"Access Denied"));
       }
        return ResponseEntity.ok(new ApiResponse<>("success",productService.searchProductsByName(name,page,size),"Retrieved product by name"));
   }

   @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Product>>> searchProductsByCategory(@PathVariable String category,
                                                  HttpServletRequest request,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "5") int size) {
       if (!CookieUtils.isAdmin(request)) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error",null,"Access Denied"));
       }
       return ResponseEntity.ok(new ApiResponse<>("success",productService.searchProductsByCategory(category,page,size),"Retrieved Products by category"));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<Product>>> filterProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        List<Product> products = productService.getFilteredProducts(category, minPrice, maxPrice,page,size);
        return ResponseEntity.ok(new ApiResponse<>("success",products,"Retrieved  Filtered Products"));
    }

    @GetMapping("/sort")
    public ResponseEntity<ApiResponse<List<Product>>> sortProducts(
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        List<Product> sortedProducts = productService.getSortedProducts(sortBy, sortDirection,page,size);
        return ResponseEntity.ok(new ApiResponse<>("success",sortedProducts,"Retrieved  sorted Products"));
    }

}
