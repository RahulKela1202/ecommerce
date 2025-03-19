package com.cleartrip.bootcamp_ecommerce.services;

import com.cleartrip.bootcamp_ecommerce.models.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User create(User user);
    List<User> getAll();
    User getById(Long id);
    User login(String email, String password);
}
