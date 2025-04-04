package com.ludus.dtos.requests;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record PurchaseDtoRequest(

    @NotNull(message = "purchase.user.not.found")
    Long userId,
    
    @NotNull(message = "purchase.game.not.found")
    Long gameId,
    
    @NotNull(message = "purchase.date.required")
    @PastOrPresent(message = "purchase.date.invalid")
    LocalDate purchaseDate,
    
    @NotNull(message = "price.NotNull")
    @Min(value = 0, message = "price.Min")
    BigDecimal price,
    
    @NotBlank(message = "purchase.invalid.payment.method")
    String paymentMethod
) {
}
