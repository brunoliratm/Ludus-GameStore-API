package com.ludus.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PurchaseDtoRequest(
    @NotNull(message = "purchase.user.not.found")
    @Positive(message = "User ID must be greater than 0")
    @JsonProperty("userId") Long userId,
    
    @NotNull(message = "purchase.game.not.found")
    @Positive(message = "Game ID must be greater than 0")
    @JsonProperty("gameId") Long gameId,

    @NotBlank(message = "purchase.invalid.payment.method")
    @JsonProperty("paymentMethod") String paymentMethod
) {
    @Override
    public String toString() {
        return "PurchaseDtoRequest{" +
                "userId=" + userId +
                ", gameId=" + gameId +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}
