package com.marks.notificacao.model;

import com.marks.candidatura.model.Candidatura;
import com.marks.usuario.model.Usuario;
import com.marks.vaga.model.Vaga;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "notificacoes")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Usuario destinatario;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoNotificacao tipo;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotBlank
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String mensagem;

    @Column(nullable = false)
    private boolean lida = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaga_id")
    private Vaga vaga;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidatura_id")
    private Candidatura candidatura;

    @CreationTimestamp
    @Column(name = "criada_em", nullable = false, updatable = false)
    private Instant criadaEm;

    protected Notificacao() {
    }

    public Notificacao(
            Usuario destinatario,
            TipoNotificacao tipo,
            String titulo,
            String mensagem
    ) {
        this.destinatario = destinatario;
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensagem = mensagem;
    }

    public Long getId() {
        return id;
    }

    public Usuario getDestinatario() {
        return destinatario;
    }

    public TipoNotificacao getTipo() {
        return tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public boolean isLida() {
        return lida;
    }

    public void marcarComoLida() {
        this.lida = true;
    }

    public Vaga getVaga() {
        return vaga;
    }

    public Candidatura getCandidatura() {
        return candidatura;
    }

    public void referenciarVaga(Vaga vaga) {
        this.vaga = vaga;
        this.candidatura = null;
    }

    public void referenciarCandidatura(Candidatura candidatura) {
        this.candidatura = candidatura;
        this.vaga = null;
    }

    @AssertTrue(message = "A notificação deve referenciar no máximo uma vaga ou candidatura")
    public boolean isReferenciaValida() {
        return vaga == null || candidatura == null;
    }

    public Instant getCriadaEm() {
        return criadaEm;
    }
}
