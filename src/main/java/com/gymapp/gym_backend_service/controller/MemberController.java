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
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@EnableMethodSecurity
@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrainerRepository trainerRepo;
    @Autowired
    private JWTHandler jwtHandler;
    @Autowired
    private RegisteredMembershipsRepository registeredMembershipsRepository;

    boolean isMemberActive(Member member) {
//        Optional<RegisteredMembership> memReg = registeredMembershipsRepository.findActiveRegisteredMemberShip(member.getId(), LocalDate.now());
//        return !memReg.isEmpty();
        return registeredMembershipsRepository.isMemberShipActive(member.getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody CreateMemberRequestDTO member) {
        if (userRepository.existsByUsername(member.getUsername())) {
            return ResponseEntity.badRequest().body(new ApiResponse("false", "Username already exists"));
        }
        if(userRepository.existsByEmail(member.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiResponse("false", "Email already exists"));
        }
        if(userRepository.existsByPhoneNumber(member.getPhoneNumber())) {
            return ResponseEntity.badRequest().body(new ApiResponse("false", "Number already exists"));
        }

        Optional<Trainer> trainer = Optional.empty();
        if(member.getTrainerID() != null) {
            trainer = trainerRepo.findById(member.getTrainerID());
            if(trainer.isEmpty())  return ResponseEntity.badRequest().body(new ApiResponse("false", "Enter a valid user ID"));
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Member mem = new Member(member);
        mem.setPassword(encoder.encode(member.getPassword()));
        if(!trainer.isEmpty())  mem.setTrainer(trainer.get());

        Member savedMember = memberRepository.save(mem);
        return ResponseEntity.ok(new MemberInfoResponseDTO(savedMember));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @PostMapping("/assign/trainer")
    public ResponseEntity<?> assignTrainer(@RequestHeader("Authorization") String header,@Valid @RequestBody AssignTrainerRequestDTO request) {
        String token = header.substring(7);
        Long memberId = jwtHandler.extractUserId(token);

        if(memberId == null) return ResponseEntity.badRequest().body(new ApiResponse("error", "Token Error"));

        Optional<User> requestUser = userRepository.findById(memberId);
        Member member;
        if(requestUser.isEmpty() || requestUser.get().getUserRole() == UserRole.ADMIN) {
            member = memberRepository.findByUsername(request.getMemberName());
        } else {
            member = memberRepository.findById(requestUser.get().getId()).get();
        }

        if(member == null) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "Member not found")); }

        if(!isMemberActive(member)) { return ResponseEntity.badRequest().body(new ApiResponse("error", "User don't have any active registered membership. Unable to assign trainer")); }

        RegisteredMembership regMem = registeredMembershipsRepository.findActiveRegisteredMemberShip(member.getId()).get();
        if(!regMem.getMembership().getTrainerIncluded()) return ResponseEntity.badRequest().body(new ApiResponse("error", "User can't have any trainer in current membership."));

        Trainer trainer = trainerRepo.findByUsername(request.getTrainerName());

        if(trainer == null) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "Trainer not found")); }

        member.setTrainer(trainer);
        memberRepository.save(member);

        return ResponseEntity.ok(new ApiResponse("success", "Trainer updated"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getMemeberById(@PathVariable Long id) {
        Optional<Member> memberOpt = memberRepository.findById(id);

        if(memberOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", "Member Not Found"));
        }
        return ResponseEntity.ok(new MemberInfoResponseDTO(memberOpt.get()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllMemeber() {
        List<User> members = userRepository.findByUserRole(UserRole.MEMBER);
        if(members.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", "No Members"));
        }
        return ResponseEntity.ok(
                members.stream()
                        .map((user) -> new UserResponse(user))
                        .toList()
        );
    }
}
