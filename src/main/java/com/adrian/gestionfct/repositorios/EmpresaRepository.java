package com.adrian.gestionfct.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adrian.gestionfct.modelo.Empresa;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findByNif(String nif);

    Optional<Empresa> findByEmail(String email);

    List<Empresa> findByActivaTrue();

    List<Empresa> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByNif(String nif);

    boolean existsByEmail(String email);
}
