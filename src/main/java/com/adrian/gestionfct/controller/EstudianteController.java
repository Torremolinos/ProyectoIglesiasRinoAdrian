package com.adrian.gestionfct.controller;

import com.adrian.gestionfct.modelo.Estudiante;
import com.adrian.gestionfct.services.EstudianteService;
import com.adrian.gestionfct.view.FxmlView;
import com.adrian.gestionfct.config.StageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Controller
public class EstudianteController implements Initializable {

    @FXML private TableView<Estudiante> tableEstudiantes;
    @FXML private TableColumn<Estudiante, Long> colId;
    @FXML private TableColumn<Estudiante, String> colNombre;
    @FXML private TableColumn<Estudiante, String> colApellidos;
    @FXML private TableColumn<Estudiante, String> colEmail;
    @FXML private TableColumn<Estudiante, String> colCiclo;
    @FXML private TableColumn<Estudiante, String> colGrupo;
    
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVolver;
    
    @Lazy
    @Autowired
    private StageManager stageManager;
    
    @Autowired
    private EstudianteService estudianteService;
    
    private ObservableList<Estudiante> listaEstudiantes = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarEstudiantes();
        
        // Doble click para editar
        tableEstudiantes.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tableEstudiantes.getSelectionModel().getSelectedItem() != null) {
                handleEditar(null);
            }
        });
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colCiclo.setCellValueFactory(new PropertyValueFactory<>("ciclo"));
        colGrupo.setCellValueFactory(new PropertyValueFactory<>("grupo"));
        
        tableEstudiantes.setItems(listaEstudiantes);
    }

    private void cargarEstudiantes() {
        listaEstudiantes.clear();
        List<Estudiante> estudiantes = estudianteService.obtenerTodos();
        listaEstudiantes.addAll(estudiantes);
    }

    @FXML
    private void handleBuscar(ActionEvent event) {
        String busqueda = txtBuscar.getText().trim();
        if (busqueda.isEmpty()) {
            cargarEstudiantes();
            return;
        }
        
        listaEstudiantes.clear();
        List<Estudiante> resultados = estudianteService.buscarPorNombreOApellidos(busqueda);
        listaEstudiantes.addAll(resultados);
        
        if (resultados.isEmpty()) {
            mostrarInfo("Sin resultados", "No se encontraron estudiantes con ese criterio.");
        }
    }

    @FXML
    private void handleLimpiar(ActionEvent event) {
        txtBuscar.clear();
        cargarEstudiantes();
    }

    @FXML
    private void handleNuevo(ActionEvent event) {
        Dialog<Estudiante> dialog = crearDialogoEstudiante(null);
        Optional<Estudiante> resultado = dialog.showAndWait();
        
        resultado.ifPresent(estudiante -> {
            try {
                estudianteService.guardar(estudiante);
                mostrarExito("Estudiante creado", "El estudiante se ha creado correctamente.");
                cargarEstudiantes();
            } catch (Exception e) {
                mostrarError("Error al crear", "No se pudo crear el estudiante: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleEditar(ActionEvent event) {
        Estudiante seleccionado = tableEstudiantes.getSelectionModel().getSelectedItem();
        
        if (seleccionado == null) {
            mostrarAdvertencia("Ningún estudiante seleccionado", 
                "Por favor, selecciona un estudiante de la tabla.");
            return;
        }
        
        Dialog<Estudiante> dialog = crearDialogoEstudiante(seleccionado);
        Optional<Estudiante> resultado = dialog.showAndWait();
        
        resultado.ifPresent(estudiante -> {
            try {
                estudianteService.guardar(estudiante);
                mostrarExito("Estudiante actualizado", "Los datos se han actualizado correctamente.");
                cargarEstudiantes();
            } catch (Exception e) {
                mostrarError("Error al actualizar", "No se pudo actualizar el estudiante: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleEliminar(ActionEvent event) {
        Estudiante seleccionado = tableEstudiantes.getSelectionModel().getSelectedItem();
        
        if (seleccionado == null) {
            mostrarAdvertencia("Ningún estudiante seleccionado", 
                "Por favor, selecciona un estudiante de la tabla.");
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar estudiante?");
        confirmacion.setContentText("¿Estás seguro de eliminar a " + 
            seleccionado.getNombre() + " " + seleccionado.getApellidos() + "?");
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                estudianteService.eliminar(seleccionado.getId());
                mostrarExito("Estudiante eliminado", "El estudiante se ha eliminado correctamente.");
                cargarEstudiantes();
            } catch (Exception e) {
                mostrarError("Error al eliminar", "No se pudo eliminar el estudiante: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        stageManager.switchScene(FxmlView.MENU_ADMIN);
    }

    private Dialog<Estudiante> crearDialogoEstudiante(Estudiante estudiante) {
        boolean esNuevo = (estudiante == null);
        Estudiante est = esNuevo ? new Estudiante() : estudiante;
        
        Dialog<Estudiante> dialog = new Dialog<>();
        dialog.setTitle(esNuevo ? "Nuevo Estudiante" : "Editar Estudiante");
        dialog.setHeaderText(esNuevo ? "Introduce los datos del nuevo estudiante" : 
            "Modifica los datos del estudiante");
        
        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField txtNombre = new TextField(est.getNombre() != null ? est.getNombre() : "");
        TextField txtApellidos = new TextField(est.getApellidos() != null ? est.getApellidos() : "");
        TextField txtEmail = new TextField(est.getEmail() != null ? est.getEmail() : "");
        TextField txtTelefono = new TextField(est.getTelefono() != null ? est.getTelefono() : "");
        TextField txtCiclo = new TextField(est.getCiclo() != null ? est.getCiclo() : "");
        TextField txtGrupo = new TextField(est.getGrupo() != null ? est.getGrupo() : "");
        
        txtNombre.setPromptText("Nombre");
        txtApellidos.setPromptText("Apellidos");
        txtEmail.setPromptText("email@example.com");
        txtTelefono.setPromptText("666123456");
        txtCiclo.setPromptText("DAW, DAM, ASIR...");
        txtGrupo.setPromptText("1A, 2B...");
        
        grid.add(new Label("Nombre:*"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Apellidos:*"), 0, 1);
        grid.add(txtApellidos, 1, 1);
        grid.add(new Label("Email:*"), 0, 2);
        grid.add(txtEmail, 1, 2);
        grid.add(new Label("Teléfono:"), 0, 3);
        grid.add(txtTelefono, 1, 3);
        grid.add(new Label("Ciclo:*"), 0, 4);
        grid.add(txtCiclo, 1, 4);
        grid.add(new Label("Grupo:*"), 0, 5);
        grid.add(txtGrupo, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                est.setNombre(txtNombre.getText().trim());
                est.setApellidos(txtApellidos.getText().trim());
                est.setEmail(txtEmail.getText().trim());
                est.setTelefono(txtTelefono.getText().trim());
                est.setCiclo(txtCiclo.getText().trim());
                est.setGrupo(txtGrupo.getText().trim());
                return est;
            }
            return null;
        });
        
        return dialog;
    }

    private void mostrarExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}