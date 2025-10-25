package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.authorization.JWTHandler;
import com.gymapp.gym_backend_service.data.model.*;
import com.gymapp.gym_backend_service.data.dto.request.register_membership.AssignCustomDietRequest;
import com.gymapp.gym_backend_service.data.dto.request.register_membership.AssignValidatorRequest;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.enums.RegistrationStatus;
import com.gymapp.gym_backend_service.data.enums.UserRole;
import com.gymapp.gym_backend_service.repository.*;
import com.gymapp.gym_backend_service.data.dto.response.registered_membership.RegisteredMembershipInfoResponseDTO;
import com.gymapp.gym_backend_service.service.RegisteredMembershipService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/register-membership")
public class RegisteredMembershipController {

    @Autowired
    private RegisteredMembershipService service;

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping
    public ResponseEntity<?> registerMembership(@RequestHeader("Authorization") String header, @RequestParam Long membershipId, @RequestParam(required = false) MultipartFile medicalDocument) {
        try {
            return ResponseEntity.ok(new ApiResponse("success", service.registerMembership(header, membershipId, medicalDocument)));
        } catch (EntityNotFoundException e) {
            String err_msg = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", err_msg));
        } catch (Exception e) {
            String err_msg = e.getMessage();
            return ResponseEntity.badRequest().body(new ApiResponse("false", err_msg));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/assign/validator")
    public ResponseEntity<?> assignValidator(@Valid @RequestBody AssignValidatorRequest request) {
        try {
            service.assignValidator(request);
            return ResponseEntity.ok(new ApiResponse("success", "Validator Updated."));
        } catch (EntityNotFoundException e) {
            String err_msg = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", err_msg));
        } catch (Exception e) {
            String err_msg = e.getMessage();
            return ResponseEntity.badRequest().body(new ApiResponse("false", err_msg));
        }
    }

    @PreAuthorize("hasRole('TRAINER')")
    @PutMapping("/assign/custom-diet")
    public ResponseEntity<?> assignCustomDiet(@Valid @RequestBody AssignCustomDietRequest request) {
        try {
            service.assignCustomDiet(request);
            return ResponseEntity.ok(new ApiResponse("success", "Diet Updated"));
        } catch (EntityNotFoundException e) {
            String err_msg = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", err_msg));
        } catch (Exception e) {
            String err_msg = e.getMessage();
            return ResponseEntity.badRequest().body(new ApiResponse("false", err_msg));
        }
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PutMapping("/register/{regMemId}")
    public ResponseEntity<?> registerRegisteredMemberShip(@RequestHeader("Authorization") String header, @PathVariable("regMemId") Long regMemId) {
        try {
            return ResponseEntity.ok(new ApiResponse("success", "MemberShip Registered. Invoice generated ID: "+service.registerRegisteredMemberShip(header, regMemId).getId()));
        } catch (EntityNotFoundException e) {
            String err_msg = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", err_msg));
        } catch (Exception e) {
            String err_msg = e.getMessage();
            return ResponseEntity.badRequest().body(new ApiResponse("false", err_msg));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllMemberShipInfo() {
        try {
            return ResponseEntity.ok(service.getAllMemberShipInfo());
        } catch (EntityNotFoundException e) {
            String err_msg = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", err_msg));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{regMemId}")
    public ResponseEntity<?> getRegMemberShipInfoByID(@PathVariable("regMemId") Long regMemID) {
        try {
            return ResponseEntity.ok(new RegisteredMembershipInfoResponseDTO(service.getRegMemberShipInfoByID(regMemID)));
        } catch (EntityNotFoundException e) {
            String err_msg = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", err_msg));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @GetMapping({"/member/{memberId}", "/member"})
    public ResponseEntity<?> getMemberMembershipInfo(@RequestHeader("Authorization") String header, @PathVariable(value = "memberId", required = false) Long memberID) {
        try {
            return ResponseEntity.ok(service.getMemberShipInfoByMember(header, memberID));
        } catch (EntityNotFoundException e) {
            String err_msg = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", err_msg));
        } catch (Exception e) {
            String err_msg = e.getMessage();
            return ResponseEntity.badRequest().body(new ApiResponse("false", err_msg));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    @GetMapping({"/trainer/{trainerId}", "/trainer"})
    public ResponseEntity<?> getTrainerAssignedRegistrationInfo(@RequestHeader("Authorization") String header, @PathVariable(value = "trainerId", required = false) Long trainerID) {
        try {
            return ResponseEntity.ok(service.getTrainerAssignedRegsitrationInfo(header, trainerID));
        } catch (EntityNotFoundException e) {
            String err_msg = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", err_msg));
        } catch (Exception e) {
            String err_msg = e.getMessage();
            return ResponseEntity.badRequest().body(new ApiResponse("false", err_msg));
        }
    }

    @PreAuthorize("hasRole('MEMBER')")
    @DeleteMapping("/{regMemID}")
    public ResponseEntity<?> cancelRegistration(@RequestHeader("Authorization") String header, @PathVariable("regMemID") Long regMemID) {
        try {
            return ResponseEntity.ok(new RegisteredMembershipInfoResponseDTO(service.cancelRegistration(header, regMemID)));
        } catch (EntityNotFoundException e) {
            String err_msg = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", err_msg));
        } catch (Exception e) {
            String err_msg = e.getMessage();
            return ResponseEntity.badRequest().body(new ApiResponse("false", err_msg));
        }
    }
}
