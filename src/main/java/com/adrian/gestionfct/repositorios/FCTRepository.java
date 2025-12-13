package com.adrian.gestionfct.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adrian.gestionfct.modelo.*;

import java.util.List;

@Repository
public interface FCTRepository extends JpaRepository<FCT, Long> {

    List<FCT> findByEstudiante(Estudiante estudiante);

    List<FCT> findByEmpresa(Empresa empresa);

    List<FCT> findByTutorEmpresa(TutorEmpresa tutorEmpresa);

    List<FCT> findByPeriodo(Periodo periodo);

    List<FCT> findByCursoAcademico(CursoAcademico cursoAcademico);

    List<FCT> findByEstado(EstadoFCT estado);

    List<FCT> findByEstudianteAndEstado(Estudiante estudiante, EstadoFCT estado);

    List<FCT> findByCursoAcademicoAndEstado(CursoAcademico cursoAcademico, EstadoFCT estado);

    boolean existsByEstudianteAndPeriodo(Estudiante estudiante, Periodo periodo);
}
