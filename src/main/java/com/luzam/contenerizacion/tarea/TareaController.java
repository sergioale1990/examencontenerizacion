package com.luzam.contenerizacion.tarea;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/tareas")
public class TareaController {

    private final TareaRepository repositorio;

    public TareaController(TareaRepository repositorio) {
        this.repositorio = repositorio;
    }

    @PostMapping
    public ResponseEntity<Tarea> crear(@Valid @RequestBody CrearTareaRequest solicitud) {
        Tarea tarea = repositorio.save(new Tarea(solicitud.titulo(), solicitud.descripcion()));
        return ResponseEntity.created(URI.create("/tareas/" + tarea.getId())).body(tarea);
    }

    @GetMapping
    public List<Tarea> listar(@RequestParam(required = false) Boolean completada) {
        return completada == null
                ? repositorio.findAllByOrderByIdAsc()
                : repositorio.findByCompletadaOrderByIdAsc(completada);
    }

    @GetMapping("/{id}")
    public Tarea obtener(@PathVariable Long id) {
        return buscar(id);
    }

    @PatchMapping("/{id}/completar")
    public Tarea completar(@PathVariable Long id) {
        Tarea tarea = buscar(id);
        tarea.marcarCompletada();
        return repositorio.save(tarea);
    }

    private Tarea buscar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La tarea " + id + " no existe"));
    }
}