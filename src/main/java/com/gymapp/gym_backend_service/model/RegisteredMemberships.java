package com.gymapp.gym_backend_service.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class RegisteredMemberships {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "membership_id")
    private Membership membership;

    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}