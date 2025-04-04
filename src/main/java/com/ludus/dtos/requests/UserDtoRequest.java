package com.ludus.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDtoRequest(
  @NotBlank(message = "email.NotBlank")
  @Email(message = "email.Email")
  String email,
  @NotBlank(message = "nome.NotBlank")
  @Size(min = 5, max = 100, message = "nome.Size")
  String name,
  @NotBlank(message = "senha.NotBlank")
  @Size(min = 5, max = 30, message = "senha.Size")
  String password
) {
  

}
