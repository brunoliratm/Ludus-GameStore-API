package com.ludus.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDtoResponse(
    Long id,
    String email,
    String name
) {

}
