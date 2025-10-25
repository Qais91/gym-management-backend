package com.gymapp.gym_backend_service.service;

import com.gymapp.gym_backend_service.data.enums.UserRole;
import com.gymapp.gym_backend_service.data.model.User;
import com.gymapp.gym_backend_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    public UserRepository userRepository;

    public User createAdmin(User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setUserRole(UserRole.ADMIN);
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
