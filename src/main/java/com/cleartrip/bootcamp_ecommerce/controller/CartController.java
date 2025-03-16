package com.cleartrip.bootcamp_ecommerce.controller;

import com.cleartrip.bootcamp_ecommerce.dto.ApiResponse;
import com.cleartrip.bootcamp_ecommerce.models.Cart;
import com.cleartrip.bootcamp_ecommerce.models.CartItem;
import com.cleartrip.bootcamp_ecommerce.models.Product;
import com.cleartrip.bootcamp_ecommerce.services.CartService;
import com.cleartrip.bootcamp_ecommerce.services.ProductService;
import com.cleartrip.bootcamp_ecommerce.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final ProductService productService;

    @Autowired
    public CartController(CartService cartService, ProductService productService){
        this.cartService = cartService;
        this.productService = productService;
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<ApiResponse<Cart>> addToCart(@PathVariable Long productId, @RequestParam int quantity, HttpServletRequest request) {
        Optional<Product> optionalProduct = productService.getProductById(productId);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("error", null, "Product not found"));
        }
        Long id  = CookieUtils.getUserId(request);
        Product product = optionalProduct.get();
        Cart cart = cartService.addToCart(id,product.getId(), quantity);
        return ResponseEntity.ok(new ApiResponse<>("success", cart, "Product added successfully"));
    }

    @DeleteMapping("/reduce/{productId}")
    public ResponseEntity<ApiResponse<Cart>> reduceFromCart(@PathVariable Long productId, HttpServletRequest request) {
        Long id  = CookieUtils.getUserId(request);
        try {
            Cart cart = cartService.reduceFromCart(id, productId);
            return ResponseEntity.ok(new ApiResponse<>("success",cart,"Product Quantity reduced"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("error",null,e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse<Cart>> removeFromCart(@PathVariable Long productId,HttpServletRequest request) {
        Long id  = CookieUtils.getUserId(request);
        Cart cart = cartService.removeFromCart(id,productId);
        return ResponseEntity.ok(new ApiResponse<>("success",cart,"Product removed"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItem>>> viewCart(HttpServletRequest request) {
        Long id  = CookieUtils.getUserId(request);
        return ResponseEntity.ok(new ApiResponse<>("success",cartService.getCartByUserId(id),"User Cart Retrieved"));
    }

    @GetMapping("/total")
    public ResponseEntity<ApiResponse<Double>> getTotalAmount(HttpServletRequest request) {
        Long id  = CookieUtils.getUserId(request);
        return ResponseEntity.ok(new ApiResponse<>("success",cartService.getTotalAmount(id),"Total Amount calculated"));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Cart>> clearCart(HttpServletRequest request) {
        Long id  = CookieUtils.getUserId(request);
        Cart cart = cartService.clearCart(id);
        return ResponseEntity.ok(new ApiResponse<>("success",cart,"Cart cleared"));
    }
}

