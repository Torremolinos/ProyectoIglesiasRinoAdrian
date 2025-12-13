package com.adrian.gestionfct.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrian.gestionfct.config.StageManager;
import com.adrian.gestionfct.modelo.Usuario;
import com.adrian.gestionfct.services.UsuarioService;
import com.adrian.gestionfct.view.FxmlView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controlador de la pantalla de Login.
 * 
 * Gestiona la autenticación y redirige al panel correspondiente según el rol.
 */
@Controller
public class LoginController implements Initializable {

	@FXML
	private TextField txtEmail;

	@FXML
	private PasswordField txtPassword;

	@FXML
	private Button btnLogin;

	@FXML
	private Label lblMensaje;

	@Autowired
	private UsuarioService usuarioService;

	@Lazy
	@Autowired
	private StageManager stageManager;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Limpiar mensaje al inicio
		if (lblMensaje != null) {
			lblMensaje.setText("");
		}
	}

	/**
	 * Maneja el evento de login. Autentica al usuario y redirige según su rol.
	 */
	@FXML
	private void handleLogin(ActionEvent event) throws IOException {
		String email = txtEmail.getText().trim();
		String password = txtPassword.getText();

		// Validar campos vacíos
		if (email.isEmpty() || password.isEmpty()) {
			mostrarError("Por favor, introduce email y contraseña");
			return;
		}

		// Intentar autenticación
		Usuario usuario = usuarioService.autenticar(email, password);

		if (usuario != null) {
			// Login exitoso - redirigir según rol
			redirigirSegunRol(usuario);
		} else {
			mostrarError("Email o contraseña incorrectos");
		}
	}

	/**
	 * Redirige al panel correspondiente según el rol del usuario.
	 */
	private void redirigirSegunRol(Usuario usuario) {
		switch (usuario.getRol()) {
		case ADMINISTRADOR:
			stageManager.switchScene(FxmlView.MENU_ADMIN);
			break;
		case DOCENTE:
			stageManager.switchScene(FxmlView.MENU_DOCENTE);
			break;
		case TUTOR_EMPRESA:
			stageManager.switchScene(FxmlView.MENU_TUTOR);
			break;
		case ESTUDIANTE:
			stageManager.switchScene(FxmlView.MENU_ESTUDIANTE);
			break;
		default:
			mostrarError("Rol no reconocido");
		}
	}

	/**
	 * Muestra un mensaje de error.
	 */
	private void mostrarError(String mensaje) {
		if (lblMensaje != null) {
			lblMensaje.setText(mensaje);
			lblMensaje.setStyle("-fx-text-fill: red;");
		}
	}

	/**
	 * Limpia los campos del formulario.
	 */
	@FXML
	private void handleLimpiar(ActionEvent event) {
		txtEmail.clear();
		txtPassword.clear();
		if (lblMensaje != null) {
			lblMensaje.setText("");
		}
		txtEmail.requestFocus();
	}

	// Getters para los tests
	public String getEmail() {
		return txtEmail.getText();
	}

	public String getPassword() {
		return txtPassword.getText();
	}
}
