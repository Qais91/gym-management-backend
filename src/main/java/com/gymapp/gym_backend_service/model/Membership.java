package com.gymapp.gym_backend_service.model;

import jakarta.persistence.*;

@Entity
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private int durationInMonths;
    private double price;
}