package com.ludus.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiDtoResponse<T>(
    InfoDtoResponse info,
    List<T> results
) {
}
