package com.adrian.gestionfct.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrian.gestionfct.config.StageManager;
import com.adrian.gestionfct.modelo.Rol;
import com.adrian.gestionfct.modelo.Usuario;
import com.adrian.gestionfct.services.UsuarioService;
import com.adrian.gestionfct.view.FxmlView;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 * Controlador para la gestión de usuarios. Adaptado para usar la entidad
 * Usuario con Rol como enum.
 */
@Controller
public class UserController implements Initializable {

	@FXML
	private Button btnLogout;

	@FXML
	private Label userId;

	@FXML
	private TextField txtNombre;

	@FXML
	private TextField txtApellidos;

	@FXML
	private TextField txtTelefono;

	@FXML
	private ComboBox<Rol> cbRol;

	@FXML
	private TextField txtEmail;

	@FXML
	private PasswordField txtPassword;

	@FXML
	private Button btnReset;

	@FXML
	private Button btnGuardar;

	@FXML
	private TableView<Usuario> tablaUsuarios;

	@FXML
	private TableColumn<Usuario, Long> colId;

	@FXML
	private TableColumn<Usuario, String> colNombre;

	@FXML
	private TableColumn<Usuario, String> colApellidos;

	@FXML
	private TableColumn<Usuario, String> colEmail;

	@FXML
	private TableColumn<Usuario, String> colTelefono;

	@FXML
	private TableColumn<Usuario, Rol> colRol;

	@FXML
	private TableColumn<Usuario, Boolean> colActivo;

	@FXML
	private TableColumn<Usuario, Boolean> colEditar;

	@FXML
	private MenuItem menuEliminar;

	@Lazy
	@Autowired
	private StageManager stageManager;

	@Autowired
	private UsuarioService usuarioService;

	private ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

	@FXML
	private void exit(ActionEvent event) {
		Platform.exit();
	}

	@FXML
	private void logout(ActionEvent event) throws IOException {
		usuarioService.logout();
		stageManager.switchScene(FxmlView.LOGIN);
	}

	@FXML
	private void reset(ActionEvent event) {
		limpiarCampos();
	}

	@FXML
	private void guardarUsuario(ActionEvent event) {
		if (!validar("Nombre", getNombre(), "[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+"))
			return;
		if (!validar("Apellidos", getApellidos(), "[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+"))
			return;
		if (!validarNoVacio("Rol", getRol() == null))
			return;

		if (userId.getText() == null || userId.getText().isEmpty()) {
			if (!validar("Email", getEmail(), "[a-zA-Z0-9][a-zA-Z0-9._]*@[a-zA-Z0-9]+([.][a-zA-Z]+)+"))
				return;
			if (!validarNoVacio("Contraseña", getPassword().isEmpty()))
				return;

			if (usuarioService.existeEmail(getEmail())) {
				mostrarAlerta(AlertType.WARNING, "Email duplicado", "Ya existe un usuario con ese email.");
				return;
			}

			Usuario usuario = new Usuario();
			usuario.setNombre(getNombre());
			usuario.setApellidos(getApellidos());
			usuario.setEmail(getEmail());
			usuario.setPassword(getPassword());
			usuario.setTelefono(getTelefono());
			usuario.setRol(getRol());
			usuario.setActivo(true);

			Usuario nuevoUsuario = usuarioService.save(usuario);
			mostrarAlertaGuardado(nuevoUsuario);

		} else {
			Optional<Usuario> optUsuario = usuarioService.findById(Long.parseLong(userId.getText()));
			if (optUsuario.isPresent()) {
				Usuario usuario = optUsuario.get();
				usuario.setNombre(getNombre());
				usuario.setApellidos(getApellidos());
				usuario.setTelefono(getTelefono());
				usuario.setRol(getRol());

				if (!getPassword().isEmpty()) {
					usuario.setPassword(getPassword());
				}

				Usuario usuarioActualizado = usuarioService.update(usuario);
				mostrarAlertaActualizado(usuarioActualizado);
			}
		}

		limpiarCampos();
		cargarUsuarios();
	}

	@FXML
	private void eliminarUsuarios(ActionEvent event) {
		List<Usuario> seleccionados = tablaUsuarios.getSelectionModel().getSelectedItems();

		if (seleccionados.isEmpty()) {
			mostrarAlerta(AlertType.WARNING, "Sin selección", "Por favor, selecciona al menos un usuario.");
			return;
		}

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmar eliminación");
		alert.setHeaderText(null);
		alert.setContentText("¿Estás seguro de que deseas eliminar los usuarios seleccionados?");
		Optional<ButtonType> action = alert.showAndWait();

		if (action.isPresent() && action.get() == ButtonType.OK) {
			usuarioService.deleteInBatch(seleccionados);
			cargarUsuarios();
		}
	}

	@FXML
	private void volverMenu(ActionEvent event) {
		Usuario usuarioActual = usuarioService.getUsuarioActual();
		if (usuarioActual != null && usuarioActual.getRol() == Rol.ADMINISTRADOR) {
			stageManager.switchScene(FxmlView.MENU_ADMIN);
		} else {
			stageManager.switchScene(FxmlView.LOGIN);
		}
	}

	private void limpiarCampos() {
		userId.setText(null);
		txtNombre.clear();
		txtApellidos.clear();
		txtEmail.clear();
		txtEmail.setDisable(false);
		txtPassword.clear();
		txtTelefono.clear();
		cbRol.getSelectionModel().clearSelection();
	}

	private void mostrarAlertaGuardado(Usuario usuario) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Usuario guardado");
		alert.setHeaderText(null);
		alert.setContentText(
				"El usuario " + usuario.getNombreCompleto() + " ha sido creado correctamente.\nID: " + usuario.getId());
		alert.showAndWait();
	}

	private void mostrarAlertaActualizado(Usuario usuario) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Usuario actualizado");
		alert.setHeaderText(null);
		alert.setContentText("El usuario " + usuario.getNombreCompleto() + " ha sido actualizado correctamente.");
		alert.showAndWait();
	}

	private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
		Alert alert = new Alert(tipo);
		alert.setTitle(titulo);
		alert.setHeaderText(null);
		alert.setContentText(mensaje);
		alert.showAndWait();
	}

	public String getNombre() {
		return txtNombre.getText().trim();
	}

	public String getApellidos() {
		return txtApellidos.getText().trim();
	}

	public String getTelefono() {
		return txtTelefono.getText().trim();
	}

	public Rol getRol() {
		return cbRol.getSelectionModel().getSelectedItem();
	}

	public String getEmail() {
		return txtEmail.getText().trim();
	}

	public String getPassword() {
		return txtPassword.getText();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cbRol.setItems(FXCollections.observableArrayList(Rol.values()));

		tablaUsuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		configurarColumnas();

		cargarUsuarios();
	}

	private void configurarColumnas() {
		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
		colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
		colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
		colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
		colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

		colEditar.setCellFactory(cellFactory);
	}

	Callback<TableColumn<Usuario, Boolean>, TableCell<Usuario, Boolean>> cellFactory = new Callback<TableColumn<Usuario, Boolean>, TableCell<Usuario, Boolean>>() {

		@Override
		public TableCell<Usuario, Boolean> call(final TableColumn<Usuario, Boolean> param) {
			final TableCell<Usuario, Boolean> cell = new TableCell<Usuario, Boolean>() {
				final Button btnEditar = new Button("Editar");

				@Override
				public void updateItem(Boolean check, boolean empty) {
					super.updateItem(check, empty);
					if (empty) {
						setGraphic(null);
						setText(null);
					} else {
						btnEditar.setOnAction(e -> {
							Usuario usuario = getTableView().getItems().get(getIndex());
							cargarUsuarioEnFormulario(usuario);
						});

						btnEditar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
						setGraphic(btnEditar);
						setAlignment(Pos.CENTER);
						setText(null);
					}
				}

				private void cargarUsuarioEnFormulario(Usuario usuario) {
					userId.setText(Long.toString(usuario.getId()));
					txtNombre.setText(usuario.getNombre());
					txtApellidos.setText(usuario.getApellidos());
					txtEmail.setText(usuario.getEmail());
					txtEmail.setDisable(true); // No permitir cambiar email
					txtTelefono.setText(usuario.getTelefono() != null ? usuario.getTelefono() : "");
					txtPassword.clear(); // No mostrar password
					cbRol.getSelectionModel().select(usuario.getRol());
				}
			};
			return cell;
		}
	};

	private void cargarUsuarios() {
		listaUsuarios.clear();
		listaUsuarios.addAll(usuarioService.findAll());
		tablaUsuarios.setItems(listaUsuarios);
	}

	private boolean validar(String campo, String valor, String patron) {
		if (valor.isEmpty()) {
			mostrarAlertaValidacion(campo, true);
			return false;
		}
		Pattern p = Pattern.compile(patron);
		Matcher m = p.matcher(valor);
		if (m.find() && m.group().equals(valor)) {
			return true;
		} else {
			mostrarAlertaValidacion(campo, false);
			return false;
		}
	}

	private boolean validarNoVacio(String campo, boolean vacio) {
		if (!vacio) {
			return true;
		} else {
			mostrarAlertaValidacion(campo, true);
			return false;
		}
	}

	private void mostrarAlertaValidacion(String campo, boolean vacio) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Error de validación");
		alert.setHeaderText(null);
		if (campo.equals("Rol")) {
			alert.setContentText("Por favor, selecciona un " + campo);
		} else if (vacio) {
			alert.setContentText("Por favor, introduce el campo " + campo);
		} else {
			alert.setContentText("Por favor, introduce un " + campo + " válido");
		}
		alert.showAndWait();
	}
}