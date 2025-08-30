package com.gymapp.gym_backend_service.data.model;

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
    @JoinColumn(name = "trainer_id")
    private Trainer createdBy;

    @ManyToMany
    @JoinTable(
            name = "custom_diet_plan_diets",
            joinColumns = @JoinColumn(name = "diet_plan_id"),
            inverseJoinColumns = @JoinColumn(name = "diet_id")
    )
    private List<Diets> diets = new ArrayList<>();

    public List<Diets> getDiets() { return diets; }
    public void setDiets(List<Diets> assignedDiet) { diets = assignedDiet; }

    public int getDietsPrice() {
        return diets.stream()
                .mapToInt(Diets::getPrice)
                .sum();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public void setNotes(String notes) { this.notes = notes; }
    public String getNotes() { return notes; }

    public void setCreatedBy(Trainer createdBy) { this.createdBy = createdBy; }
    public Trainer getCreatedBy() { return createdBy; }

    public LocalDate getCreatedDate() { return createdDate; }
}
