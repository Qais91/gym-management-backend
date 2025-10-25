package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.data.model.Membership;
import com.gymapp.gym_backend_service.data.dto.request.memberShip.CreateMemberShipRequestDTO;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.dto.response.MemberShipInfoResponseDTO;
import com.gymapp.gym_backend_service.service.MembershipService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership")
public class MembershipController {

    @Autowired
    private MembershipService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateMemberShipRequestDTO membership) {
        try{
            return ResponseEntity.ok(new ApiResponse("sucess", "membership created with ID : "+service.cerateMembership(membership).getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("false", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getMembershipById(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(service.getMembershipByID(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("false", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @GetMapping
    public ResponseEntity<?> getAllMembership() {
        try{
            return ResponseEntity.ok(service.getAllMembership());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("false", e.getMessage()));
        }
    }
}