package com.example.secruity101.controller;

import com.example.secruity101.model.LoginRequest;
import com.example.secruity101.model.User;
import com.example.secruity101.service.UserDetailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserDetailService userDetailService;

    public AuthController(UserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }

    @GetMapping("/login")
    public String login() {
        return "로그인됨 ㅊㅋ";
    }

    @PostMapping("/signup")
    public String signup(@RequestBody LoginRequest loginRequest) {
        userDetailService.saveUser(loginRequest);
        return "로그인됨 ㅊㅋ";
    }
}
