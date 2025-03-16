package com.cleartrip.bootcamp_ecommerce.controller;

import com.cleartrip.bootcamp_ecommerce.dto.ApiResponse;
import com.cleartrip.bootcamp_ecommerce.models.Cart;
import com.cleartrip.bootcamp_ecommerce.models.CartItem;
import com.cleartrip.bootcamp_ecommerce.models.Product;
import com.cleartrip.bootcamp_ecommerce.services.CartService;
import com.cleartrip.bootcamp_ecommerce.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
        if (!optionalProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("error", null, "Product not found"));
        }
        HttpSession session = request.getSession(false);
        Long id = (Long) session.getAttribute("userId");
        Product product = optionalProduct.get();
        Cart cart = cartService.addToCart(id,product.getId(), quantity);
        return ResponseEntity.ok(new ApiResponse<>("success", cart, "Product added successfully"));
    }

    @DeleteMapping("/reduce/{productId}")
    public ResponseEntity<ApiResponse<Cart>> reduceFromCart(@PathVariable Long productId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long id = (Long) session.getAttribute("userId");
        try {
            Cart cart = cartService.reduceFromCart(id, productId);
            return ResponseEntity.ok(new ApiResponse<>("success",cart,"Product Quantity reduced"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("error",null,e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse<Cart>> removeFromCart(@PathVariable Long productId,HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long id = (Long) session.getAttribute("userId");
        Cart cart = cartService.removeFromCart(id,productId);
        return ResponseEntity.ok(new ApiResponse<>("success",cart,"Product removed"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItem>>> viewCart(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long id = (Long) session.getAttribute("userId");
        return ResponseEntity.ok(new ApiResponse<>("success",cartService.getCartByUserId(id),"User Cart Retrieved"));
    }

    @GetMapping("/total")
    public ResponseEntity<ApiResponse<Double>> getTotalAmount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long id = (Long) session.getAttribute("userId");
        return ResponseEntity.ok(new ApiResponse<>("success",cartService.getTotalAmount(id),"Total Amount calculated"));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Cart>> clearCart(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long id = (Long) session.getAttribute("userId");
        Cart cart = cartService.clearCart(id);
        return ResponseEntity.ok(new ApiResponse<>("success",cart,"Cart cleared"));
    }
}

