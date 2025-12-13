package com.adrian.gestionfct.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adrian.gestionfct.modelo.Rol;
import com.adrian.gestionfct.modelo.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailAndPassword(String email, String password);

    List<Usuario> findByRol(Rol rol);

    List<Usuario> findByActivoTrue();

    List<Usuario> findByRolAndActivoTrue(Rol rol);

    boolean existsByEmail(String email);
}
