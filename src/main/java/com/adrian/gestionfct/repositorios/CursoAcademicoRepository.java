package com.adrian.gestionfct.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adrian.gestionfct.modelo.CursoAcademico;

import java.util.Optional;

@Repository
public interface CursoAcademicoRepository extends JpaRepository<CursoAcademico, Long> {

    Optional<CursoAcademico> findByNombre(String nombre);

    Optional<CursoAcademico> findByActivoTrue();

    boolean existsByNombre(String nombre);
}
