package com.adrian.gestionfct.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrian.gestionfct.modelo.Estudiante;
import com.adrian.gestionfct.modelo.Usuario;
import com.adrian.gestionfct.repositorios.EstudianteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EstudianteService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    public Estudiante save(Estudiante estudiante) {
        return estudianteRepository.save(estudiante);
    }

    public Estudiante update(Estudiante estudiante) {
        return estudianteRepository.save(estudiante);
    }

    public void delete(Estudiante estudiante) {
        estudianteRepository.delete(estudiante);
    }

    public void deleteById(Long id) {
        estudianteRepository.deleteById(id);
    }

    public Optional<Estudiante> findById(Long id) {
        return estudianteRepository.findById(id);
    }

    public List<Estudiante> findAll() {
        return estudianteRepository.findAll();
    }

    public List<Estudiante> findActivos() {
        return estudianteRepository.findByActivoTrue();
    }

    public Optional<Estudiante> findByDni(String dni) {
        return estudianteRepository.findByDni(dni);
    }

    public Optional<Estudiante> findByUsuario(Usuario usuario) {
        return estudianteRepository.findByUsuario(usuario);
    }

    public List<Estudiante> findByCiclo(String ciclo) {
        return estudianteRepository.findByCiclo(ciclo);
    }

    public List<Estudiante> findByGrupo(String grupo) {
        return estudianteRepository.findByGrupo(grupo);
    }

    public List<Estudiante> findByProfesorTutor(Usuario profesorTutor) {
        return estudianteRepository.findByProfesorTutor(profesorTutor);
    }

    public List<Estudiante> findByCicloYGrupo(String ciclo, String grupo) {
        return estudianteRepository.findByCicloAndGrupo(ciclo, grupo);
    }

    public boolean existeDni(String dni) {
        return estudianteRepository.existsByDni(dni);
    }

    public void deleteInBatch(List<Estudiante> estudiantes) {
        estudianteRepository.deleteAll(estudiantes);
    }
}
