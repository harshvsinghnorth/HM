package com.example.HM.controllers;

import com.example.HM.dto.LoginRequestDto;
import com.example.HM.dto.LoginResponseDto;
import com.example.HM.dto.SignUpRequestDto;
import com.example.HM.dto.SignUpResponseDto;
import com.example.HM.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

     @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
         return ResponseEntity.ok(authService.login(loginRequestDto));

     }


     @PostMapping("/signup")
     public ResponseEntity<SignUpResponseDto> signup(@RequestBody SignUpRequestDto signUpRequestDto){
         return ResponseEntity.ok(authService.signup(signUpRequestDto));

     }
}
