package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.Member;
import com.gymapp.gym_backend_service.model.Trainer;
import com.gymapp.gym_backend_service.model.User;
import com.gymapp.gym_backend_service.model.dto.request.member.CreateMemberRequestDTO;
import com.gymapp.gym_backend_service.model.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.model.dto.response.UserResponse;
import com.gymapp.gym_backend_service.model.enums.UserRole;
import com.gymapp.gym_backend_service.repository.MemberRepository;
import com.gymapp.gym_backend_service.repository.TrainerRepository;
import com.gymapp.gym_backend_service.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody CreateMemberRequestDTO member) {
        if (userRepository.existsByUsername(member.getName())) {
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
        return ResponseEntity.ok(savedMember);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMemeberById(@PathVariable Long id) {
        Optional<Member> memberOpt = memberRepository.findById(id);

        if(memberOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", "Member Not Found"));
        }
        return ResponseEntity.ok(memberOpt.get());
    }

    @PreAuthorize("hasRole('ADMIN', 'TRAINER')")
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
