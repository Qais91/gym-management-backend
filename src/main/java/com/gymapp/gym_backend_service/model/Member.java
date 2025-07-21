package com.gymapp.gym_backend_service.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("MEMBER")
public class Member extends User {

    @ManyToOne
    @JoinColumn(name = "personal_trainer_id")
    private Trainer personalTrainer;

    private boolean isValid;// Getters and setters
}
