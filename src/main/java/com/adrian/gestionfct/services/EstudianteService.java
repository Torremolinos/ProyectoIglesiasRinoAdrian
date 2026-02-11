package com.adrian.gestionfct.services;

import com.adrian.gestionfct.modelo.Estudiante;
import com.adrian.gestionfct.repositorios.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EstudianteService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    // CRUD básico
    @Transactional
    public Estudiante guardar(Estudiante estudiante) {
        validar(estudiante);
        return estudianteRepository.save(estudiante);
    }

    @Transactional
    public void eliminar(Long id) {
        estudianteRepository.deleteById(id);
    }

    public List<Estudiante> obtenerTodos() {
        return estudianteRepository.findAllByOrderByApellidosAsc();
    }

    public Optional<Estudiante> obtenerPorId(Long id) {
        return estudianteRepository.findById(id);
    }

    // Búsquedas
    public List<Estudiante> buscarPorNombreOApellidos(String busqueda) {
        return estudianteRepository.buscarPorNombreOApellidos(busqueda);
    }

    public List<Estudiante> obtenerPorCiclo(String ciclo) {
        return estudianteRepository.findByCiclo(ciclo);
    }

    // Validaciones
    public void validar(Estudiante estudiante) {
        if (estudiante.getNombre() == null || estudiante.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (estudiante.getApellidos() == null || estudiante.getApellidos().trim().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios");
        }
        if (estudiante.getEmail() == null || estudiante.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (!estudiante.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El email no es válido");
        }
        if (estudiante.getCiclo() == null || estudiante.getCiclo().trim().isEmpty()) {
            throw new IllegalArgumentException("El ciclo es obligatorio");
        }
        if (estudiante.getGrupo() == null || estudiante.getGrupo().trim().isEmpty()) {
            throw new IllegalArgumentException("El grupo es obligatorio");
        }
    }

    public boolean existeEmail(String email, Long idExcluir) {
        Optional<Estudiante> existente = estudianteRepository.findByEmail(email);
        if (existente.isEmpty()) {
            return false;
        }
        // Si es el mismo estudiante que estamos editando, no hay conflicto
        return !existente.get().getId().equals(idExcluir);
    }

    /**
     * Obtiene todos los estudiantes activos.
     */
    public List<Estudiante> obtenerActivos() {
        return estudianteRepository.findByActivoTrue();
    }
}