package com.gymapp.gym_backend_service.service;

import com.gymapp.gym_backend_service.authorization.JWTHandler;
import com.gymapp.gym_backend_service.data.dto.request.GymSessionRequest;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.dto.response.gym_session.SessionResponseDTO;
import com.gymapp.gym_backend_service.data.enums.ActivityType;
import com.gymapp.gym_backend_service.data.enums.UserRole;
import com.gymapp.gym_backend_service.data.model.GymSession;
import com.gymapp.gym_backend_service.data.model.Member;
import com.gymapp.gym_backend_service.data.model.Trainer;
import com.gymapp.gym_backend_service.data.model.User;
import com.gymapp.gym_backend_service.repository.GymSessionRepository;
import com.gymapp.gym_backend_service.repository.MemberRepository;
import com.gymapp.gym_backend_service.repository.RegisteredMembershipsRepository;
import com.gymapp.gym_backend_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class GymSessionService {
    @Autowired
    private GymSessionRepository gymSessionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RegisteredMembershipsRepository registeredMembershipsRepository;
    @Autowired
    private CommonService commonService;

    public List<SessionResponseDTO> getAllSessionsByUser(String header) {
        Long memberID = commonService.getMemberID(header);
        if(memberID == null) throw new IllegalArgumentException("Invalid token. Kindly check it.");

        Optional<Member> member = memberRepository.findById(memberID);
        if(member.isEmpty()) throw new EntityNotFoundException("Unable to proceed with session. No active membership");

        List<GymSession> allSessions = gymSessionRepository.findByMemberId(memberID);
        if(allSessions.isEmpty()) { throw new EntityNotFoundException("No session so far"); }
        return allSessions.stream().map((SessionResponseDTO::new)).toList();
    }

    public List<SessionResponseDTO> getAllSessionByTrainer(Long trainerId) {
        List<GymSession> sessionFilteredByTrainer = gymSessionRepository.findByTrainer((Trainer) userRepository.findById(trainerId).get());
        if(sessionFilteredByTrainer.isEmpty()) { throw new IllegalArgumentException("Enter a valid Trainer ID"); }
        return sessionFilteredByTrainer.stream().map((SessionResponseDTO::new)).toList();
    }

    public GymSession getSessionByID(Long sessionID) {
        Optional<GymSession> gymSession = gymSessionRepository.findById(sessionID);
        if(gymSession.isEmpty()) { throw new EntityNotFoundException("Enter a valid Session ID"); }
        return gymSession.get();
    }

    public GymSession createSession(String header, GymSessionRequest request) {
        Long memberID = commonService.getMemberID(header);
        if(memberID == null) { throw new IllegalArgumentException("Invalid token. Kindly check token"); }

        Optional<User> memberOpt = userRepository.findById(memberID);
        if (memberOpt.isEmpty() || !UserRole.MEMBER.equals(memberOpt.get().getUserRole())) { throw new EntityNotFoundException("Invalid member ID or user is not a member."); }

        Trainer trainer = memberRepository.findById(memberOpt.get().getId()).get().getTrainer();

        if(!registeredMembershipsRepository.isMemberShipActive(memberID)) throw new EntityNotFoundException("Unable to proceed with session. No active membership");

        ActivityType activityType;
        try {
            activityType = ActivityType.valueOf(request.getActivityType().toUpperCase());
        } catch (Exception e) {
            String allowedActivity = String.join(", ",  Arrays.stream(ActivityType.values()).map(Enum::name).toList());
            throw new IllegalArgumentException("Invalid Activity Type. Allowed values are: " + allowedActivity);
        }

        LocalDate _startTime = LocalDate.now();
        LocalDateTime startTime = _startTime.atTime(request.getStartTime());

        LocalDate _endTime = LocalDate.now();
        LocalDateTime endTime = _endTime.atTime(request.getEndTime());

        if(startTime.isAfter(endTime)) throw new IllegalArgumentException("Invalid Start time and End time");

        GymSession session = new GymSession();
        session.setTrainer(trainer);
        session.setMember((Member) memberOpt.get());
        session.setCaloriesBurned(request.getCaloriesBurned());
        session.setNotes(request.getNotes());
        session.setActivityType(activityType);
        session.setStartTime(startTime);
        session.setEndTime(endTime);

        GymSession savedSession = gymSessionRepository.save(session);
        return savedSession;
    }


}
