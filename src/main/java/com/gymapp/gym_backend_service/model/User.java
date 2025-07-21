package com.gymapp.gym_backend_service.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(unique = true)
    private String username;

    private String password;
    private LocalDate joinedDate = LocalDate.now();
}