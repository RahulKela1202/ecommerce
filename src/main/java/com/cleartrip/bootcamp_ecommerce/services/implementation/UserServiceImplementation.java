package com.cleartrip.bootcamp_ecommerce.services.implementation;

import com.cleartrip.bootcamp_ecommerce.models.User;
import com.cleartrip.bootcamp_ecommerce.repository.UserRepository;
import com.cleartrip.bootcamp_ecommerce.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImplementation(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public User addUser(User user) {
        try {
            System.out.println("Role before saving: " + user.getRole());
            return userRepository.save(user);
        } catch (DuplicateKeyException e) {
            return null;
        }
    }

    @Override
    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    @Override
    public User login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent() && user.get().getPassword().equals(password)){
            return user.get();
        }
        throw new RuntimeException("Invalid email or password");
    }

    @Override
    public Optional<User> getById(Long id){
        return userRepository.findById(id);
    }
}
