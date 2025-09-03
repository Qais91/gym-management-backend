package com.gymapp.gym_backend_service.data.dto.request.trainer;

import com.gymapp.gym_backend_service.data.dto.request.CreateUserRequestDTO;
import jakarta.validation.constraints.NotBlank;

public class CreateTrainerRequestDTO extends CreateUserRequestDTO {

    @NotBlank(message = "Specialization need to be mentioned")
    private String specialization;
    private Integer experience = 0;
    private Long ratings;

    public String getSpecialization() { return specialization; }
    public Integer getExperience() { return experience; }
    public Long getRatings() { return ratings; }
}
