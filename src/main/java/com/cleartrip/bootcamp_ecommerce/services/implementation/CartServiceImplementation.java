package com.cleartrip.bootcamp_ecommerce.services.implementation;

import com.cleartrip.bootcamp_ecommerce.exception.InvalidRequestException;
import com.cleartrip.bootcamp_ecommerce.exception.NotFoundException;
import com.cleartrip.bootcamp_ecommerce.models.Cart;
import com.cleartrip.bootcamp_ecommerce.models.CartItem;
import com.cleartrip.bootcamp_ecommerce.models.Product;
import com.cleartrip.bootcamp_ecommerce.repository.CartRepository;
import com.cleartrip.bootcamp_ecommerce.repository.ProductRepository;
import com.cleartrip.bootcamp_ecommerce.repository.UserRepository;
import com.cleartrip.bootcamp_ecommerce.services.CartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@SessionScope
public class CartServiceImplementation implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public CartServiceImplementation(ProductRepository productRepository,
                                     CartRepository cartRepository,
                                     UserRepository userRepository){
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }


    @Override
    public Cart getByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userRepository.findById(userId)
                            .orElseThrow(() -> new NotFoundException("User not found: " + userId)).getId());
                    newCart.setCartItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });
    }

    @Override
    public Cart addItem(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new InvalidRequestException("Quantity must be positive");
        }
        Cart cart = getByUserId(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(product.getId()))
                .findFirst();

        List<CartItem> items = cart.getCartItems();
        boolean found = false;

        for (CartItem item : items) {
            if (item.getProductId().equals(productId)) {
                int newQuantity = item.getQuantity() + quantity;
                if (newQuantity > product.getQuantity()) {
                    throw new InvalidRequestException("Requested quantity exceeds available stock");
                }
                item.setQuantity(newQuantity);
                found = true;
                break;
            }
        }

        if (!found) {
            if (quantity > product.getQuantity()) {
                throw new InvalidRequestException("Requested quantity exceeds available stock");
            }
            CartItem newItem = new CartItem(productId, quantity);
            items.add(newItem);
        }
        cart.setItems(items);
        cart.setTotalAmount(getTotalAmount(userId));
        return cartRepository.save(cart);
    }

    @Override
    public Cart reduceItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found for user: " + userId));

        boolean productExist = false;

        Iterator<CartItem> iterator = cart.getCartItems().iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getProductId().equals(productId)) {
                productExist = true;
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    iterator.remove();
                }
                break;
            }
        }
        if(!productExist){
            throw new NotFoundException("Product not found in cart");
        }
        cart.setItems(cart.getCartItems());
        cart.setTotalAmount(getTotalAmount(userId));
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart clear(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found for user: " + userId));

        cart.getCartItems().clear();
        cart.setItemsJson("[]");
        cart.setTotalAmount(getTotalAmount(userId));
        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found for user: " + userId));
        cart.getCartItems().removeIf(item -> item.getProductId().equals(productId));
        cart.setItems(cart.getCartItems());
        cart.setTotalAmount(getTotalAmount(userId));
        return cartRepository.save(cart);
    }

  
    public double getTotalAmount(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found for user: " + userId));
        List<CartItem> cartItems = cart.getCartItems();
        double total = 0;

        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));
            total += product.getPrice().doubleValue() * item.getQuantity();
        }

        return total;
    }
}

