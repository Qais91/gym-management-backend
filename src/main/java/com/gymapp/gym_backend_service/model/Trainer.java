package com.gymapp.gym_backend_service.model;

import jakarta.persistence.*;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Trainer extends User {

    private int experience;
    private String specialization;
    private double ratings;

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
}
