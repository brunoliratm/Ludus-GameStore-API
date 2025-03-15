package com.ludus.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.ludus.dto.UserDTO;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/api/users")
public class UserController {

    @Operation(summary = "Create a New User")
    @PostMapping()
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Update an Existing User")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Delete an Existing User")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get a User by ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

