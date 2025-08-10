package com.gymapp.gym_backend_service.model;

import com.gymapp.gym_backend_service.model.enums.UserRole;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Member extends User {

    Member() { setUserRole(UserRole.GymMemeber); }

    @ManyToOne
    @JoinColumn(name = "personal_trainer_id")
    private Trainer personalTrainer;

    private boolean isValid;
    public Trainer getTrainer() { return personalTrainer; }
    public void setTrainer(Trainer trainer) { this.personalTrainer = trainer; }
}
