package com.example.mediatracker.controller;

import com.example.mediatracker.dto.AuthenticationDTO;
import com.example.mediatracker.dto.LoginResponseDTO;
import com.example.mediatracker.dto.RegisterDTO;
import com.example.mediatracker.model.UserModel;
import com.example.mediatracker.repository.UserRepository;
import com.example.mediatracker.security.TokenService;
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
    private TokenService tokenService;

    public AuthenticationController(AuthenticationManager authenticationManager, UserRepository userRepository, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO loginData) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(loginData.userName(), loginData.password());
        Authentication auth = authenticationManager.authenticate(usernamePassword);

        String token = tokenService.generateToken((UserModel) auth.getPrincipal());

        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDTO(token));
    }

    @PostMapping("register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO registerData) {
        if(userRepository.findByUserName(registerData.userName()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(registerData.password());

        UserModel newUser = new UserModel(registerData.userName(), encryptedPassword, registerData.role());

        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
