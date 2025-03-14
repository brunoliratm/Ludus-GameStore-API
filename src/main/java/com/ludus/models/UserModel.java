package com.ludus.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
public class UserModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @Column(nullable = false, length = 11)
    private String cpf;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 30)
    private String senha;

    public UserModel() {
    }

    public UserModel(String nome, String senha, String cpf, String email) {
        this.nome = nome;
        this.senha = senha;
        this.email = email;
        this.cpf = cpf;
    }
}
