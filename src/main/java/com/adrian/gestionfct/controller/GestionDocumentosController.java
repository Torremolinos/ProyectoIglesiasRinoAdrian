package com.adrian.gestionfct.controller;

import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrian.gestionfct.config.StageManager;
import com.adrian.gestionfct.exception.ValidacionException;
import com.adrian.gestionfct.modelo.*;
import com.adrian.gestionfct.services.*;
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
import javafx.stage.FileChooser;

/**
 * Controlador para la gestión de Documentos FCT.
 * Permite subir, visualizar y gestionar documentos asociados a las FCT.
 * 
 * @author Adrián Iglesias Rino
 */
@Controller
public class GestionDocumentosController implements Initializable {

    // ============== COMPONENTES FXML ==============
    @FXML private TableView<Documento> tablaDocumentos;
    @FXML private TableColumn<Documento, Long> colId;
    @FXML private TableColumn<Documento, String> colNombre;
    @FXML private TableColumn<Documento, String> colTipo;
    @FXML private TableColumn<Documento, String> colFCT;
    @FXML private TableColumn<Documento, String> colFecha;
    @FXML private TableColumn<Documento, String> colTamano;
    @FXML private TableColumn<Documento, String> colAutor;

    @FXML private ComboBox<TipoDocumento> cmbFiltroTipo;
    @FXML private ComboBox<FCT> cmbFiltroFCT;
    @FXML private TextField txtBuscar;
    @FXML private Label lblContador;

    @FXML private Button btnSubir;
    @FXML private Button btnDescargar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVerDetalles;
    @FXML private Button btnVolver;

    // ============== SERVICIOS ==============
    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private FCTService fctService;

    @Autowired
    private UsuarioService usuarioService;

    @Lazy
    @Autowired
    private StageManager stageManager;

    // ============== DATOS ==============
    private ObservableList<Documento> listaDocumentos = FXCollections.observableArrayList();
    private List<Documento> todosLosDocumentos;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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
        
        colTipo.setCellValueFactory(cellData -> {
            TipoDocumento tipo = cellData.getValue().getTipo();
            return new SimpleStringProperty(tipo != null ? tipo.name() : "N/A");
        });
        
        colFCT.setCellValueFactory(cellData -> {
            FCT fct = cellData.getValue().getFct();
            if (fct != null && fct.getEstudiante() != null) {
                return new SimpleStringProperty(fct.getEstudiante().getNombreCompleto());
            }
            return new SimpleStringProperty("N/A");
        });
        
        colFecha.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(
                cellData.getValue().getFechaSubida().format(DATE_FORMATTER)
            );
        });
        
        colTamano.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getTamanoFormateado());
        });
        
        colAutor.setCellValueFactory(cellData -> {
            Usuario autor = cellData.getValue().getAutor();
            return new SimpleStringProperty(autor != null ? autor.getNombreCompleto() : "N/A");
        });

        // Estilo para columna tipo
        colTipo.setCellFactory(column -> new TableCell<Documento, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "ANEXO" -> setStyle("-fx-text-fill: #3498db;");
                        case "INFORME" -> setStyle("-fx-text-fill: #27ae60;");
                        case "EVALUACION" -> setStyle("-fx-text-fill: #e74c3c;");
                        case "MEMORIA" -> setStyle("-fx-text-fill: #9b59b6;");
                        default -> setStyle("");
                    }
                }
            }
        });

        tablaDocumentos.setItems(listaDocumentos);
        tablaDocumentos.setPlaceholder(new Label("No hay documentos para mostrar"));
    }

    private void configurarFiltros() {
        // Filtro de tipo
        cmbFiltroTipo.setItems(FXCollections.observableArrayList(TipoDocumento.values()));
        cmbFiltroTipo.setPromptText("Todos los tipos");
        cmbFiltroTipo.setOnAction(e -> aplicarFiltros());

        // Filtro de FCT
        List<FCT> fcts = fctService.findAll();
        cmbFiltroFCT.setItems(FXCollections.observableArrayList(fcts));
        cmbFiltroFCT.setPromptText("Todas las FCT");
        cmbFiltroFCT.setOnAction(e -> aplicarFiltros());
    }

    private void configurarEventos() {
        // Doble clic para ver detalles
        tablaDocumentos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleVerDetalles(null);
            }
        });

        // Búsqueda en tiempo real
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());

        // Habilitar/deshabilitar botones según selección
        tablaDocumentos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean haySeleccion = newSel != null;
            btnDescargar.setDisable(!haySeleccion);
            btnEliminar.setDisable(!haySeleccion);
            btnVerDetalles.setDisable(!haySeleccion);
        });

        // Estado inicial de botones
        btnDescargar.setDisable(true);
        btnEliminar.setDisable(true);
        btnVerDetalles.setDisable(true);
    }

    // ============== CARGA DE DATOS ==============
    private void cargarDatos() {
        try {
            todosLosDocumentos = documentoService.findAll();
            aplicarFiltros();
        } catch (Exception e) {
            mostrarError("Error al cargar datos", e.getMessage());
        }
    }

    private void aplicarFiltros() {
        if (todosLosDocumentos == null) return;

        String busqueda = txtBuscar.getText().toLowerCase().trim();
        TipoDocumento tipoFiltro = cmbFiltroTipo.getValue();
        FCT fctFiltro = cmbFiltroFCT.getValue();

        List<Documento> filtrados = todosLosDocumentos.stream()
            .filter(doc -> {
                // Filtro por tipo
                if (tipoFiltro != null && doc.getTipo() != tipoFiltro) return false;
                
                // Filtro por FCT
                if (fctFiltro != null && !fctFiltro.equals(doc.getFct())) return false;
                
                // Filtro por búsqueda
                if (!busqueda.isEmpty()) {
                    return doc.getNombre().toLowerCase().contains(busqueda) ||
                           (doc.getDescripcion() != null && 
                            doc.getDescripcion().toLowerCase().contains(busqueda));
                }
                return true;
            })
            .collect(Collectors.toList());

        listaDocumentos.setAll(filtrados);
        actualizarContador();
    }

    private void actualizarContador() {
        int total = todosLosDocumentos != null ? todosLosDocumentos.size() : 0;
        int mostrados = listaDocumentos.size();
        lblContador.setText(String.format("Mostrando %d de %d documentos", mostrados, total));
    }

    // ============== ACCIONES ==============
    @FXML
    private void handleSubir(ActionEvent event) {
        mostrarDialogoSubida();
    }

    @FXML
    private void handleDescargar(ActionEvent event) {
        Documento seleccionado = tablaDocumentos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        // Simulación de descarga (en un entorno real se abriría el archivo)
        mostrarInfo("Descarga", 
            "Documento: " + seleccionado.getNombre() + "\n" +
            "Ruta: " + seleccionado.getRuta() + "\n\n" +
            "En un entorno real, aquí se abriría o descargaría el archivo.");
    }

    @FXML
    private void handleEliminar(ActionEvent event) {
        Documento seleccionado = tablaDocumentos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar documento?");
        confirmacion.setContentText("Se eliminará el documento: " + seleccionado.getNombre() + 
                                   "\n\nEsta acción no se puede deshacer.");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    documentoService.delete(seleccionado);
                    cargarDatos();
                    mostrarExito("Documento eliminado", 
                        "El documento ha sido eliminado correctamente.");
                } catch (Exception e) {
                    mostrarError("Error al eliminar", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleVerDetalles(ActionEvent event) {
        Documento seleccionado = tablaDocumentos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        Alert detalles = new Alert(Alert.AlertType.INFORMATION);
        detalles.setTitle("Detalles del Documento");
        detalles.setHeaderText(seleccionado.getNombre());

        StringBuilder sb = new StringBuilder();
        sb.append("═══ INFORMACIÓN GENERAL ═══\n");
        sb.append("Nombre: ").append(seleccionado.getNombre()).append("\n");
        sb.append("Tipo: ").append(seleccionado.getTipo()).append("\n");
        sb.append("Tamaño: ").append(seleccionado.getTamanoFormateado()).append("\n");
        sb.append("Extensión: ").append(seleccionado.getExtension()).append("\n\n");

        sb.append("═══ FCT ASOCIADA ═══\n");
        if (seleccionado.getFct() != null) {
            FCT fct = seleccionado.getFct();
            sb.append("Estudiante: ").append(fct.getEstudiante().getNombreCompleto()).append("\n");
            sb.append("Empresa: ").append(fct.getEmpresa().getNombre()).append("\n");
        }
        sb.append("\n");

        sb.append("═══ METADATOS ═══\n");
        sb.append("Fecha subida: ").append(seleccionado.getFechaSubida().format(DATE_FORMATTER)).append("\n");
        sb.append("Autor: ").append(seleccionado.getAutor() != null ? 
            seleccionado.getAutor().getNombreCompleto() : "N/A").append("\n");
        sb.append("Ruta: ").append(seleccionado.getRuta()).append("\n");

        if (seleccionado.getDescripcion() != null && !seleccionado.getDescripcion().isEmpty()) {
            sb.append("\n═══ DESCRIPCIÓN ═══\n");
            sb.append(seleccionado.getDescripcion());
        }

        detalles.setContentText(sb.toString());
        detalles.getDialogPane().setPrefWidth(450);
        detalles.showAndWait();
    }

    @FXML
    private void handleLimpiarFiltros(ActionEvent event) {
        txtBuscar.clear();
        cmbFiltroTipo.setValue(null);
        cmbFiltroFCT.setValue(null);
        aplicarFiltros();
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        stageManager.switchScene(FxmlView.MENU_ADMIN);
    }

    // ============== DIÁLOGO DE SUBIDA ==============
    private void mostrarDialogoSubida() {
        Dialog<Documento> dialog = new Dialog<>();
        dialog.setTitle("Subir Documento");
        dialog.setHeaderText("Subir nuevo documento a una FCT");

        ButtonType btnGuardar = new ButtonType("Subir", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Selector de archivo
        TextField txtArchivo = new TextField();
        txtArchivo.setPromptText("Seleccionar archivo...");
        txtArchivo.setEditable(false);
        txtArchivo.setPrefWidth(250);
        
        Button btnSeleccionar = new Button("Examinar...");
        final File[] archivoSeleccionado = {null};
        
        btnSeleccionar.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar documento");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*"),
                new FileChooser.ExtensionFilter("PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Documentos", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
            );
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                archivoSeleccionado[0] = file;
                txtArchivo.setText(file.getName());
            }
        });

        // ComboBox de FCT
        ComboBox<FCT> cmbFCT = new ComboBox<>();
        List<FCT> fctsActivas = fctService.findActivas();
        cmbFCT.setItems(FXCollections.observableArrayList(fctsActivas));
        cmbFCT.setPromptText("Seleccionar FCT *");
        cmbFCT.setPrefWidth(300);

        // ComboBox de tipo
        ComboBox<TipoDocumento> cmbTipo = new ComboBox<>();
        cmbTipo.setItems(FXCollections.observableArrayList(TipoDocumento.values()));
        cmbTipo.setPromptText("Tipo de documento *");
        cmbTipo.setPrefWidth(300);

        // Descripción
        TextArea txtDescripcion = new TextArea();
        txtDescripcion.setPromptText("Descripción del documento...");
        txtDescripcion.setPrefRowCount(3);
        txtDescripcion.setPrefWidth(300);

        grid.add(new Label("Archivo: *"), 0, 0);
        grid.add(txtArchivo, 1, 0);
        grid.add(btnSeleccionar, 2, 0);
        grid.add(new Label("FCT: *"), 0, 1);
        grid.add(cmbFCT, 1, 1, 2, 1);
        grid.add(new Label("Tipo: *"), 0, 2);
        grid.add(cmbTipo, 1, 2, 2, 1);
        grid.add(new Label("Descripción:"), 0, 3);
        grid.add(txtDescripcion, 1, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                try {
                    // Validaciones
                    if (archivoSeleccionado[0] == null) {
                        throw new ValidacionException("archivo", "es obligatorio");
                    }
                    if (cmbFCT.getValue() == null) {
                        throw new ValidacionException("FCT", "es obligatoria");
                    }
                    if (cmbTipo.getValue() == null) {
                        throw new ValidacionException("tipo", "es obligatorio");
                    }

                    Usuario autor = usuarioService.getUsuarioActual();
                    if (autor == null) {
                        throw new ValidacionException("sesión", "No hay usuario activo");
                    }

                    // Crear documento
                    Documento doc = new Documento(
                        archivoSeleccionado[0].getName(),
                        archivoSeleccionado[0].getAbsolutePath(),
                        cmbTipo.getValue(),
                        cmbFCT.getValue(),
                        autor
                    );
                    doc.setTamano(archivoSeleccionado[0].length());
                    doc.setDescripcion(txtDescripcion.getText().trim());

                    return doc;
                } catch (ValidacionException e) {
                    mostrarError("Error de validación", e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Documento> resultado = dialog.showAndWait();
        resultado.ifPresent(doc -> {
            try {
                documentoService.save(doc);
                cargarDatos();
                mostrarExito("Documento subido", 
                    "El documento '" + doc.getNombre() + "' se ha subido correctamente.");
            } catch (Exception e) {
                mostrarError("Error al subir", e.getMessage());
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

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
