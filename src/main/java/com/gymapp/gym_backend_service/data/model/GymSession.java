package com.gymapp.gym_backend_service.data.model;

import com.gymapp.gym_backend_service.data.enums.ActivityType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class GymSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    private Long caloriesBurned;

    @Column(length = 1000)
    private String notes;

    private LocalDateTime sessionStartDateTime;
    private LocalDateTime sessionEndDateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Long caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public Trainer getTrainer() { return trainer; }
    public void setTrainer(Trainer trainer) { this.trainer = trainer; }

    public ActivityType getActivityType() { return activityType; }
    public void setActivityType(ActivityType activityType) { this.activityType = activityType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getStartTime() { return sessionStartDateTime; }
    public void setStartTime(LocalDateTime startTime) { this.sessionStartDateTime = startTime; }

    public LocalDateTime getEndTime() { return sessionEndDateTime; }
    public void setEndTime(LocalDateTime endTime) { this.sessionEndDateTime = endTime; }
}
