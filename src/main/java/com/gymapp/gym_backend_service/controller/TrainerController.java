package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.Trainer;
import com.gymapp.gym_backend_service.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    @Autowired
    private TrainerRepository trainerRepository;

    @PostMapping
    public Trainer addTrainer(@RequestBody Trainer trainer) {
        return trainerRepository.save(trainer);
    }
}
