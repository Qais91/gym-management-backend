package com.gymapp.gym_backend_service.model;

import jakarta.persistence.*;
import java.time.LocalDate;

public class CustomDietPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDate createdDate = LocalDate.now();

    @Column(length = 1000)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;
}
