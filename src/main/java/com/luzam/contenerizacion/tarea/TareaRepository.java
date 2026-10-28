package com.luzam.contenerizacion.tarea;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findAllByOrderByIdAsc();

    List<Tarea> findByCompletadaOrderByIdAsc(boolean completada);
}
