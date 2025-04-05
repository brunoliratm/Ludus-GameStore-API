package com.ludus.dtos.responses;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GameDtoResponse(
  Long id,
  String name,
  String genre,
  int releaseYear,
  String platform,
  BigDecimal price
) {

}
