package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.Membership;
import com.gymapp.gym_backend_service.model.dto.request.memberShip.CreateMemberShipDTO;
import com.gymapp.gym_backend_service.model.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.model.dto.response.MemberShipInfoResponseDTO;
import com.gymapp.gym_backend_service.repository.MembershipRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/membership")
public class MembershipController {

    @Autowired
    private MembershipRepository membershipRepository;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateMemberShipDTO membership) {
        Membership mem = membershipRepository.save(new Membership(membership));
        return ResponseEntity.ok(new ApiResponse("sucess", "membership created with ID : "+mem.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Membership> memberShip = membershipRepository.findById(id);
        if (memberShip.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", "Membership Not Found"));
        }
        return ResponseEntity.ok(memberShip.get());
    }

    @GetMapping
    public ResponseEntity<?> getAllMemebership() {
        List<Membership> memberships = membershipRepository.findAll();
        if(memberships.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", "No MemeberShips"));
        }
        return ResponseEntity.ok(
                memberships.stream()
                        .map((membership) -> new MemberShipInfoResponseDTO(membership))
                        .toList()
        );
    }
}