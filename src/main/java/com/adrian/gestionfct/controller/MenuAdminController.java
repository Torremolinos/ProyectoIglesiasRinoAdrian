package com.adrian.gestionfct.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrian.gestionfct.config.StageManager;
import com.adrian.gestionfct.modelo.EstadoFCT;
import com.adrian.gestionfct.modelo.Rol;
import com.adrian.gestionfct.modelo.Usuario;
import com.adrian.gestionfct.services.*;
import com.adrian.gestionfct.view.FxmlView;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

/**
 * Controlador del panel de Administración.
 */
@Controller
public class MenuAdminController implements Initializable {

	@FXML
	private Label lblUsuario;

	@FXML
	private Label lblTotalEstudiantes;

	@FXML
	private Label lblTotalEmpresas;

	@FXML
	private Label lblFCTActivas;

	@FXML
	private Label lblTotalDocentes;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private EstudianteService estudianteService;

	@Autowired
	private EmpresaService empresaService;

	@Autowired
	private FCTService fctService;

	@Lazy
	@Autowired
	private StageManager stageManager;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Mostrar usuario actual
		Usuario usuario = usuarioService.getUsuarioActual();
		if (usuario != null && lblUsuario != null) {
			lblUsuario.setText("Usuario: " + usuario.getNombreCompleto());
		}

		// Cargar estadísticas
		cargarEstadisticas();
	}

	private void cargarEstadisticas() {
		try {
			if (lblTotalEstudiantes != null) {
				lblTotalEstudiantes.setText(String.valueOf(estudianteService.obtenerTodos().size()));
			}
			if (lblTotalEmpresas != null) {
				lblTotalEmpresas.setText(String.valueOf(empresaService.findAll().size()));
			}
			if (lblFCTActivas != null) {
				lblFCTActivas.setText(String.valueOf(fctService.findByEstado(EstadoFCT.ACTIVA).size()));
			}
			if (lblTotalDocentes != null) {
				lblTotalDocentes.setText(String.valueOf(usuarioService.findByRol(Rol.DOCENTE).size()));
			}
		} catch (Exception e) {
			System.err.println("Error cargando estadísticas: " + e.getMessage());
		}
	}

	@FXML
	private void handleInicio(ActionEvent event) {
		cargarEstadisticas();
	}

	@FXML
	private void handleGestionUsuarios(ActionEvent event) {
		stageManager.switchScene(FxmlView.GESTION_USUARIOS);
	}

	@FXML
	private void handleGestionEmpresas(ActionEvent event) {
		stageManager.switchScene(FxmlView.GESTION_EMPRESAS);
	}

	@FXML
	private void handleGestionTutores(ActionEvent event) {
		mostrarEnDesarrollo("Gestión de Tutores");
	}

	@FXML
	private void handleGestionEstudiantes(ActionEvent event) {
		stageManager.switchScene(FxmlView.GESTION_ESTUDIANTES);
	}

	@FXML
	private void handleGestionDocentes(ActionEvent event) {
		mostrarEnDesarrollo("Gestión de Docentes");
	}

	@FXML
	private void handleAsignarFCT(ActionEvent event) {
		stageManager.switchScene(FxmlView.ASIGNAR_FCT);
	}

	@FXML
	private void handleGestionDocumentos(ActionEvent event) {
		stageManager.switchScene(FxmlView.GESTION_DOCUMENTOS);
	}

	@FXML
	private void handleGestionCursos(ActionEvent event) {
		mostrarEnDesarrollo("Gestión de Cursos Académicos");
	}

	@FXML
	private void handleGestionPeriodos(ActionEvent event) {
		mostrarEnDesarrollo("Gestión de Periodos FCT");
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
	

	private void mostrarEnDesarrollo(String modulo) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Módulo en desarrollo");
		alert.setHeaderText(modulo);
		alert.setContentText("Esta funcionalidad estará disponible próximamente.");
		alert.showAndWait();
	}
	
	
	
}