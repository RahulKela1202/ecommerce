package com.cleartrip.bootcamp_ecommerce.services;

import com.cleartrip.bootcamp_ecommerce.models.Cart;
import com.cleartrip.bootcamp_ecommerce.models.CartItem;
import com.cleartrip.bootcamp_ecommerce.models.User;


import java.util.List;

public interface CartService {
    Cart getByUserId(Long userId);
    Cart addItem(Long userId, Long productId, int quantity);
    Cart removeItem(Long userId, Long productId);
    Cart reduceItem(Long userId,Long productId);
    Cart clear(Long userId);
}

/*

addItem
removeItem
updateItem



 */
