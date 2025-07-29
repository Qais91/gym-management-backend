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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
}
