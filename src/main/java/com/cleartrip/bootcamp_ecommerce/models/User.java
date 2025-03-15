package com.cleartrip.bootcamp_ecommerce.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="email",nullable = false, unique = true)
    private String email;

    @Column(name="password",nullable = false)
    private String password;

    @Column(name="name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name="role")
    private Role role;  // "admin" or "customer"

    @OneToMany (mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Order> orders;

}
