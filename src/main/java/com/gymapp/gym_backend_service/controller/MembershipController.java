package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.Membership;
import com.gymapp.gym_backend_service.repository.MembershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/membership")
public class MembershipController {

    @Autowired
    private MembershipRepository membershipRepository;

    @PostMapping
    public Membership create(@RequestBody Membership membership) {
        return membershipRepository.save(membership);
    }

    @GetMapping("/{id}")
    public Membership getById(@PathVariable Long id) {
        return membershipRepository.findById(id).orElseThrow();
    }

}