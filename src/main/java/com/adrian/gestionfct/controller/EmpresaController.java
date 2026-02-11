package com.adrian.gestionfct.controller;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrian.gestionfct.config.StageManager;
import com.adrian.gestionfct.exception.DuplicadoException;
import com.adrian.gestionfct.exception.OperacionNoPermitidaException;
import com.adrian.gestionfct.exception.ValidacionException;
import com.adrian.gestionfct.modelo.Empresa;
import com.adrian.gestionfct.services.EmpresaService;
import com.adrian.gestionfct.services.FCTService;
import com.adrian.gestionfct.view.FxmlView;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * Controlador para la gestión de Empresas.
 * Proporciona funcionalidades CRUD completas con validaciones.
 * 
 * @author Adrián Iglesias Rino
 */
@Controller
public class EmpresaController implements Initializable {

    // ============== COMPONENTES FXML ==============
    @FXML private TableView<Empresa> tablaEmpresas;
    @FXML private TableColumn<Empresa, Long> colId;
    @FXML private TableColumn<Empresa, String> colNombre;
    @FXML private TableColumn<Empresa, String> colNif;
    @FXML private TableColumn<Empresa, String> colEmail;
    @FXML private TableColumn<Empresa, String> colTelefono;
    @FXML private TableColumn<Empresa, String> colLocalidad;
    @FXML private TableColumn<Empresa, String> colEstado;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private Label lblContador;
    @FXML private Label lblTitulo;

    @FXML private Button btnNueva;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVerDetalles;
    @FXML private Button btnVolver;

    // ============== SERVICIOS ==============
    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private FCTService fctService;

    @Lazy
    @Autowired
    private StageManager stageManager;

    // ============== DATOS ==============
    private ObservableList<Empresa> listaEmpresas = FXCollections.observableArrayList();
    private List<Empresa> todasLasEmpresas;

    // ============== REGEX PARA VALIDACIONES ==============
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String NIF_REGEX = "^[A-Z]\\d{7}[A-Z0-9]$|^\\d{8}[A-Z]$";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        configurarFiltros();
        configurarEventos();
        cargarDatos();
    }

    // ============== CONFIGURACIÓN ==============
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNif.setCellValueFactory(new PropertyValueFactory<>("nif"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colLocalidad.setCellValueFactory(new PropertyValueFactory<>("localidad"));
        
        colEstado.setCellValueFactory(cellData -> {
            Boolean activa = cellData.getValue().getActiva();
            return new SimpleStringProperty(activa != null && activa ? "Activa" : "Inactiva");
        });

        // Estilo condicional para estado
        colEstado.setCellFactory(column -> new TableCell<Empresa, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Activa".equals(item)) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });

        tablaEmpresas.setItems(listaEmpresas);
        tablaEmpresas.setPlaceholder(new Label("No hay empresas para mostrar"));
    }

    private void configurarFiltros() {
        cmbFiltroEstado.setItems(FXCollections.observableArrayList("Todas", "Activas", "Inactivas"));
        cmbFiltroEstado.setValue("Todas");
        cmbFiltroEstado.setOnAction(e -> aplicarFiltros());
    }

    private void configurarEventos() {
        // Doble clic para ver detalles
        tablaEmpresas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                verDetalles();
            }
        });

        // Enter en búsqueda
        txtBuscar.setOnAction(e -> aplicarFiltros());

        // Listener para búsqueda en tiempo real
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());

        // Habilitar/deshabilitar botones según selección
        tablaEmpresas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean haySeleccion = newSel != null;
            btnEditar.setDisable(!haySeleccion);
            btnEliminar.setDisable(!haySeleccion);
            btnVerDetalles.setDisable(!haySeleccion);
        });

        // Estado inicial de botones
        btnEditar.setDisable(true);
        btnEliminar.setDisable(true);
        btnVerDetalles.setDisable(true);
    }

    // ============== CARGA DE DATOS ==============
    private void cargarDatos() {
        try {
            todasLasEmpresas = empresaService.findAll();
            aplicarFiltros();
        } catch (Exception e) {
            mostrarError("Error al cargar empresas", e.getMessage());
        }
    }

    private void aplicarFiltros() {
        if (todasLasEmpresas == null) return;

        String busqueda = txtBuscar.getText().toLowerCase().trim();
        String filtroEstado = cmbFiltroEstado.getValue();

        List<Empresa> filtradas = todasLasEmpresas.stream()
            .filter(emp -> {
                // Filtro por estado
                if ("Activas".equals(filtroEstado) && !Boolean.TRUE.equals(emp.getActiva())) return false;
                if ("Inactivas".equals(filtroEstado) && Boolean.TRUE.equals(emp.getActiva())) return false;
                
                // Filtro por búsqueda
                if (!busqueda.isEmpty()) {
                    return emp.getNombre().toLowerCase().contains(busqueda) ||
                           (emp.getNif() != null && emp.getNif().toLowerCase().contains(busqueda)) ||
                           (emp.getLocalidad() != null && emp.getLocalidad().toLowerCase().contains(busqueda));
                }
                return true;
            })
            .collect(Collectors.toList());

        listaEmpresas.setAll(filtradas);
        actualizarContador();
    }

    private void actualizarContador() {
        int total = todasLasEmpresas != null ? todasLasEmpresas.size() : 0;
        int mostradas = listaEmpresas.size();
        lblContador.setText(String.format("Mostrando %d de %d empresas", mostradas, total));
    }

    // ============== ACCIONES CRUD ==============
    @FXML
    private void handleNueva(ActionEvent event) {
        mostrarDialogoEmpresa(null);
    }

    @FXML
    private void handleEditar(ActionEvent event) {
        Empresa seleccionada = tablaEmpresas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            mostrarDialogoEmpresa(seleccionada);
        }
    }

    @FXML
    private void handleEliminar(ActionEvent event) {
        Empresa seleccionada = tablaEmpresas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;

        // Verificar si tiene FCTs asociadas
        if (!fctService.findByEmpresa(seleccionada).isEmpty()) {
            mostrarError("No se puede eliminar", 
                "La empresa tiene FCTs asociadas. Debe eliminarlas o reasignarlas primero.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar empresa?");
        confirmacion.setContentText("Se eliminará la empresa: " + seleccionada.getNombre() + 
                                   "\n\nEsta acción no se puede deshacer.");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    empresaService.delete(seleccionada);
                    cargarDatos();
                    mostrarExito("Empresa eliminada", 
                        "La empresa '" + seleccionada.getNombre() + "' ha sido eliminada correctamente.");
                } catch (Exception e) {
                    mostrarError("Error al eliminar", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleVerDetalles(ActionEvent event) {
        verDetalles();
    }

    private void verDetalles() {
        Empresa seleccionada = tablaEmpresas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;

        Alert detalles = new Alert(Alert.AlertType.INFORMATION);
        detalles.setTitle("Detalles de Empresa");
        detalles.setHeaderText(seleccionada.getNombre());
        
        StringBuilder sb = new StringBuilder();
        sb.append("NIF: ").append(seleccionada.getNif()).append("\n");
        sb.append("Estado: ").append(Boolean.TRUE.equals(seleccionada.getActiva()) ? "Activa" : "Inactiva").append("\n\n");
        
        if (seleccionada.getDireccion() != null) 
            sb.append("Dirección: ").append(seleccionada.getDireccion()).append("\n");
        if (seleccionada.getLocalidad() != null) 
            sb.append("Localidad: ").append(seleccionada.getLocalidad()).append("\n");
        if (seleccionada.getCodigoPostal() != null) 
            sb.append("C.P.: ").append(seleccionada.getCodigoPostal()).append("\n");
        if (seleccionada.getProvincia() != null) 
            sb.append("Provincia: ").append(seleccionada.getProvincia()).append("\n\n");
        
        if (seleccionada.getTelefono() != null) 
            sb.append("Teléfono: ").append(seleccionada.getTelefono()).append("\n");
        if (seleccionada.getEmail() != null) 
            sb.append("Email: ").append(seleccionada.getEmail()).append("\n");
        if (seleccionada.getPersonaContacto() != null) 
            sb.append("Contacto: ").append(seleccionada.getPersonaContacto()).append("\n\n");
        
        if (seleccionada.getSector() != null) 
            sb.append("Sector: ").append(seleccionada.getSector()).append("\n");
        if (seleccionada.getObservaciones() != null) 
            sb.append("\nObservaciones: ").append(seleccionada.getObservaciones());

        // Contar FCTs
        int numFcts = fctService.findByEmpresa(seleccionada).size();
        sb.append("\n\nFCTs asociadas: ").append(numFcts);

        detalles.setContentText(sb.toString());
        detalles.showAndWait();
    }

    @FXML
    private void handleLimpiarFiltros(ActionEvent event) {
        txtBuscar.clear();
        cmbFiltroEstado.setValue("Todas");
        aplicarFiltros();
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        stageManager.switchScene(FxmlView.MENU_ADMIN);
    }

    // ============== DIÁLOGO DE EMPRESA ==============
    private void mostrarDialogoEmpresa(Empresa empresa) {
        boolean esNueva = (empresa == null);
        
        Dialog<Empresa> dialog = new Dialog<>();
        dialog.setTitle(esNueva ? "Nueva Empresa" : "Editar Empresa");
        dialog.setHeaderText(esNueva ? "Introduce los datos de la nueva empresa" : 
                                       "Modifica los datos de la empresa");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        // Crear formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre de la empresa *");
        TextField txtNif = new TextField();
        txtNif.setPromptText("NIF/CIF *");
        TextField txtDireccion = new TextField();
        txtDireccion.setPromptText("Dirección");
        TextField txtLocalidad = new TextField();
        txtLocalidad.setPromptText("Localidad");
        TextField txtCodigoPostal = new TextField();
        txtCodigoPostal.setPromptText("Código Postal");
        TextField txtProvincia = new TextField();
        txtProvincia.setPromptText("Provincia");
        TextField txtTelefono = new TextField();
        txtTelefono.setPromptText("Teléfono");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Email");
        TextField txtContacto = new TextField();
        txtContacto.setPromptText("Persona de contacto");
        TextField txtSector = new TextField();
        txtSector.setPromptText("Sector empresarial");
        TextArea txtObservaciones = new TextArea();
        txtObservaciones.setPromptText("Observaciones");
        txtObservaciones.setPrefRowCount(3);
        CheckBox chkActiva = new CheckBox("Empresa activa");
        chkActiva.setSelected(true);

        // Si es edición, cargar datos
        if (!esNueva) {
            txtNombre.setText(empresa.getNombre());
            txtNif.setText(empresa.getNif());
            txtNif.setDisable(true); // NIF no editable
            txtDireccion.setText(empresa.getDireccion());
            txtLocalidad.setText(empresa.getLocalidad());
            txtCodigoPostal.setText(empresa.getCodigoPostal());
            txtProvincia.setText(empresa.getProvincia());
            txtTelefono.setText(empresa.getTelefono());
            txtEmail.setText(empresa.getEmail());
            txtContacto.setText(empresa.getPersonaContacto());
            txtSector.setText(empresa.getSector());
            txtObservaciones.setText(empresa.getObservaciones());
            chkActiva.setSelected(Boolean.TRUE.equals(empresa.getActiva()));
        }

        grid.add(new Label("Nombre: *"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("NIF/CIF: *"), 0, 1);
        grid.add(txtNif, 1, 1);
        grid.add(new Label("Dirección:"), 0, 2);
        grid.add(txtDireccion, 1, 2);
        grid.add(new Label("Localidad:"), 0, 3);
        grid.add(txtLocalidad, 1, 3);
        grid.add(new Label("C.P.:"), 0, 4);
        grid.add(txtCodigoPostal, 1, 4);
        grid.add(new Label("Provincia:"), 0, 5);
        grid.add(txtProvincia, 1, 5);
        grid.add(new Label("Teléfono:"), 0, 6);
        grid.add(txtTelefono, 1, 6);
        grid.add(new Label("Email:"), 0, 7);
        grid.add(txtEmail, 1, 7);
        grid.add(new Label("Contacto:"), 0, 8);
        grid.add(txtContacto, 1, 8);
        grid.add(new Label("Sector:"), 0, 9);
        grid.add(txtSector, 1, 9);
        grid.add(new Label("Observaciones:"), 0, 10);
        grid.add(txtObservaciones, 1, 10);
        grid.add(chkActiva, 1, 11);

        dialog.getDialogPane().setContent(grid);

        // Convertir resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                try {
                    // Validaciones
                    String nombre = txtNombre.getText().trim();
                    String nif = txtNif.getText().trim().toUpperCase();
                    String email = txtEmail.getText().trim();

                    if (nombre.isEmpty()) {
                        throw new ValidacionException("nombre", "es obligatorio");
                    }
                    if (nif.isEmpty()) {
                        throw new ValidacionException("NIF", "es obligatorio");
                    }
                    
                    // Validar email si se proporciona
                    if (!email.isEmpty() && !email.matches(EMAIL_REGEX)) {
                        throw new ValidacionException("email", "formato inválido");
                    }

                    // Verificar NIF único (solo en nueva empresa)
                    if (esNueva && empresaService.existeNif(nif)) {
                        throw new DuplicadoException("NIF", nif);
                    }

                    // Crear o actualizar empresa
                    Empresa emp = esNueva ? new Empresa() : empresa;
                    emp.setNombre(nombre);
                    if (esNueva) emp.setNif(nif);
                    emp.setDireccion(txtDireccion.getText().trim());
                    emp.setLocalidad(txtLocalidad.getText().trim());
                    emp.setCodigoPostal(txtCodigoPostal.getText().trim());
                    emp.setProvincia(txtProvincia.getText().trim());
                    emp.setTelefono(txtTelefono.getText().trim());
                    emp.setEmail(email.isEmpty() ? null : email);
                    emp.setPersonaContacto(txtContacto.getText().trim());
                    emp.setSector(txtSector.getText().trim());
                    emp.setObservaciones(txtObservaciones.getText().trim());
                    emp.setActiva(chkActiva.isSelected());

                    return emp;
                } catch (ValidacionException | DuplicadoException e) {
                    mostrarError("Error de validación", e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Empresa> resultado = dialog.showAndWait();
        resultado.ifPresent(emp -> {
            try {
                empresaService.save(emp);
                cargarDatos();
                mostrarExito(esNueva ? "Empresa creada" : "Empresa actualizada",
                    "La empresa '" + emp.getNombre() + "' se ha guardado correctamente.");
            } catch (Exception e) {
                mostrarError("Error al guardar", e.getMessage());
            }
        });
    }

    // ============== UTILIDADES ==============
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
