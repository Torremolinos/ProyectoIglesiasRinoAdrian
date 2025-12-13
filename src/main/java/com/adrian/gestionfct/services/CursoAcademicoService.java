package com.adrian.gestionfct.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.gestionfct.modelo.CursoAcademico;
import com.adrian.gestionfct.repositorios.CursoAcademicoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CursoAcademicoService {

    @Autowired
    private CursoAcademicoRepository cursoAcademicoRepository;

    public CursoAcademico save(CursoAcademico cursoAcademico) {
        return cursoAcademicoRepository.save(cursoAcademico);
    }

    public CursoAcademico update(CursoAcademico cursoAcademico) {
        return cursoAcademicoRepository.save(cursoAcademico);
    }

    public void delete(CursoAcademico cursoAcademico) {
        cursoAcademicoRepository.delete(cursoAcademico);
    }

    public void deleteById(Long id) {
        cursoAcademicoRepository.deleteById(id);
    }

    public Optional<CursoAcademico> findById(Long id) {
        return cursoAcademicoRepository.findById(id);
    }

    public List<CursoAcademico> findAll() {
        return cursoAcademicoRepository.findAll();
    }

    public Optional<CursoAcademico> findByNombre(String nombre) {
        return cursoAcademicoRepository.findByNombre(nombre);
    }

    public Optional<CursoAcademico> findActivo() {
        return cursoAcademicoRepository.findByActivoTrue();
    }

    public boolean existeNombre(String nombre) {
        return cursoAcademicoRepository.existsByNombre(nombre);
    }

    /**
     * Activa un curso académico y desactiva los demás.
     */
    @Transactional
    public void activarCurso(CursoAcademico cursoActivar) {
        // Desactivar todos los cursos
        List<CursoAcademico> todos = cursoAcademicoRepository.findAll();
        for (CursoAcademico curso : todos) {
            curso.setActivo(false);
            cursoAcademicoRepository.save(curso);
        }
        // Activar el seleccionado
        cursoActivar.setActivo(true);
        cursoAcademicoRepository.save(cursoActivar);
    }
}
