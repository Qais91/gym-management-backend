package com.gymapp.gym_backend_service.data.model;

import com.gymapp.gym_backend_service.data.dto.request.trainer.CreateTrainerRequestDTO;
import com.gymapp.gym_backend_service.data.enums.UserRole;
import jakarta.persistence.*;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Trainer extends User {

    Trainer() {}

    public Trainer(String userName, String name, String email, String phoneNumber, String password, String specialization, Integer experience) {
        super(userName, name, email, phoneNumber, password);
        setSpecialization(specialization);
        setExperience(experience);
        setUserRole(UserRole.TRAINER);
    }

    public Trainer(CreateTrainerRequestDTO trainerInfo) {
        setName(trainerInfo.getName());
        setUsername(trainerInfo.getUsername());
        setEmail(trainerInfo.getEmail());
        setPhoneNumber(trainerInfo.getPhoneNumber());
        setSpecialization(trainerInfo.getSpecialization());
        setExperience(trainerInfo.getExperience());
        setUserRole(UserRole.TRAINER);
    }

    private Integer experience;
    private String specialization;
    private Long ratings;

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }

    public Long getRatings() { return ratings; }
    public void setRatings(Long ratings) { this.ratings = ratings; }
}
