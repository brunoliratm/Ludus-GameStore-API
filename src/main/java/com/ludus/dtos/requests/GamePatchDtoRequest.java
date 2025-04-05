package com.ludus.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GamePatchDtoRequest(
    @JsonProperty("name") String name,
    @JsonProperty("genre") String genre,
    @JsonProperty("releaseYear") Integer releaseYear,
    @JsonProperty("platform") String platform,
    @JsonProperty("price") Float price
) {

}
