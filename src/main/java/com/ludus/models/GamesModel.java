package com.ludus.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
public class GamesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String nome;
    @Column(length = 30)
    private String genero;
    @Column(length = 4)
    private int anoLancamento;
    @Column(length = 30)
    private String plataforma;
    @Column(length = 10)
    private Float preco;

    public GamesModel() {
    }

    public GamesModel(String nome, String genero, int anoLancamento, String plataforma, Float preco) {
        this.nome = nome;
        this.genero = genero;
        this.anoLancamento = anoLancamento;
        this.plataforma = plataforma;
        this.preco = preco;
    }

}
