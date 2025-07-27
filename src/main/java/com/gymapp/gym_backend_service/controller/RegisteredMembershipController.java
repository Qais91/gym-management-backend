package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.Member;
import com.gymapp.gym_backend_service.model.Membership;
import com.gymapp.gym_backend_service.model.RegisteredMemberships;
import com.gymapp.gym_backend_service.repository.MemberRepository;
import com.gymapp.gym_backend_service.repository.MembershipRepository;
import com.gymapp.gym_backend_service.repository.RegisteredMembershipsRepository;
import com.gymapp.gym_backend_service.model.dto.MembershipInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class RegisteredMembershipController {

    @Autowired
    private RegisteredMembershipsRepository registrationRepo;
    @Autowired
    private MemberRepository memberRepo;
    @Autowired
    private MembershipRepository membershipRepo;

    @PostMapping
    public ResponseEntity<?> registerMembership(@RequestParam Long memberId, @RequestParam Long membershipId) {
        Member member = memberRepo.findById(memberId).orElse(null);
        Membership membership = membershipRepo.findById(membershipId).orElse(null);

        if (member == null) {
            return ResponseEntity.badRequest().body("Invalid member");
        }

        if(membership == null) {
            return ResponseEntity.badRequest().body("Invalid membership ID");
        }

        RegisteredMemberships registration = new RegisteredMemberships();
        registration.setCustomer(member);
        registration.setMembership(membership);
        registration.setStartDate(LocalDate.now());
        registration.setEndDate(LocalDate.now().plusMonths(membership.getDurationInMonths()));
        registration.setActive(true);

        return ResponseEntity.ok(registrationRepo.save(registration));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<?> getMemberMembershipInfo(@RequestParam Long memberID) {
        List<RegisteredMemberships> registrations = registrationRepo.findByMemberId(memberID);

        if (registrations.isEmpty()) {
            return ResponseEntity.status(404).body("No memberships found for customer ID: " + memberID);
        }

        List<MembershipInfoDTO> result = registrations.stream().map(reg ->
                new MembershipInfoDTO(
                        reg.getMembership().getTitle(),
                        reg.getStartDate(),
                        reg.getEndDate(),
                        reg.isActive()
                )
        ).toList();

        return ResponseEntity.ok(registrations);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelRegistration(@PathVariable Long id) {
        return registrationRepo.findById(id).map(reg -> {
            reg.setActive(false);
            return ResponseEntity.ok(registrationRepo.save(reg));
        }).orElse(ResponseEntity.notFound().build());
    }
}
