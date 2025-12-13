package com.adrian.gestionfct.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrian.gestionfct.modelo.Rol;
import com.adrian.gestionfct.modelo.Usuario;
import com.adrian.gestionfct.repositorios.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	// Variable para almacenar el usuario logueado en la sesión
	private Usuario usuarioActual;

	public Usuario save(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	public Usuario update(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	public void delete(Usuario usuario) {
		usuarioRepository.delete(usuario);
	}

	public void deleteById(Long id) {
		usuarioRepository.deleteById(id);
	}

	public Optional<Usuario> findById(Long id) {
		return usuarioRepository.findById(id);
	}

	public List<Usuario> findAll() {
		return usuarioRepository.findAll();
	}

	public Optional<Usuario> findByEmail(String email) {
		return usuarioRepository.findByEmail(email);
	}

	public List<Usuario> findByRol(Rol rol) {
		return usuarioRepository.findByRol(rol);
	}

	public List<Usuario> findActivos() {
		return usuarioRepository.findByActivoTrue();
	}

	public List<Usuario> findByRolActivos(Rol rol) {
		return usuarioRepository.findByRolAndActivoTrue(rol);
	}

	/**
	 * Autentica al usuario por email y contraseña. Retorna el usuario si las
	 * credenciales son correctas, null en caso contrario.
	 */
	public Usuario autenticar(String email, String password) {
		Optional<Usuario> usuario = usuarioRepository.findByEmailAndPassword(email, password);
		if (usuario.isPresent() && usuario.get().getActivo()) {
			Usuario u = usuario.get();
			u.setUltimoAcceso(LocalDateTime.now());
			usuarioRepository.save(u);
			this.usuarioActual = u;
			return u;
		}
		return null;
	}

	/**
	 * Verifica si el email ya existe.
	 */
	public boolean existeEmail(String email) {
		return usuarioRepository.existsByEmail(email);
	}

	/**
	 * Obtiene el usuario actualmente logueado.
	 */
	public Usuario getUsuarioActual() {
		return usuarioActual;
	}

	/**
	 * Establece el usuario actual (para uso en login).
	 */
	public void setUsuarioActual(Usuario usuario) {
		this.usuarioActual = usuario;
	}

	/**
	 * Cierra la sesión del usuario actual.
	 */
	public void logout() {
		this.usuarioActual = null;
	}

	/**
	 * Obtiene los docentes activos.
	 */
	public List<Usuario> findDocentes() {
		return usuarioRepository.findByRolAndActivoTrue(Rol.DOCENTE);
	}

	public void deleteInBatch(List<Usuario> usuarios) {
		usuarioRepository.deleteAll(usuarios);
	}
}
