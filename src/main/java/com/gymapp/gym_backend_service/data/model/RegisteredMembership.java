package com.gymapp.gym_backend_service.data.model;

import com.gymapp.gym_backend_service.data.enums.RegistrationStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class RegisteredMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "membership_id")
    private Membership membership;

    @ManyToOne
    @JoinColumn(name = "validator_id")
    private Trainer validator;

    private String documentPath;

    private LocalDate startDate;
    private LocalDate endDate;

    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "diet_plan_id")
    public CustomDietPlan dietPlan;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus status = RegistrationStatus.APPLIED;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public Membership getMembership() { return membership; }
    public void setMembership(Membership membership) { this.membership = membership; }

    public Trainer getValidator() { return validator; }
    public void setValidator(Trainer trainer) { this.validator = trainer; }

    public String getDocumentPath() { return documentPath; }
    public void setDocumentPath(String docPath) { documentPath = docPath; }

    public CustomDietPlan getDietPlan() { return dietPlan; }
    public void setDietPlan(CustomDietPlan dietPlan) { this.dietPlan = dietPlan; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public void setStatus(RegistrationStatus status) { this.status = status; }
    public RegistrationStatus getStatus() { return status; }
}