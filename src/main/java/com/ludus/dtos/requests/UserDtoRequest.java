package com.ludus.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDtoRequest(
  @NotBlank(message = "{user.email.NotBlank}")
  @Email(message = "{user.email.Email}")
  @JsonProperty("email") String email,
  
  @NotBlank(message = "{user.name.NotBlank}")
  @Size(min = 5, max = 100, message = "{user.name.Size}")
  @JsonProperty("name") String name,
  
  @NotBlank(message = "{user.password.NotBlank}")
  @Size(min = 5, max = 30, message = "{user.password.Size}")
  @JsonProperty("password") String password
) {
  
}
