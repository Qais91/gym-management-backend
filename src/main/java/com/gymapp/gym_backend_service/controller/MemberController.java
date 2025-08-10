package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.Member;
import com.gymapp.gym_backend_service.model.User;
import com.gymapp.gym_backend_service.model.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.model.dto.response.UserResponse;
import com.gymapp.gym_backend_service.model.enums.UserRole;
import com.gymapp.gym_backend_service.repository.MemberRepository;
import com.gymapp.gym_backend_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createMember(@RequestBody Member member) {
        if (userRepository.existsByUsername(member.getName())) {
            return ResponseEntity.badRequest().body(new ApiResponse("false", "Username already exists"));
        }
        if(userRepository.existsByEmail(member.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiResponse("false", "Email already exists"));
        }
        if(userRepository.existsByPhoneNumber(member.getPhoneNumber())) {
            return ResponseEntity.badRequest().body(new ApiResponse("false", "Number already exists"));
        }

        Member savedMember = memberRepository.save(member);
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

    @GetMapping
    public ResponseEntity<?> getAllMemeber() {
        List<User> members = userRepository.findByUserRole(UserRole.GymMemeber);
        if(members.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", "No Members Added"));
        }
        return ResponseEntity.ok(
                members.stream()
                        .map((user) -> new UserResponse(user))
                        .toList()
        );
    }
}
