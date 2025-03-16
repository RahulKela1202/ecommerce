package com.cleartrip.bootcamp_ecommerce.services;

import com.cleartrip.bootcamp_ecommerce.models.Cart;
import com.cleartrip.bootcamp_ecommerce.models.CartItem;
import com.cleartrip.bootcamp_ecommerce.models.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface CartService {
    List<CartItem> getCartByUserId(Long userId);
    Cart addToCart(Long userId, Long productId, int quantity);
    Cart removeFromCart(Long userId, Long productId);
    Cart reduceFromCart(Long userId,Long productId);
    Cart clearCart(Long UserId);
    double getTotalAmount(Long UserId);
    Cart getCartByUser(User user);
}
