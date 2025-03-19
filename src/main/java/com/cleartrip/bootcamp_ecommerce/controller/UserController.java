package com.cleartrip.bootcamp_ecommerce.controller;

import com.cleartrip.bootcamp_ecommerce.dto.ApiResponse;
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


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUser(User user){
        return ResponseEntity.ok(new ApiResponse<>("success", userService.getAll(),"Retrieved All Users"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<User>> loginUser(@RequestBody LoginObject loginObject, HttpServletRequest request){
        User loggedInUser =  userService.login(loginObject.getEmail(), loginObject.getPassword());

        HttpSession session = request.getSession();
        session.setAttribute("userId", loggedInUser.getId());
        session.setAttribute("userRole", loggedInUser.getRole());

         return ResponseEntity.ok(new ApiResponse<>("success",loggedInUser,"Logged in"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(new ApiResponse<>("Success","","Logout successful"));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<User>> addUser(@RequestBody User user){
        return ResponseEntity.ok(new ApiResponse<>("Success",userService.create(user),"User Added successful"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(new ApiResponse<>("Success",userService.getById(id),"User Retrived"));
    }
}
