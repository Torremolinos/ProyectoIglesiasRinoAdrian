package com.adrian.gestionfct.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrian.gestionfct.modelo.Empresa;
import com.adrian.gestionfct.modelo.TutorEmpresa;
import com.adrian.gestionfct.modelo.Usuario;
import com.adrian.gestionfct.repositorios.TutorEmpresaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TutorEmpresaService {

    @Autowired
    private TutorEmpresaRepository tutorEmpresaRepository;

    public TutorEmpresa save(TutorEmpresa tutorEmpresa) {
        return tutorEmpresaRepository.save(tutorEmpresa);
    }

    public TutorEmpresa update(TutorEmpresa tutorEmpresa) {
        return tutorEmpresaRepository.save(tutorEmpresa);
    }

    public void delete(TutorEmpresa tutorEmpresa) {
        tutorEmpresaRepository.delete(tutorEmpresa);
    }

    public void deleteById(Long id) {
        tutorEmpresaRepository.deleteById(id);
    }

    public Optional<TutorEmpresa> findById(Long id) {
        return tutorEmpresaRepository.findById(id);
    }

    public List<TutorEmpresa> findAll() {
        return tutorEmpresaRepository.findAll();
    }

    public List<TutorEmpresa> findActivos() {
        return tutorEmpresaRepository.findByActivoTrue();
    }

    public Optional<TutorEmpresa> findByUsuario(Usuario usuario) {
        return tutorEmpresaRepository.findByUsuario(usuario);
    }

    public List<TutorEmpresa> findByEmpresa(Empresa empresa) {
        return tutorEmpresaRepository.findByEmpresa(empresa);
    }

    public List<TutorEmpresa> findByEmpresaActivos(Empresa empresa) {
        return tutorEmpresaRepository.findByEmpresaAndActivoTrue(empresa);
    }

    public boolean existeDni(String dni) {
        return tutorEmpresaRepository.existsByDni(dni);
    }

    public void deleteInBatch(List<TutorEmpresa> tutores) {
        tutorEmpresaRepository.deleteAll(tutores);
    }
}
