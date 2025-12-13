package com.adrian.gestionfct.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrian.gestionfct.modelo.CursoAcademico;
import com.adrian.gestionfct.modelo.Periodo;
import com.adrian.gestionfct.modelo.TipoPeriodo;
import com.adrian.gestionfct.repositorios.PeriodoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PeriodoService {

    @Autowired
    private PeriodoRepository periodoRepository;

    public Periodo save(Periodo periodo) {
        return periodoRepository.save(periodo);
    }

    public Periodo update(Periodo periodo) {
        return periodoRepository.save(periodo);
    }

    public void delete(Periodo periodo) {
        periodoRepository.delete(periodo);
    }

    public void deleteById(Long id) {
        periodoRepository.deleteById(id);
    }

    public Optional<Periodo> findById(Long id) {
        return periodoRepository.findById(id);
    }

    public List<Periodo> findAll() {
        return periodoRepository.findAll();
    }

    public List<Periodo> findByCursoAcademico(CursoAcademico cursoAcademico) {
        return periodoRepository.findByCursoAcademico(cursoAcademico);
    }

    public List<Periodo> findByCurso(Integer curso) {
        return periodoRepository.findByCurso(curso);
    }

    public List<Periodo> findByTipo(TipoPeriodo tipo) {
        return periodoRepository.findByTipo(tipo);
    }

    public List<Periodo> findByCursoAcademicoYCurso(CursoAcademico cursoAcademico, Integer curso) {
        return periodoRepository.findByCursoAcademicoAndCurso(cursoAcademico, curso);
    }
}
