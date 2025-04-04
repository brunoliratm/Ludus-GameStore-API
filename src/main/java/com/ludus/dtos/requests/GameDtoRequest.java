package com.ludus.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record GameDtoRequest(
  @NotBlank(message = "name.NotBlank")
  @Size(min = 3, max = 100, message = "name.Size")
  String name,
  
  @NotBlank(message = "genre.NotBlank")
  @Pattern(regexp = "^(ACTION|ADVENTURE|FIGHTING|HORROR|MMORPG|RACING|RPG|SHOOTER|SIMULATION|SPORTS|STRATEGY|SURVIVAL|OTHER)$", message = "invalid.genre")
  @Size(min = 3, max = 30, message = "genre.Size")
  String genre,
  
  @NotNull(message = "releaseYear.NotNull")
  @Min(value = 1900, message = "releaseYear.Min")
  int releaseYear,
  

  @NotBlank(message = "platform.NotBlank")
  @Pattern(regexp = "^(PC|PLAYSTATION|XBOX|NINTENDO|MOBILE|OTHER)$", message = "platform.Invalid")
  @Size(min = 3, max = 30, message = "platform.Size")
  String platform,
  
  @NotNull(message = "price.NotNull")
  @Min(value = 0, message = "price.Min")
  Float price
) {

}
