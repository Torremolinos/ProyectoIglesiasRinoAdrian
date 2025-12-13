package com.adrian.gestionfct.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrian.gestionfct.modelo.Documento;
import com.adrian.gestionfct.modelo.FCT;
import com.adrian.gestionfct.modelo.TipoDocumento;
import com.adrian.gestionfct.modelo.Usuario;
import com.adrian.gestionfct.repositorios.DocumentoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentoService {

    @Autowired
    private DocumentoRepository documentoRepository;

    public Documento save(Documento documento) {
        return documentoRepository.save(documento);
    }

    public Documento update(Documento documento) {
        return documentoRepository.save(documento);
    }

    public void delete(Documento documento) {
        documentoRepository.delete(documento);
    }

    public void deleteById(Long id) {
        documentoRepository.deleteById(id);
    }

    public Optional<Documento> findById(Long id) {
        return documentoRepository.findById(id);
    }

    public List<Documento> findAll() {
        return documentoRepository.findAll();
    }

    public List<Documento> findByFct(FCT fct) {
        return documentoRepository.findByFct(fct);
    }

    public List<Documento> findByAutor(Usuario autor) {
        return documentoRepository.findByAutor(autor);
    }

    public List<Documento> findByTipo(TipoDocumento tipo) {
        return documentoRepository.findByTipo(tipo);
    }

    public List<Documento> findByFctYTipo(FCT fct, TipoDocumento tipo) {
        return documentoRepository.findByFctAndTipo(fct, tipo);
    }
}
