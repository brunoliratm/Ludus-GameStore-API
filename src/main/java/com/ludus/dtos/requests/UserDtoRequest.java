package com.ludus.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDtoRequest(
  @NotBlank(message = "email.NotBlank")
  @Email(message = "email.Email")
  String email,
  @NotBlank(message = "name.NotBlank")
  @Size(min = 5, max = 100, message = "name.Size")
  String name,
  @NotBlank(message = "password.NotBlank")
  @Size(min = 5, max = 30, message = "password.Size")
  String password
) {
  

}
