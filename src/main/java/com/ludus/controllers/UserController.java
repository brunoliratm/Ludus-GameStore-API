package com.ludus.controllers;
import com.ludus.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get All Users")
    @GetMapping()
    public ResponseEntity<UserDTO> getUsers() {
        userService.getAllUsers();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get a User by ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        userService.getUserById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Create a New User")
    @PostMapping()
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserDTO userDTO,
            BindingResult bindingResult) {
        userService.createUser(userDTO, bindingResult);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Update an Existing User")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id,
            @RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        userService.updateUser(id, userDTO, bindingResult);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Delete an Existing User")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

