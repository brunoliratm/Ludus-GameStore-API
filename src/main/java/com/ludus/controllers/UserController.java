package com.ludus.controllers;

import com.ludus.dtos.requests.UserDtoRequest;
import com.ludus.dtos.requests.UserPatchDtoRequest;
import com.ludus.dtos.responses.ApiDtoResponse;
import com.ludus.dtos.responses.UserDtoResponse;
import com.ludus.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/${api.version}/users")
@Tag(name = "Users", description = "Endpoints for user management")
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
        summary = "Get All Users", 
        description = "Retrieves a paginated list of active users with optional name filtering",
        responses = {
            @ApiResponse(responseCode = "200", description = "Users found successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters", 
                content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Invalid Page", value = "{\"message\": \"Page number must be greater than 0\"}"),
                    @ExampleObject(name = "Invalid Format", value = "{\"message\": \"Invalid Page format: must be a number\"}")
                })
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
                )
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden access", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"You don't have permission to access this resource\"}")
                )
            )
        }
    )
    @GetMapping()
    public ResponseEntity<ApiDtoResponse<UserDtoResponse>> getUsers(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(required = false) String name
    ) {
        ApiDtoResponse<UserDtoResponse> response= userService.getAllUsers(page, name);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
        summary = "Get a User by ID", 
        description = "Retrieves a specific user by their ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "User found successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID provided", 
                content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Invalid ID", value = "{\"message\": \"Invalid ID\"}"),
                    @ExampleObject(name = "Invalid Format", value = "{\"message\": \"Invalid ID format: must be a number\", \"details\": \"The value 'abc' is not valid for parameter 'id'\"}")
                })
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
                )
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden access", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"You don't have permission to access this resource\"}")
                )
            ),
            @ApiResponse(responseCode = "404", description = "User not found", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"User not found with id: 123\"}")
                )
            )
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserDtoResponse> getUser(@PathVariable Long id) {
        UserDtoResponse user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(
        summary = "Create a New User", 
        description = "Creates a new user with the provided details",
        responses = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data provided", 
                content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Validation Error", value = "{\"error\": \"Validation failed\", \"details\": [\"Email cannot be blank\", \"Name must be between 5 and 100 characters\", \"Password cannot be blank\"]}"),
                    @ExampleObject(name = "Email Exists", value = "{\"error\": \"Validation failed\", \"details\": [\"Email already registered\"]}")
                })
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
                )
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden access", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"You don't have permission to access this resource\"}")
                )
            ),
            @ApiResponse(responseCode = "500", description = "Error creating user", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"Error creating user\"}")
                )
            )
        }
    )
    @PostMapping()
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserDtoRequest userDTO,
            BindingResult bindingResult) {
        userService.createUser(userDTO, bindingResult);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(
        summary = "Update an Existing User", 
        description = "Updates an existing user with the provided details",
        responses = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data provided", 
                content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Invalid ID", value = "{\"message\": \"Invalid ID\"}"),
                    @ExampleObject(name = "Validation Error", value = "{\"error\": \"Validation failed\", \"details\": [\"Email must be valid\", \"Name must be between 5 and 100 characters\", \"Password must be between 5 and 30 characters\"]}"),
                    @ExampleObject(name = "Email Exists", value = "{\"error\": \"Validation failed\", \"details\": [\"Email already registered\"]}")
                })
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
                )
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden access", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"You don't have permission to access this resource\"}")
                )
            ),
            @ApiResponse(responseCode = "404", description = "User not found", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"User not found with id: 123\"}")
                )
            ),
            @ApiResponse(responseCode = "500", description = "Error updating user", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"Error updating user\"}")
                )
            )
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id,
            @RequestBody @Valid UserPatchDtoRequest userDTO, BindingResult bindingResult) {
        userService.updateUser(id, userDTO, bindingResult);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
        summary = "Delete an Existing User", 
        description = "Soft deletes a user by setting their active status to false",
        responses = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID provided", 
                content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Invalid ID", value = "{\"message\": \"Invalid ID\"}"),
                    @ExampleObject(name = "Invalid Format", value = "{\"message\": \"Invalid ID format: must be a number\", \"details\": \"The value 'abc' is not valid for parameter 'id'\"}")
                })
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
                )
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden access", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"You don't have permission to access this resource\"}")
                )
            ),
            @ApiResponse(responseCode = "404", description = "User not found", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"User not found with id: 123\"}")
                )
            ),
            @ApiResponse(responseCode = "500", description = "Error deleting user", 
                content = @Content(mediaType = "application/json", 
                    examples = @ExampleObject(value = "{\"message\": \"Error deleting user\"}")
                )
            )
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

