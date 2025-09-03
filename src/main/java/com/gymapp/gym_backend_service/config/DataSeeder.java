package com.gymapp.gym_backend_service.config;

import com.gymapp.gym_backend_service.data.enums.ActivityType;
import com.gymapp.gym_backend_service.data.enums.DietMealType;
import com.gymapp.gym_backend_service.data.enums.UserRole;
import com.gymapp.gym_backend_service.data.model.*;
import com.gymapp.gym_backend_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrainerRepository trainerRepo;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private DietsRepository dietsRepository;
    @Autowired
    private MembershipRepository membershipRepository;

    @Override
    public void run(String... args) {
        seedUser("admin123","Admin User", "admin@example.com", "ADMIN@pass", "133850493850", UserRole.ADMIN);
        seedUser("regular_user123","Regular User", "regular.user@example.com", "USER@pass", "320493850", UserRole.MEMBER);
        seedUser("premium_user123","Premium User", "premium.user@example.com", "PREMIUM@pass", "320493859870", UserRole.MEMBER);
        seedUser("elite_user123","Elite User", "elite.user@example.com", "ELITE@pass", "32049385970", UserRole.MEMBER);
        seedTrainer("trainer1", "Trainer User 1", "trainer.1@example.com", "TRAINER1@pass", "21763265273662", ActivityType.CARDIO, 3);
        seedTrainer("trainer2", "Trainer User 2", "trainer.2@example.com", "TRAINER2@pass", "217632652662", ActivityType.CROSSFIT, 2);

        seedDiets("Fruit salad", DietMealType.BREAKFAST, 150, 500);
        seedDiets("Roasted Chicken", DietMealType.LUNCH, 400, 250);
        seedDiets("Protien Bars", DietMealType.PRE_WORKOUT, 200, 200);
        seedDiets("Protien Shake", DietMealType.POST_WORKOUT, 350, 300);

        seedMembership("Regular Plan", 1, 800, false, false);
        seedMembership("Silver Plan", 1, 1500, true, false);
        seedMembership("Elite Plan", 1, 4000, true, true);
    }

    private void seedUser(String user_name, String name, String email, String rawPassword, String phNum, UserRole role) {
        if (!userRepository.existsByEmail(email)) {
            String hashedPassword = new BCryptPasswordEncoder().encode(rawPassword);
            if(role == UserRole.ADMIN) {
                User user = new User(user_name, name, email, phNum, hashedPassword);
                user.setUserRole(UserRole.ADMIN);
                userRepository.save(user);
            }else {
                Member member = new Member(user_name, name, email, phNum, hashedPassword);
                memberRepository.save(member);
            }
        }
    }

    private void seedTrainer(String user_name, String name, String email, String rawPassword, String phNum, ActivityType specialization, Integer yearsOfExp) {
        if(!userRepository.existsByEmail(email)) {
            String hashedPassword = new BCryptPasswordEncoder().encode(rawPassword);
            Trainer trainer = new Trainer(user_name, name, email, phNum, hashedPassword, specialization.name(), yearsOfExp);
            trainerRepo.save(trainer);
        }
    }

    private void seedDiets(String foodItem, DietMealType mealType, Integer calories, Integer price) {
        if(!dietsRepository.existsByDiet(foodItem, calories, mealType)) {
            dietsRepository.save(new Diets(foodItem, mealType, calories, price));
        }
    }

    private void seedMembership(String title, Integer monthDuration, double price, boolean trainerIncluded, boolean requireMedicalVal) {
        if(!membershipRepository.existsByMembershipPlan(title, monthDuration, price)) {
            membershipRepository.save(new Membership(title, monthDuration, price, trainerIncluded, requireMedicalVal));
        }
    }
}
