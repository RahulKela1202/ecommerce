package com.cleartrip.bootcamp_ecommerce.services.implementation;

import com.cleartrip.bootcamp_ecommerce.exception.DuplicateException;
import com.cleartrip.bootcamp_ecommerce.exception.NotFoundException;
import com.cleartrip.bootcamp_ecommerce.exception.UnauthorizedAccessException;
import com.cleartrip.bootcamp_ecommerce.models.User;
import com.cleartrip.bootcamp_ecommerce.repository.UserRepository;
import com.cleartrip.bootcamp_ecommerce.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public User create(User user) {
        try {
            System.out.println("Role before saving: " + user.getRole());
            if(userRepository.findByEmail(user.getEmail()).isPresent()){
                throw new DuplicateException("Already Existing User with same email");
            }
            return userRepository.save(user);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<User> getAll(){
        return userRepository.findAll();
    }

    @Override
    public User login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new NotFoundException("Invalid email");
        }
        if(user.get().getPassword().equals(password)){
            return user.get();
        }
        throw new UnauthorizedAccessException("Wrong password");
    }

    @Override
    public User getById(Long id){
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty()){
            throw new NotFoundException("User Not Found with ID:" + id);
        }
        return optionalUser.get();
    }
}
