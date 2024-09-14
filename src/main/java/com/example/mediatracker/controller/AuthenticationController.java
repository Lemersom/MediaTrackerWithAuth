package com.example.mediatracker.controller;

import com.example.mediatracker.dto.AuthenticationRecordDto;
import com.example.mediatracker.dto.RegisterRecordDto;
import com.example.mediatracker.model.UserModel;
import com.example.mediatracker.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;

    public AuthenticationController(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid AuthenticationRecordDto loginData) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(loginData.userName(), loginData.password());

        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRecordDto registerData) {
        if(userRepository.findByUserName(registerData.userName()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(registerData.password());

        UserModel newUser = new UserModel(registerData.userName(), registerData.password(), registerData.role());

        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
