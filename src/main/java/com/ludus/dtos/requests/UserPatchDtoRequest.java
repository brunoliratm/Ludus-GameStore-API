package com.ludus.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserPatchDtoRequest(
    @Email(message = "email.Email")
    String email,
    @Size(min = 5, max = 100, message = "nome.Size")
    String name,
    @Size(min = 5, max = 30, message = "senha.Size")
    String password
) {

}
