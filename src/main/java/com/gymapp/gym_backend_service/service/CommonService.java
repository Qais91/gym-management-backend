package com.gymapp.gym_backend_service.service;

import com.gymapp.gym_backend_service.authorization.JWTHandler;
import com.gymapp.gym_backend_service.data.model.Member;
import com.gymapp.gym_backend_service.repository.RegisteredMembershipsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonService {
    @Autowired
    private JWTHandler jwtHandler;

    @Autowired
    private RegisteredMembershipsRepository registrationRepo;

    public Long getMemberID(String header) {
        try{
            String token = header.substring(7);
            return jwtHandler.extractUserId(token);
        } catch(Exception e) {
            return null;
        }
    }

    public boolean isMemberActive(Member member) {
        return registrationRepo.isMemberShipActive(member.getId());
    }

}
