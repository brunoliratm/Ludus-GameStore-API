package com.ludus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record GameDTO(
  @NotBlank(message = "name.NotBlank")
  @Size(min = 3, max = 100, message = "nome.Size")
  String name,
  
  @NotBlank(message = "genre.NotBlank")
  @Size(min = 3, max = 30, message = "genero.Size")
  String genre,
  
  @NotNull(message = "releaseYear.NotNull")
  @Min(value = 1900, message = "anoLancamento.Min")
  int releaseYear,
  
  @NotBlank(message = "platform.NotBlank")
  @Size(min = 3, max = 30, message = "plataforma.Size")
  String platform,
  
  @NotNull(message = "price.NotNull")
  @Min(value = 0, message = "price.Min")
  Float price
) {

}
