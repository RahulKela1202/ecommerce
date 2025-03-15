package com.cleartrip.bootcamp_ecommerce.services;

import com.cleartrip.bootcamp_ecommerce.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
     User addUser(User user);
     List<User> getAllUser();
     User login(String email, String password);
     Optional<User> getById(Long id);
}
