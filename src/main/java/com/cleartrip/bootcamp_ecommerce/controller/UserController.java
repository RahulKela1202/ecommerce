package com.cleartrip.bootcamp_ecommerce.controller;

import com.cleartrip.bootcamp_ecommerce.dto.LoginObject;
import com.cleartrip.bootcamp_ecommerce.models.User;
import com.cleartrip.bootcamp_ecommerce.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUser(User user){
        return userService.getAllUser();
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginObject loginObject, HttpServletRequest request){
        User loggedInUser =  userService.login(loginObject.getEmail(), loginObject.getPassword());

        HttpSession session = request.getSession();
        session.setAttribute("userId", loggedInUser.getId());
        session.setAttribute("userRole", loggedInUser.getRole());

         return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("Logout successful");
    }

    @PostMapping("/add")
    public User addUser(@RequestBody User user){
        return userService.addUser(user);
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id){
        return userService.getById(id);
    }
}
