package com.gymapp.gym_backend_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class GymSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime sessionDateTime;

    private int durationInMinutes;

    @Column(length = 1000)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;
}
