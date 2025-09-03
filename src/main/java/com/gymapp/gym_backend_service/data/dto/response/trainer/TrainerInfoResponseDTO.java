package com.gymapp.gym_backend_service.data.dto.response.trainer;

import com.gymapp.gym_backend_service.data.dto.response.user.UserOverviewResponseDTO;
import com.gymapp.gym_backend_service.data.model.Trainer;

public class TrainerInfoResponseDTO extends UserOverviewResponseDTO {
    private String specialization;
    private Integer experience;
    private Long ratings;

    public TrainerInfoResponseDTO(Trainer data) {
        super(data);

        specialization = data.getSpecialization();
        experience = data.getExperience();
        ratings = (data.getRatings() == null) ? 0 : data.getRatings();
    }
    public String getSpecialization() { return specialization; }
    public Integer getExperience() { return experience; }
    public Long getRatings() { return ratings; }
}
