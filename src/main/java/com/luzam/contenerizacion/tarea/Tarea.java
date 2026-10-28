package com.luzam.contenerizacion.tarea;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;


@Entity
@Table(name = "tareas")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT de MySQL
    private Long id;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private boolean completada = false;

    @Column(name = "creada_en", nullable = false, updatable = false)
    private Instant creadaEn;

    @Column(name = "completada_en")
    private Instant completadaEn;

    /** Constructor vacío requerido por JPA (no se usa desde el código). */
    protected Tarea() {
    }

    public Tarea(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    /** Se ejecuta justo antes del INSERT. */
    @PrePersist
    void alCrear() {
        this.creadaEn = Instant.now();
    }

    /** Regla de negocio: marcar como completada es idempotente (repetirlo no cambia la fecha). */
    public void marcarCompletada() {
        if (!completada) {
            this.completada = true;
            this.completadaEn = Instant.now();
        }
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public boolean isCompletada() { return completada; }
    public Instant getCreadaEn() { return creadaEn; }
    public Instant getCompletadaEn() { return completadaEn; }
}
