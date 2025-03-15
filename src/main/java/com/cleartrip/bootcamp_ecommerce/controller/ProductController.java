package com.cleartrip.bootcamp_ecommerce.controller;

import com.cleartrip.bootcamp_ecommerce.dto.NewProduct;
import com.cleartrip.bootcamp_ecommerce.exception.UnauthorizedAccessException;
import com.cleartrip.bootcamp_ecommerce.models.Product;
import com.cleartrip.bootcamp_ecommerce.services.ProductService;
import com.cleartrip.bootcamp_ecommerce.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    public List<Product> getAllProducts(HttpServletRequest request) throws UnauthorizedAccessException {
        if (!CookieUtils.isAdmin(request)) {
            throw new UnauthorizedAccessException("Access Denied");
        }
        return productService.getAllProducts();
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
    public List<Product> getProductByName(@PathVariable String name,HttpServletRequest request) throws UnauthorizedAccessException {
       if (!CookieUtils.isAdmin(request)) {
           throw new UnauthorizedAccessException("Access Denied");
       }
        return productService.searchProductsByName(name);
   }

   @GetMapping("/category/{category}")
    public List<Product> searchProductsByCategory(@PathVariable String category,HttpServletRequest request) throws UnauthorizedAccessException{
       if (!CookieUtils.isAdmin(request)) {
           throw new UnauthorizedAccessException("Access Denied");
       }
        return productService.searchProductsByCategory(category);
    }

}
