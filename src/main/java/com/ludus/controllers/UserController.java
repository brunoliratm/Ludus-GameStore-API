package com.ludus.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ludus.dto.UserDTO;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/api/users")
public class UserController {

    @PostMapping()
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

