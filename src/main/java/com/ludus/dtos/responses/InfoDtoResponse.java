package com.ludus.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InfoDtoResponse(
    long count,
    long pages,
    String next,
    String prev
) {

}
