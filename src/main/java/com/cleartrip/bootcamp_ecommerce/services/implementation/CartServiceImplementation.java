package com.cleartrip.bootcamp_ecommerce.services.implementation;

import com.cleartrip.bootcamp_ecommerce.models.Cart;
import com.cleartrip.bootcamp_ecommerce.models.CartItem;
import com.cleartrip.bootcamp_ecommerce.models.Product;
import com.cleartrip.bootcamp_ecommerce.models.User;
import com.cleartrip.bootcamp_ecommerce.repository.CartItemRepository;
import com.cleartrip.bootcamp_ecommerce.repository.CartRepository;
import com.cleartrip.bootcamp_ecommerce.repository.ProductRepository;
import com.cleartrip.bootcamp_ecommerce.repository.UserRepository;
import com.cleartrip.bootcamp_ecommerce.services.CartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;


import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@SessionScope
public class CartServiceImplementation implements CartService {
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartServiceImplementation(UserRepository userRepository,
                                     CartRepository cartRepository,
                                     ProductRepository productRepository,
                                     CartItemRepository cartItemRepository){
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }


    @Override
    public List<CartItem> getCartByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = getCartByUser(user);
        return cart.getCartItems();
    }

    @Override
    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    @Override
    public Cart addToCart(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart cart = getCartByUser(user);
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem cartItem = new CartItem(cart, product, quantity);
            cart.getCartItems().add(cartItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart reduceFromCart(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean productExist = false;

        Cart cart = getCartByUser(user);

        Iterator<CartItem> iterator = cart.getCartItems().iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getProduct().getId().equals(productId)) {
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
            throw new RuntimeException("Product not found in cart");
        }
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = getCartByUser(user);
        cartItemRepository.deleteByCart(cart);
        return cart;
    }

    @Override
    public Cart removeFromCart(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = getCartByUser(user);
        cart.getCartItems().removeIf(item -> item.getProduct().getId().equals(productId));
        return cartRepository.save(cart);
    }

    @Override
    public double getTotalAmount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = getCartByUser(user);
        List<CartItem> cartItems = cart.getCartItems();
        return cartItems.stream().mapToDouble(item -> item.getProduct().getPrice().doubleValue() * item.getQuantity()).sum();
    }
}

