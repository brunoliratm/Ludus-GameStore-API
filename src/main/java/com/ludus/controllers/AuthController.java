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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/${api.version}/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @Operation(
        summary = "Login and get authentication token", 
        description = "Authenticates user credentials and returns a JWT token",
        responses = {
            @ApiResponse(responseCode = "200", description = "Authentication successful, token provided in Authorization header"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials format", 
                content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Empty Credentials", value = "{\"message\": \"Email and password are required\"}"),
                    @ExampleObject(name = "Invalid Email", value = "{\"message\": \"Invalid email format\"}")
                })
            ),
            @ApiResponse(responseCode = "401", description = "Authentication failed", 
                content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Invalid Credentials", value = "{\"message\": \"Email or password do not match\"}"),
                    @ExampleObject(name = "User Inactive", value = "{\"message\": \"User inactive\"}")
                })
            ),
            @ApiResponse(responseCode = "404", description = "User not found", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"User not found\"}")
                )
            ),
            @ApiResponse(responseCode = "500", description = "Error during authentication", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"Error while logging in\"}")
                )
            )
        }
    )
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthDto loginDto) {
        String token = authService.login(loginDto, authenticationManager);
        return ResponseEntity.ok()
        .header("Authorization", "Bearer " + token)
        .build();
    }

    @Operation(
        summary = "Register a new user and get authentication token", 
        description = "Creates a new user account and returns a JWT token",
        responses = {
            @ApiResponse(responseCode = "201", description = "User registered successfully, token provided in Authorization header"),
            @ApiResponse(responseCode = "400", description = "Invalid user data provided", 
                content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Validation Error", value = "{\"error\": \"Validation failed\", \"details\": [\"Email cannot be blank\", \"Name must be between 5 and 100 characters\", \"Password cannot be blank\"]}"),
                    @ExampleObject(name = "Email Exists", value = "{\"error\": \"Validation failed\", \"details\": [\"Email already registered\"]}")
                })
            ),
            @ApiResponse(responseCode = "401", description = "Authentication failed after registration", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"Error while logging in\"}")
                )
            ),
            @ApiResponse(responseCode = "500", description = "Error during registration", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"Error creating user\"}")
                )
            )
        }
    )
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserDtoRequest userDto, BindingResult bindingResult) {
        authService.registerUser(userDto, bindingResult, authenticationManager);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Authorization", "Bearer " + authService.login(new AuthDto(userDto.email(), userDto.password()), authenticationManager))
                .build();
    }
}