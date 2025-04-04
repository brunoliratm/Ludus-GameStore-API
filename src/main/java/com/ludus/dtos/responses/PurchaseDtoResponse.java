package com.ludus.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PurchaseDtoResponse(
    Long id,
    Long userId,
    Long gameId,
    LocalDate purchaseDate,
    BigDecimal price,
    String paymentMethod
) {

}
