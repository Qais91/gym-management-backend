package com.gymapp.gym_backend_service.model.dto.request.member;

import com.gymapp.gym_backend_service.model.dto.request.CreateUserRequestDTO;
import com.gymapp.gym_backend_service.model.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;

public class CreateMemberRequestDTO extends CreateUserRequestDTO {

    private Long trainerID;

    public Long getTrainerID() { return trainerID; }
}
