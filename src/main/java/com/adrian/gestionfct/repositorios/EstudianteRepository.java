package com.adrian.gestionfct.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adrian.gestionfct.modelo.Estudiante;
import com.adrian.gestionfct.modelo.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    Optional<Estudiante> findByDni(String dni);

    Optional<Estudiante> findByUsuario(Usuario usuario);

    List<Estudiante> findByActivoTrue();

    List<Estudiante> findByCiclo(String ciclo);

    List<Estudiante> findByGrupo(String grupo);

    List<Estudiante> findByProfesorTutor(Usuario profesorTutor);

    List<Estudiante> findByCicloAndGrupo(String ciclo, String grupo);

    boolean existsByDni(String dni);
    
    Optional<Estudiante> findByEmail(String email);

    boolean existsByEmail(String email);


    @Query("SELECT e FROM Estudiante e WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR LOWER(e.apellidos) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Estudiante> buscarPorNombreOApellidos(@Param("busqueda") String busqueda);

    List<Estudiante> findAllByOrderByApellidosAsc();
}
