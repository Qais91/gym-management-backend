package com.gymapp.gym_backend_service.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("TRAINER")
public class Trainer extends User {

    private int experience;
    private String specialization;
    private double ratings;
}
