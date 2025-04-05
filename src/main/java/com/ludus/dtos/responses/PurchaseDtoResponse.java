package com.ludus.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PurchaseDtoResponse(
    Long id,
    LocalDate purchaseDate,
    BigDecimal price,
    String paymentMethod,
    List<GameDtoResponse> game,
    List<UserDtoResponse> user
) {

}
