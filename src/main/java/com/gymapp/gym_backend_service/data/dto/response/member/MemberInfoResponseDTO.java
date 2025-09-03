package com.gymapp.gym_backend_service.data.dto.response.member;

import com.gymapp.gym_backend_service.data.model.Member;
import com.gymapp.gym_backend_service.data.dto.response.user.UserOverviewResponseDTO;

public class MemberInfoResponseDTO extends UserOverviewResponseDTO {
    private String trainer;

    public MemberInfoResponseDTO(Member data) {
        super(data);
        trainer = (data.getTrainer() != null) ? data.getTrainer().getName() : "-";
    }
    public String getTrainer() { return trainer; }
}
