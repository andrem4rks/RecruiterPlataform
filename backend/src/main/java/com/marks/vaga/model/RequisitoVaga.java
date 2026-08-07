package com.marks.vaga.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "requisitos_vaga")
public class RequisitoVaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String nome;

    @Size(max = 500)
    @Column(length = 500)
    private String descricao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoRequisito tipo;

    @Size(max = 255)
    @Column(name = "valor_texto", length = 255)
    private String valorTexto;

    @PositiveOrZero
    @Column(name = "valor_numerico")
    private Integer valorNumerico;

    @Column(nullable = false)
    private boolean obrigatorio = true;

    protected RequisitoVaga() {
    }

    public RequisitoVaga(String nome, TipoRequisito tipo) {
        this.nome = nome;
        this.tipo = tipo;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoRequisito getTipo() {
        return tipo;
    }

    public void setTipo(TipoRequisito tipo) {
        this.tipo = tipo;
    }

    public String getValorTexto() {
        return valorTexto;
    }

    public void setValorTexto(String valorTexto) {
        this.valorTexto = valorTexto;
    }

    public Integer getValorNumerico() {
        return valorNumerico;
    }

    public void setValorNumerico(Integer valorNumerico) {
        this.valorNumerico = valorNumerico;
    }

    public boolean isObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }
}
