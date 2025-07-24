package com.gymapp.gym_backend_service.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Member extends User {

    @ManyToOne
    @JoinColumn(name = "personal_trainer_id")
    private Trainer personalTrainer;

    private boolean isValid;
    public Trainer getTrainer() { return personalTrainer; }
    public void setTrainer(Trainer trainer) { this.personalTrainer = trainer; }
}
