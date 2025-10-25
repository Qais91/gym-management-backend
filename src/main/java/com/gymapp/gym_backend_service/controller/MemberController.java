package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.authorization.JWTHandler;
import com.gymapp.gym_backend_service.data.model.Member;
import com.gymapp.gym_backend_service.data.model.RegisteredMembership;
import com.gymapp.gym_backend_service.data.model.Trainer;
import com.gymapp.gym_backend_service.data.model.User;
import com.gymapp.gym_backend_service.data.dto.request.member.AssignTrainerRequestDTO;
import com.gymapp.gym_backend_service.data.dto.request.member.CreateMemberRequestDTO;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.dto.response.UserResponse;
import com.gymapp.gym_backend_service.data.dto.response.member.MemberInfoResponseDTO;
import com.gymapp.gym_backend_service.data.enums.UserRole;
import com.gymapp.gym_backend_service.repository.*;
import com.gymapp.gym_backend_service.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@EnableMethodSecurity
@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private MemberService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody CreateMemberRequestDTO member) {
        try {
            return ResponseEntity.ok(new MemberInfoResponseDTO(service.addMember(member)));
        } catch (Exception e) {
            String err_msg = e.getMessage();
            return ResponseEntity.badRequest().body(new ApiResponse("false", err_msg));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @PostMapping("/assign/trainer")
    public ResponseEntity<?> assignTrainer(@RequestHeader("Authorization") String header,@Valid @RequestBody AssignTrainerRequestDTO request) {

        try{
            service.assignTrainer(header, request);
            return ResponseEntity.ok(new ApiResponse("success", "Trainer updated"));
        } catch (EntityNotFoundException e) {
            String err_msg = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", err_msg));
        } catch (Exception e) {
            String err_msg = e.getMessage();
            return ResponseEntity.badRequest().body(new ApiResponse("false", err_msg));
        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getMemeberById(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(new MemberInfoResponseDTO(service.getMember(id)));
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
    public ResponseEntity<?> getAllMemeber() {
        try {
            return ResponseEntity.ok(service.getAllMember());
        } catch (EntityNotFoundException e) {
            String err_msg = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", err_msg));
        } catch (Exception e) {
            String err_msg = e.getMessage();
            return ResponseEntity.badRequest().body(new ApiResponse("false", err_msg));
        }
    }
}
