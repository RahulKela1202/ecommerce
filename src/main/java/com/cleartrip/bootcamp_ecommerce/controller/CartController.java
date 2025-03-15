package com.cleartrip.bootcamp_ecommerce.controller;

import com.cleartrip.bootcamp_ecommerce.models.CartItem;
import com.cleartrip.bootcamp_ecommerce.models.Product;
import com.cleartrip.bootcamp_ecommerce.services.CartService;
import com.cleartrip.bootcamp_ecommerce.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<String> addToCart(@PathVariable Long productId, @RequestParam int quantity, HttpServletRequest request) {
        Optional<Product> optionalProduct = productService.getProductById(productId);
        if (!optionalProduct.isPresent()) {
            return ResponseEntity.badRequest().body("Product not found");
        }
        HttpSession session = request.getSession(false);
        Long id = (Long) session.getAttribute("userId");
        Product product = optionalProduct.get();
        cartService.addToCart(id,product.getId(), quantity);
        return ResponseEntity.ok("Product added to cart");
    }

    @DeleteMapping("/reduce/{productId}")
    public ResponseEntity<String> reduceFromCart(@PathVariable Long productId,HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long id = (Long) session.getAttribute("userId");
        cartService.reduceFromCart(id,productId);
        return ResponseEntity.ok("Product quantity reduced from cart");
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long productId,HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long id = (Long) session.getAttribute("userId");
        cartService.removeFromCart(id,productId);
        return ResponseEntity.ok("Product removed from cart");
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> viewCart(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long id = (Long) session.getAttribute("userId");
        return ResponseEntity.ok(cartService.getCartByUserId(id));
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getTotalAmount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long id = (Long) session.getAttribute("userId");
        return ResponseEntity.ok(cartService.getTotalAmount(id));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long id = (Long) session.getAttribute("userId");
        cartService.clearCart(id);
        return ResponseEntity.ok("Cart cleared");
    }
}

