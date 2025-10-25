package com.gymapp.gym_backend_service.service;

import com.gymapp.gym_backend_service.data.dto.request.memberShip.CreateMemberShipRequestDTO;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.dto.response.MemberShipInfoResponseDTO;
import com.gymapp.gym_backend_service.data.model.Membership;
import com.gymapp.gym_backend_service.repository.MembershipRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MembershipService {
    @Autowired
    private MembershipRepository membershipRepository;

    public Membership cerateMembership(CreateMemberShipRequestDTO membership) {
        return membershipRepository.save(new Membership(membership));
    }

    public Membership getMembershipByID(Long membershipID) {
        Optional<Membership> memberShip = membershipRepository.findById(membershipID);
        if (memberShip.isEmpty()) { throw new EntityNotFoundException("Membership Not Found"); }
        return memberShip.get();
    }

    public List<MemberShipInfoResponseDTO> getAllMembership() {
        List<Membership> memberships = membershipRepository.findAll();
        if(memberships.isEmpty()) {
            throw new EntityNotFoundException("No MemberShips found");
        }
        return memberships.stream()
            .map(MemberShipInfoResponseDTO::new)
            .toList();
    }

}
