package com.adrian.gestionfct.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adrian.gestionfct.modelo.CursoAcademico;
import com.adrian.gestionfct.modelo.Periodo;
import com.adrian.gestionfct.modelo.TipoPeriodo;

import java.util.List;

@Repository
public interface PeriodoRepository extends JpaRepository<Periodo, Long> {

    List<Periodo> findByCursoAcademico(CursoAcademico cursoAcademico);

    List<Periodo> findByCurso(Integer curso);

    List<Periodo> findByTipo(TipoPeriodo tipo);

    List<Periodo> findByCursoAcademicoAndCurso(CursoAcademico cursoAcademico, Integer curso);

    List<Periodo> findByActivoTrue();
}
