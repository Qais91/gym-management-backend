package com.gymapp.gym_backend_service.model;

import jakarta.persistence.*;

@Entity
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String specialization;
    private int experienceInYears;
}
