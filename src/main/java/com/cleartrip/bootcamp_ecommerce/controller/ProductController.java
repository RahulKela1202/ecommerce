package com.cleartrip.bootcamp_ecommerce.controller;

import com.cleartrip.bootcamp_ecommerce.dto.NewProduct;
import com.cleartrip.bootcamp_ecommerce.exception.UnauthorizedAccessException;
import com.cleartrip.bootcamp_ecommerce.models.Product;
import com.cleartrip.bootcamp_ecommerce.services.ProductService;
import com.cleartrip.bootcamp_ecommerce.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Product> getAllProducts(HttpServletRequest request,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "5") int size) throws UnauthorizedAccessException {
        if (!CookieUtils.isAdmin(request)) {
            throw new UnauthorizedAccessException("Access Denied");
        }
        return productService.getAllProducts(page,size);
    }

    @PostMapping("/add")
    public String createProduct(@RequestBody NewProduct newproduct, HttpServletRequest request) throws UnauthorizedAccessException {
        if (!CookieUtils.isAdmin(request)) {
            throw new UnauthorizedAccessException("Access Denied");
        }
        Product product = new Product();
        product.setName(newproduct.getName());
        product.setDescription(newproduct.getDescription());
        product.setPrice(newproduct.getPrice());
        product.setCategory(newproduct.getCategory());
        return productService.addNewProduct(product, newproduct.getStockQuantity());
    }

    @PatchMapping("/update/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> updates, HttpServletRequest request) throws UnauthorizedAccessException {
        if (!CookieUtils.isAdmin(request)) {
            throw new UnauthorizedAccessException("Access Denied");
        }
        return productService.updateProduct(id,updates);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id,HttpServletRequest request) throws UnauthorizedAccessException {
        if (!CookieUtils.isAdmin(request)) {
            throw new UnauthorizedAccessException("Access Denied");
        }
        return productService.deleteProduct(id);
    }

   @GetMapping("/name/{name}")
    public List<Product> getProductByName(@PathVariable String name,
                                          HttpServletRequest request,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "5") int size) throws UnauthorizedAccessException {
       if (!CookieUtils.isAdmin(request)) {
           throw new UnauthorizedAccessException("Access Denied");
       }
        return productService.searchProductsByName(name,page,size);
   }

   @GetMapping("/category/{category}")
    public List<Product> searchProductsByCategory(@PathVariable String category,
                                                  HttpServletRequest request,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "5") int size) throws UnauthorizedAccessException{
       if (!CookieUtils.isAdmin(request)) {
           throw new UnauthorizedAccessException("Access Denied");
       }
        return productService.searchProductsByCategory(category,page,size);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Product>> filterProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        List<Product> products = productService.getFilteredProducts(category, minPrice, maxPrice,page,size);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/sort")
    public ResponseEntity<List<Product>> sortProducts(
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        List<Product> sortedProducts = productService.getSortedProducts(sortBy, sortDirection,page,size);
        return ResponseEntity.ok(sortedProducts);
    }

}
