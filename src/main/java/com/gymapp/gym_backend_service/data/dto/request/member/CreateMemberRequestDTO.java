package com.gymapp.gym_backend_service.data.dto.request.member;

import com.gymapp.gym_backend_service.data.dto.request.CreateUserRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CreateMemberRequestDTO extends CreateUserRequestDTO {

    private Long trainerID;

    public Long getTrainerID() { return trainerID; }
}
