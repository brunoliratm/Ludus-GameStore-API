package com.ludus.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.ludus.enums.PaymentMethod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PurchaseModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDate purchaseDate;

  @Column(nullable = false)
  private BigDecimal price;

  @Column(nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private PaymentMethod paymentMethod;

  @ManyToOne
  @JoinColumn(name = "game_id")
  private GameModel game;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserModel user;

}
