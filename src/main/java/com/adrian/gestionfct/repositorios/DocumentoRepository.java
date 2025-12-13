package com.adrian.gestionfct.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adrian.gestionfct.modelo.Documento;
import com.adrian.gestionfct.modelo.FCT;
import com.adrian.gestionfct.modelo.TipoDocumento;
import com.adrian.gestionfct.modelo.Usuario;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    List<Documento> findByFct(FCT fct);

    List<Documento> findByAutor(Usuario autor);

    List<Documento> findByTipo(TipoDocumento tipo);

    List<Documento> findByFctAndTipo(FCT fct, TipoDocumento tipo);
}
