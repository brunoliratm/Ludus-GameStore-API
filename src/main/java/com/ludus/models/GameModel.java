package com.ludus.models;

import java.math.BigDecimal;

import com.ludus.enums.GameGenre;
import com.ludus.enums.GamePlatform;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class GameModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;

    @Column(nullable = false ,length = 30)
    @Enumerated(EnumType.STRING)
    private GameGenre genre;

    private int releaseYear;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private GamePlatform platform;

    private BigDecimal price;

}
