package com.gymapp.gym_backend_service.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
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

    @ManyToMany
    @JoinTable(
            name = "custom_diet_plan_diets",
            joinColumns = @JoinColumn(name = "diet_plan_id"),
            inverseJoinColumns = @JoinColumn(name = "diet_id")
    )
    private List<Diets> diets = new ArrayList<>();

    public List<Diets> getDiets() { return diets; }
    public void setDiets(List<Diets> asssignedDiet) { diets = asssignedDiet; }
}
