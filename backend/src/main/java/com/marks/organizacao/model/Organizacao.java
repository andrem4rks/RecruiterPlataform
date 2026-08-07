package com.marks.organizacao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "organizacoes")
public class Organizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false)
    private boolean ativa = true;

    @CreationTimestamp
    @Column(name = "criada_em", nullable = false, updatable = false)
    private Instant criadaEm;

    @UpdateTimestamp
    @Column(name = "atualizada_em", nullable = false)
    private Instant atualizadaEm;

    protected Organizacao() {
    }

    public Organizacao(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void ativar() {
        ativa = true;
    }

    public void desativar() {
        ativa = false;
    }

    public Instant getCriadaEm() {
        return criadaEm;
    }

    public Instant getAtualizadaEm() {
        return atualizadaEm;
    }
}
