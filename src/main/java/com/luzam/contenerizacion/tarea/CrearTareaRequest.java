package com.luzam.contenerizacion.tarea;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearTareaRequest(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 120, message = "El título admite como máximo 120 caracteres")
        String titulo,

        @Size(max = 500, message = "La descripción admite como máximo 500 caracteres")
        String descripcion) {
}
