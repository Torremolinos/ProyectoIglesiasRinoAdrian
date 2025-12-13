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

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * Controlador del panel de Tutor de Empresa.
 */
@Controller
public class MenuTutorController implements Initializable {

	@FXML
	private Label lblBienvenida;

	@Autowired
	private UsuarioService usuarioService;

	@Lazy
	@Autowired
	private StageManager stageManager;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Usuario usuario = usuarioService.getUsuarioActual();
		if (usuario != null && lblBienvenida != null) {
			lblBienvenida.setText("Bienvenido/a, " + usuario.getNombreCompleto());
		}
	}

	@FXML
	private void handleMisEstudiantes(ActionEvent event) {
		// TODO: Implementar vista de estudiantes asignados
	}

	@FXML
	private void handleGestionDocumentos(ActionEvent event) {
		stageManager.switchScene(FxmlView.GESTION_DOCUMENTOS);
	}

	@FXML
	private void handleLogout(ActionEvent event) throws IOException {
		usuarioService.logout();
		stageManager.switchScene(FxmlView.LOGIN);
	}

	@FXML
	private void handleSalir(ActionEvent event) {
		Platform.exit();
	}
}
