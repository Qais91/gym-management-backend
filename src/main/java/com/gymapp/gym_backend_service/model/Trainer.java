package com.gymapp.gym_backend_service.model;

import com.gymapp.gym_backend_service.model.enums.UserRole;
import jakarta.persistence.*;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Trainer extends User {

    Trainer() { setUserRole(UserRole.Trainer); }

    private int experience;
    private String specialization;
    private double ratings;

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
}
