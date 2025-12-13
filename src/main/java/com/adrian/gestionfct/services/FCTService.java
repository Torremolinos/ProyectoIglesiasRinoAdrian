package com.adrian.gestionfct.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrian.gestionfct.modelo.*;
import com.adrian.gestionfct.repositorios.FCTRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FCTService {

    @Autowired
    private FCTRepository fctRepository;

    public FCT save(FCT fct) {
        return fctRepository.save(fct);
    }

    public FCT update(FCT fct) {
        return fctRepository.save(fct);
    }

    public void delete(FCT fct) {
        fctRepository.delete(fct);
    }

    public void deleteById(Long id) {
        fctRepository.deleteById(id);
    }

    public Optional<FCT> findById(Long id) {
        return fctRepository.findById(id);
    }

    public List<FCT> findAll() {
        return fctRepository.findAll();
    }

    public List<FCT> findByEstudiante(Estudiante estudiante) {
        return fctRepository.findByEstudiante(estudiante);
    }

    public List<FCT> findByEmpresa(Empresa empresa) {
        return fctRepository.findByEmpresa(empresa);
    }

    public List<FCT> findByTutorEmpresa(TutorEmpresa tutorEmpresa) {
        return fctRepository.findByTutorEmpresa(tutorEmpresa);
    }

    public List<FCT> findByPeriodo(Periodo periodo) {
        return fctRepository.findByPeriodo(periodo);
    }

    public List<FCT> findByCursoAcademico(CursoAcademico cursoAcademico) {
        return fctRepository.findByCursoAcademico(cursoAcademico);
    }

    public List<FCT> findByEstado(EstadoFCT estado) {
        return fctRepository.findByEstado(estado);
    }

    public List<FCT> findActivas() {
        return fctRepository.findByEstado(EstadoFCT.ACTIVA);
    }

    public List<FCT> findByEstudianteYEstado(Estudiante estudiante, EstadoFCT estado) {
        return fctRepository.findByEstudianteAndEstado(estudiante, estado);
    }

    public List<FCT> findByCursoAcademicoYEstado(CursoAcademico cursoAcademico, EstadoFCT estado) {
        return fctRepository.findByCursoAcademicoAndEstado(cursoAcademico, estado);
    }

    /**
     * Verifica si un estudiante ya tiene una FCT asignada en un periodo.
     */
    public boolean existeFctParaEstudianteEnPeriodo(Estudiante estudiante, Periodo periodo) {
        return fctRepository.existsByEstudianteAndPeriodo(estudiante, periodo);
    }

    /**
     * Crea una nueva asignación FCT.
     */
    public FCT crearAsignacion(Estudiante estudiante, Empresa empresa, TutorEmpresa tutorEmpresa, Periodo periodo) {
        FCT fct = new FCT(estudiante, empresa, tutorEmpresa, periodo);
        return fctRepository.save(fct);
    }
}
