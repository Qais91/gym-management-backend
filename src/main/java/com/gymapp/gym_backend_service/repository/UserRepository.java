package com.gymapp.gym_backend_service.repository;

import com.gymapp.gym_backend_service.model.User;
import com.gymapp.gym_backend_service.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findByUserRole(UserRole role);
    boolean existsByUsername(String userName);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phNumber);
}