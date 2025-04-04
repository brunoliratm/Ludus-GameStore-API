package com.ludus.controllers;
import com.ludus.dtos.requests.UserDtoRequest;
import com.ludus.dtos.requests.UserPatchDtoRequest;
import com.ludus.dtos.responses.ApiDtoResponse;
import com.ludus.dtos.responses.UserDtoResponse;
import com.ludus.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/api/${api.version}/users")
public class UserController {

    @Autowired
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get All Users")
    @GetMapping()
    public ResponseEntity<ApiDtoResponse<UserDtoResponse>> getUsers(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(required = false) String name
    ) {
        ApiDtoResponse<UserDtoResponse> response= userService.getAllUsers(page, name);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get a User by ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserDtoResponse> getUser(@PathVariable Long id) {
        UserDtoResponse user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Create a New User")
    @PostMapping()
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserDtoRequest userDTO,
            BindingResult bindingResult) {
        userService.createUser(userDTO, bindingResult);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Update an Existing User")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id,
            @RequestBody @Valid UserPatchDtoRequest userDTO, BindingResult bindingResult) {
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

