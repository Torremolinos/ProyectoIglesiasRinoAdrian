package com.adrian.gestionfct.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adrian.gestionfct.modelo.Empresa;
import com.adrian.gestionfct.modelo.TutorEmpresa;
import com.adrian.gestionfct.modelo.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorEmpresaRepository extends JpaRepository<TutorEmpresa, Long> {

    Optional<TutorEmpresa> findByUsuario(Usuario usuario);

    List<TutorEmpresa> findByEmpresa(Empresa empresa);

    List<TutorEmpresa> findByActivoTrue();

    List<TutorEmpresa> findByEmpresaAndActivoTrue(Empresa empresa);

    Optional<TutorEmpresa> findByDni(String dni);

    boolean existsByDni(String dni);
}
