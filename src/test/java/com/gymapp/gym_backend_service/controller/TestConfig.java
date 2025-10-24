package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.data.dto.request.CreateUserRequestDTO;
import com.gymapp.gym_backend_service.data.dto.request.member.CreateMemberRequestDTO;
import com.gymapp.gym_backend_service.data.enums.DietMealType;
import com.gymapp.gym_backend_service.data.model.Diets;
import com.gymapp.gym_backend_service.data.model.Trainer;
import com.gymapp.gym_backend_service.data.model.User;
import org.springframework.stereotype.Component;

@Component
public class TestConfig {
    String loginToken;
    public User test_admin = new User(
            "test_admin",
            "test admin",
            "test_admin@mail.com",
            "98244276438947",
            "admin_pass"
    );

    public User test_member = new User(
            "test_user",
            "test user",
            "test_user@mail.com",
            "561651212789",
            "user_pass"
    );

    public Trainer test_trainer = new Trainer(
            "test_trainer",
            "test trainer",
            "test_trainer@mail.com",
            "12132378794",
            "trainer_pass",
            "ZUMBA",
            3
    );

    public Diets test_diet = new Diets(
            "test meal",
            DietMealType.BREAKFAST,
            200,
            50
    );

    public void setLoginToken(String token) {
        loginToken = token;
    }

    public String getLoginToken() {
        return loginToken;
    }
}
