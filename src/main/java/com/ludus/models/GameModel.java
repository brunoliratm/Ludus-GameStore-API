package com.ludus.models;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class GameModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String nome;

    @Column(length = 30)
    private String genero;

    private int anoLancamento;

    @Column(length = 30)
    private String plataforma;

    private BigDecimal preco;

    public GameModel() {
    }

    public GameModel(String nome, String genero, int anoLancamento, String plataforma, BigDecimal preco) {
        this.nome = nome;
        this.genero = genero;
        this.anoLancamento = anoLancamento;
        this.plataforma = plataforma;
        this.preco = preco;
    }

}
