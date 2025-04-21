package com.ludus.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ludus.dtos.requests.AuthDto;
import com.ludus.dtos.requests.UserDtoRequest;
import com.ludus.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/${api.version}/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @Operation(summary = "Login and get authentication token")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthDto loginDto) {
        String token = authService.login(loginDto, authenticationManager);
        return ResponseEntity.ok()
        .header("Authorization", "Bearer " + token)
        .build();
    }

    @Operation(summary = "Register a new user and get authentication token")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserDtoRequest userDto, BindingResult bindingResult) {
        authService.registerUser(userDto, bindingResult, authenticationManager);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Authorization", "Bearer " + authService.login(new AuthDto(userDto.email(), userDto.password()), authenticationManager))
                .build();
    }
}