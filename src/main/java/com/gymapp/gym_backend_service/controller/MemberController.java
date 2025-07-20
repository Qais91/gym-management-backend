package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.Member;
import com.gymapp.gym_backend_service.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return memberRepository.save(member);
    }
}
