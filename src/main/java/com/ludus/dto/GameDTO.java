package com.ludus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GameDTO(
  @NotBlank(message = "nome.NotBlank")
  @Size(min = 3, max = 100, message = "nome.Size")
  String nome,
  @NotBlank(message = "genero.NotBlank")
  @Size(min = 3, max = 30, message = "genero.Size")
  String genero,
  @NotBlank(message = "anoLancamento.NotBlank")
  @Size(min = 4, max = 4, message = "anoLancamento.Size")
  int anoLancamento,
  @NotBlank(message = "plataforma.NotBlank")
  @Size(min = 3, max = 30, message = "plataforma.Size")
  String plataforma,
  @NotBlank(message = "preco.NotBlank")
  @Size(min = 1, max = 10, message = "preco.Size")
  Float preco
) {

}
