package com.gymapp.gym_backend_service.data.model;

import com.gymapp.gym_backend_service.data.dto.request.member.CreateMemberRequestDTO;
import com.gymapp.gym_backend_service.data.enums.UserRole;
import jakarta.persistence.*;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Member extends User {

    Member() { setUserRole(UserRole.MEMBER); }

    public Member(CreateMemberRequestDTO memberInfo) {
        setName(memberInfo.getName());
        setUsername(memberInfo.getUsername());
        setEmail(memberInfo.getEmail());
        setPhoneNumber(memberInfo.getPhoneNumber());
        setUserRole(UserRole.MEMBER);
    }

    @ManyToOne
    @JoinColumn(name = "personal_trainer_id")
    private Trainer personalTrainer;

    private boolean isValid;
    public Trainer getTrainer() { return personalTrainer; }
    public void setTrainer(Trainer trainer) { this.personalTrainer = trainer; }
}
