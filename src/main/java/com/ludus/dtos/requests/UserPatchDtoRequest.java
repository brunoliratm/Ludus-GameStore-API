package com.ludus.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserPatchDtoRequest(
    @Email(message = "{user.email.Email}")
    @JsonProperty("email") String email,
    
    @Size(min = 5, max = 100, message = "{user.name.Size}")
    @JsonProperty("name") String name,
    
    @Size(min = 5, max = 30, message = "{user.password.Size}")
    @JsonProperty("password") String password
) { }
