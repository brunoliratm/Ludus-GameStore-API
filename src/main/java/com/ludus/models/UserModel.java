package com.ludus.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.io.Serial;
import java.io.Serializable;

@Data
@Getter
@Setter
@Entity
public class UserModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @Column(length = 11)
    private String cpf;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String nome;

    @Column(length = 30)
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
