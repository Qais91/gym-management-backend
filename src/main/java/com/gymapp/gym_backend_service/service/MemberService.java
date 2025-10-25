package com.gymapp.gym_backend_service.service;

import com.gymapp.gym_backend_service.authorization.JWTHandler;
import com.gymapp.gym_backend_service.data.dto.request.member.AssignTrainerRequestDTO;
import com.gymapp.gym_backend_service.data.dto.request.member.CreateMemberRequestDTO;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.dto.response.UserResponse;
import com.gymapp.gym_backend_service.data.dto.response.member.MemberInfoResponseDTO;
import com.gymapp.gym_backend_service.data.enums.UserRole;
import com.gymapp.gym_backend_service.data.model.Member;
import com.gymapp.gym_backend_service.data.model.RegisteredMembership;
import com.gymapp.gym_backend_service.data.model.Trainer;
import com.gymapp.gym_backend_service.data.model.User;
import com.gymapp.gym_backend_service.repository.MemberRepository;
import com.gymapp.gym_backend_service.repository.RegisteredMembershipsRepository;
import com.gymapp.gym_backend_service.repository.TrainerRepository;
import com.gymapp.gym_backend_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
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
        return registeredMembershipsRepository.isMemberShipActive(member.getId());
    }

    public Member addMember(CreateMemberRequestDTO member) {
        if (userRepository.existsByUsername(member.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if(userRepository.existsByEmail(member.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if(userRepository.existsByPhoneNumber(member.getPhoneNumber())) {
            throw new IllegalArgumentException("Number already exists");
        }


        Optional<Trainer> trainer = Optional.empty();
        if(member.getTrainerID() != null) {
            trainer = trainerRepo.findById(member.getTrainerID());
            if(trainer.isEmpty())  throw new IllegalArgumentException("Enter a valid user ID");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Member mem = new Member(member);
        mem.setPassword(encoder.encode(member.getPassword()));
        trainer.ifPresent(mem::setTrainer);

        return memberRepository.save(mem);
    }

    public void assignTrainer(String header, AssignTrainerRequestDTO request) {
        String token = header.substring(7);
        Long memberId = jwtHandler.extractUserId(token);

        if(memberId == null) throw new IllegalArgumentException("Token Error");

        Optional<User> requestUser = userRepository.findById(memberId);
        Member member;
        if(requestUser.isEmpty() || requestUser.get().getUserRole() == UserRole.ADMIN) {
            member = memberRepository.findByUsername(request.getMemberName());
        } else {
            member = memberRepository.findById(requestUser.get().getId()).get();
        }

        if(member == null) { throw new EntityNotFoundException("Member not found"); }

        if(!isMemberActive(member)) { throw new IllegalArgumentException("User don't have any active registered membership. Unable to assign trainer"); }

        RegisteredMembership regMem = registeredMembershipsRepository.findActiveRegisteredMemberShip(member.getId()).get();
        if(!regMem.getMembership().getTrainerIncluded()) throw new IllegalArgumentException("User can't have any trainer in current membership.");

        Trainer trainer = trainerRepo.findByUsername(request.getTrainerName());

        if(trainer == null) { throw new EntityNotFoundException("Trainer not found"); }

        member.setTrainer(trainer);
        memberRepository.save(member);
    }

    public Member getMember(Long memberID) {
        Optional<Member> memberOpt = memberRepository.findById(memberID);

        if(memberOpt.isEmpty()) throw new EntityNotFoundException("Member Not Found");

        return memberOpt.get();
    }

    public List<UserResponse> getAllMember() {
        List<User> members = userRepository.findByUserRole(UserRole.MEMBER);
        if(members.isEmpty()) { throw new EntityNotFoundException("No Members"); }

        return members.stream()
                        .map((user) -> new UserResponse(user))
                        .toList();
    }
}
