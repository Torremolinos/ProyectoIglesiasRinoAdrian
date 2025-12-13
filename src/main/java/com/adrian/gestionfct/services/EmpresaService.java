package com.adrian.gestionfct.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrian.gestionfct.modelo.Empresa;
import com.adrian.gestionfct.repositorios.EmpresaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    public Empresa save(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    public Empresa update(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    public void delete(Empresa empresa) {
        empresaRepository.delete(empresa);
    }

    public void deleteById(Long id) {
        empresaRepository.deleteById(id);
    }

    public Optional<Empresa> findById(Long id) {
        return empresaRepository.findById(id);
    }

    public List<Empresa> findAll() {
        return empresaRepository.findAll();
    }

    public List<Empresa> findActivas() {
        return empresaRepository.findByActivaTrue();
    }

    public Optional<Empresa> findByNif(String nif) {
        return empresaRepository.findByNif(nif);
    }

    public List<Empresa> buscarPorNombre(String nombre) {
        return empresaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public boolean existeNif(String nif) {
        return empresaRepository.existsByNif(nif);
    }

    public boolean existeEmail(String email) {
        return empresaRepository.existsByEmail(email);
    }

    public void deleteInBatch(List<Empresa> empresas) {
        empresaRepository.deleteAll(empresas);
    }
}
