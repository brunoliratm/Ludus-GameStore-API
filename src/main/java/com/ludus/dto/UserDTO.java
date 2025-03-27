package com.ludus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDTO(
  @NotBlank(message = "cpf.NotBlank")
  @Size(min = 11, max = 11, message = "CPF must have 11 characters")
  String cpf,
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
