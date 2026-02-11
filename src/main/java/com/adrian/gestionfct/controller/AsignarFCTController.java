package com.adrian.gestionfct.controller;

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
import com.adrian.gestionfct.exception.OperacionNoPermitidaException;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controlador para la asignación de FCT a estudiantes.
 * Permite crear, editar y gestionar las asignaciones de Formación en Centro de Trabajo.
 * 
 * @author Adrián Iglesias Rino
 */
@Controller
public class AsignarFCTController implements Initializable {

    // ============== COMPONENTES FXML ==============
    @FXML private TableView<FCT> tablaFCTs;
    @FXML private TableColumn<FCT, Long> colId;
    @FXML private TableColumn<FCT, String> colEstudiante;
    @FXML private TableColumn<FCT, String> colEmpresa;
    @FXML private TableColumn<FCT, String> colTutor;
    @FXML private TableColumn<FCT, String> colPeriodo;
    @FXML private TableColumn<FCT, String> colEstado;
    @FXML private TableColumn<FCT, String> colProgreso;

    @FXML private ComboBox<EstadoFCT> cmbFiltroEstado;
    @FXML private ComboBox<CursoAcademico> cmbFiltroCurso;
    @FXML private TextField txtBuscar;
    @FXML private Label lblContador;

    @FXML private Button btnNuevaAsignacion;
    @FXML private Button btnEditar;
    @FXML private Button btnFinalizar;
    @FXML private Button btnCancelar;
    @FXML private Button btnVerDetalles;
    @FXML private Button btnVolver;

    // ============== SERVICIOS ==============
    @Autowired
    private FCTService fctService;

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private TutorEmpresaService tutorEmpresaService;

    @Autowired
    private PeriodoService periodoService;

    @Autowired
    private CursoAcademicoService cursoAcademicoService;

    @Lazy
    @Autowired
    private StageManager stageManager;

    // ============== DATOS ==============
    private ObservableList<FCT> listaFCTs = FXCollections.observableArrayList();
    private List<FCT> todasLasFCTs;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
        
        colEstudiante.setCellValueFactory(cellData -> {
            Estudiante est = cellData.getValue().getEstudiante();
            return new SimpleStringProperty(est != null ? est.getNombreCompleto() : "N/A");
        });
        
        colEmpresa.setCellValueFactory(cellData -> {
            Empresa emp = cellData.getValue().getEmpresa();
            return new SimpleStringProperty(emp != null ? emp.getNombre() : "N/A");
        });
        
        colTutor.setCellValueFactory(cellData -> {
            TutorEmpresa tutor = cellData.getValue().getTutorEmpresa();
            return new SimpleStringProperty(tutor != null ? tutor.getNombreCompleto() : "N/A");
        });
        
        colPeriodo.setCellValueFactory(cellData -> {
            Periodo periodo = cellData.getValue().getPeriodo();
            return new SimpleStringProperty(periodo != null ? periodo.getNombre() : "N/A");
        });
        
        colEstado.setCellValueFactory(cellData -> {
            EstadoFCT estado = cellData.getValue().getEstado();
            return new SimpleStringProperty(estado != null ? estado.name() : "N/A");
        });
        
        // Columna de progreso
        colProgreso.setCellValueFactory(cellData -> {
            FCT fct = cellData.getValue();
            int realizadas = fct.getHorasRealizadas() != null ? fct.getHorasRealizadas() : 0;
            int totales = fct.getHorasTotales() != null ? fct.getHorasTotales() : 0;
            double porcentaje = totales > 0 ? (realizadas * 100.0 / totales) : 0;
            return new SimpleStringProperty(String.format("%d/%d h (%.0f%%)", realizadas, totales, porcentaje));
        });

        // Estilo condicional para estado
        colEstado.setCellFactory(column -> new TableCell<FCT, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "ACTIVA" -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        case "FINALIZADA" -> setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                        case "CANCELADA" -> setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        default -> setStyle("");
                    }
                }
            }
        });

        tablaFCTs.setItems(listaFCTs);
        tablaFCTs.setPlaceholder(new Label("No hay asignaciones FCT para mostrar"));
    }

    private void configurarFiltros() {
        // Filtro de estado
        cmbFiltroEstado.setItems(FXCollections.observableArrayList(EstadoFCT.values()));
        cmbFiltroEstado.setPromptText("Todos los estados");
        cmbFiltroEstado.setOnAction(e -> aplicarFiltros());

        // Filtro de curso académico
        List<CursoAcademico> cursos = cursoAcademicoService.findAll();
        cmbFiltroCurso.setItems(FXCollections.observableArrayList(cursos));
        cmbFiltroCurso.setPromptText("Todos los cursos");
        cmbFiltroCurso.setOnAction(e -> aplicarFiltros());
    }

    private void configurarEventos() {
        // Doble clic para ver detalles
        tablaFCTs.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleVerDetalles(null);
            }
        });

        // Búsqueda en tiempo real
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());

        // Habilitar/deshabilitar botones según selección
        tablaFCTs.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean haySeleccion = newSel != null;
            btnEditar.setDisable(!haySeleccion);
            btnVerDetalles.setDisable(!haySeleccion);
            
            // Finalizar y Cancelar solo para FCTs activas
            boolean esActiva = haySeleccion && newSel.getEstado() == EstadoFCT.ACTIVA;
            btnFinalizar.setDisable(!esActiva);
            btnCancelar.setDisable(!esActiva);
        });

        // Estado inicial de botones
        btnEditar.setDisable(true);
        btnFinalizar.setDisable(true);
        btnCancelar.setDisable(true);
        btnVerDetalles.setDisable(true);
    }

    // ============== CARGA DE DATOS ==============
    private void cargarDatos() {
        try {
            todasLasFCTs = fctService.findAll();
            aplicarFiltros();
        } catch (Exception e) {
            mostrarError("Error al cargar datos", e.getMessage());
        }
    }

    private void aplicarFiltros() {
        if (todasLasFCTs == null) return;

        String busqueda = txtBuscar.getText().toLowerCase().trim();
        EstadoFCT estadoFiltro = cmbFiltroEstado.getValue();
        CursoAcademico cursoFiltro = cmbFiltroCurso.getValue();

        List<FCT> filtradas = todasLasFCTs.stream()
            .filter(fct -> {
                // Filtro por estado
                if (estadoFiltro != null && fct.getEstado() != estadoFiltro) return false;
                
                // Filtro por curso académico
                if (cursoFiltro != null && !cursoFiltro.equals(fct.getCursoAcademico())) return false;
                
                // Filtro por búsqueda (estudiante o empresa)
                if (!busqueda.isEmpty()) {
                    String nombreEstudiante = fct.getEstudiante() != null ? 
                        fct.getEstudiante().getNombreCompleto().toLowerCase() : "";
                    String nombreEmpresa = fct.getEmpresa() != null ? 
                        fct.getEmpresa().getNombre().toLowerCase() : "";
                    return nombreEstudiante.contains(busqueda) || nombreEmpresa.contains(busqueda);
                }
                return true;
            })
            .collect(Collectors.toList());

        listaFCTs.setAll(filtradas);
        actualizarContador();
    }

    private void actualizarContador() {
        int total = todasLasFCTs != null ? todasLasFCTs.size() : 0;
        int mostradas = listaFCTs.size();
        lblContador.setText(String.format("Mostrando %d de %d asignaciones", mostradas, total));
    }

    // ============== ACCIONES ==============
    @FXML
    private void handleNuevaAsignacion(ActionEvent event) {
        mostrarDialogoAsignacion(null);
    }

    @FXML
    private void handleEditar(ActionEvent event) {
        FCT seleccionada = tablaFCTs.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            if (seleccionada.getEstado() != EstadoFCT.ACTIVA) {
                mostrarAdvertencia("No editable", 
                    "Solo se pueden editar asignaciones en estado ACTIVA.");
                return;
            }
            mostrarDialogoAsignacion(seleccionada);
        }
    }

    @FXML
    private void handleFinalizar(ActionEvent event) {
        FCT seleccionada = tablaFCTs.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Finalizar FCT");
        confirmacion.setHeaderText("¿Finalizar esta asignación?");
        confirmacion.setContentText("Estudiante: " + seleccionada.getEstudiante().getNombreCompleto() +
                                   "\nEmpresa: " + seleccionada.getEmpresa().getNombre() +
                                   "\n\n¿Confirma la finalización de esta FCT?");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    seleccionada.finalizar();
                    fctService.update(seleccionada);
                    cargarDatos();
                    mostrarExito("FCT Finalizada", 
                        "La asignación ha sido marcada como finalizada correctamente.");
                } catch (Exception e) {
                    mostrarError("Error", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        FCT seleccionada = tablaFCTs.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;

        // Pedir motivo de cancelación
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cancelar FCT");
        dialog.setHeaderText("Cancelar asignación");
        dialog.setContentText("Motivo de cancelación:");

        dialog.showAndWait().ifPresent(motivo -> {
            if (!motivo.trim().isEmpty()) {
                try {
                    seleccionada.cancelar();
                    seleccionada.setObservaciones(
                        (seleccionada.getObservaciones() != null ? seleccionada.getObservaciones() + "\n" : "") +
                        "CANCELACIÓN: " + motivo
                    );
                    fctService.update(seleccionada);
                    cargarDatos();
                    mostrarExito("FCT Cancelada", 
                        "La asignación ha sido cancelada correctamente.");
                } catch (Exception e) {
                    mostrarError("Error", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleVerDetalles(ActionEvent event) {
        FCT seleccionada = tablaFCTs.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;

        Alert detalles = new Alert(Alert.AlertType.INFORMATION);
        detalles.setTitle("Detalles de FCT");
        detalles.setHeaderText("Asignación #" + seleccionada.getId());

        StringBuilder sb = new StringBuilder();
        sb.append("═══ ESTUDIANTE ═══\n");
        sb.append("Nombre: ").append(seleccionada.getEstudiante().getNombreCompleto()).append("\n");
        sb.append("Ciclo: ").append(seleccionada.getEstudiante().getCiclo()).append(" - ")
          .append(seleccionada.getEstudiante().getGrupo()).append("\n\n");

        sb.append("═══ EMPRESA ═══\n");
        sb.append("Nombre: ").append(seleccionada.getEmpresa().getNombre()).append("\n");
        sb.append("NIF: ").append(seleccionada.getEmpresa().getNif()).append("\n");
        if (seleccionada.getTutorEmpresa() != null) {
            sb.append("Tutor: ").append(seleccionada.getTutorEmpresa().getNombreCompleto()).append("\n");
        }
        sb.append("\n");

        sb.append("═══ PERIODO ═══\n");
        sb.append("Periodo: ").append(seleccionada.getPeriodo().getNombre()).append("\n");
        sb.append("Fechas: ").append(seleccionada.getFechaInicio().format(DATE_FORMATTER))
          .append(" - ").append(seleccionada.getFechaFin().format(DATE_FORMATTER)).append("\n");
        sb.append("Progreso: ").append(seleccionada.getHorasRealizadas())
          .append("/").append(seleccionada.getHorasTotales()).append(" horas (")
          .append(String.format("%.1f%%", seleccionada.getPorcentajeCompletado())).append(")\n\n");

        sb.append("═══ ESTADO ═══\n");
        sb.append("Estado: ").append(seleccionada.getEstado()).append("\n");
        if (seleccionada.getObservaciones() != null && !seleccionada.getObservaciones().isEmpty()) {
            sb.append("\nObservaciones: ").append(seleccionada.getObservaciones());
        }

        detalles.setContentText(sb.toString());
        detalles.getDialogPane().setPrefWidth(450);
        detalles.showAndWait();
    }

    @FXML
    private void handleLimpiarFiltros(ActionEvent event) {
        txtBuscar.clear();
        cmbFiltroEstado.setValue(null);
        cmbFiltroCurso.setValue(null);
        aplicarFiltros();
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        stageManager.switchScene(FxmlView.MENU_ADMIN);
    }

    // ============== DIÁLOGO DE ASIGNACIÓN ==============
    private void mostrarDialogoAsignacion(FCT fct) {
        boolean esNueva = (fct == null);

        Dialog<FCT> dialog = new Dialog<>();
        dialog.setTitle(esNueva ? "Nueva Asignación FCT" : "Editar Asignación FCT");
        dialog.setHeaderText(esNueva ? "Crear nueva asignación de FCT" : "Modificar asignación existente");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        // Crear formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // ComboBox de Estudiantes
        ComboBox<Estudiante> cmbEstudiante = new ComboBox<>();
        cmbEstudiante.setItems(FXCollections.observableArrayList(
            estudianteService.obtenerActivos()));
        cmbEstudiante.setPromptText("Seleccionar estudiante *");
        cmbEstudiante.setPrefWidth(300);

        // ComboBox de Empresas
        ComboBox<Empresa> cmbEmpresa = new ComboBox<>();
        cmbEmpresa.setItems(FXCollections.observableArrayList(
            empresaService.findActivas()));
        cmbEmpresa.setPromptText("Seleccionar empresa *");
        cmbEmpresa.setPrefWidth(300);

        // ComboBox de Tutores (se actualiza al seleccionar empresa)
        ComboBox<TutorEmpresa> cmbTutor = new ComboBox<>();
        cmbTutor.setPromptText("Seleccionar tutor *");
        cmbTutor.setPrefWidth(300);

        // ComboBox de Periodos
        ComboBox<Periodo> cmbPeriodo = new ComboBox<>();
        cmbPeriodo.setItems(FXCollections.observableArrayList(
            periodoService.findActivos()));
        cmbPeriodo.setPromptText("Seleccionar periodo *");
        cmbPeriodo.setPrefWidth(300);

        // Spinner de horas realizadas (solo para edición)
        Spinner<Integer> spnHoras = new Spinner<>(0, 500, 0);
        spnHoras.setEditable(true);
        spnHoras.setDisable(esNueva);

        // TextArea de observaciones
        TextArea txtObservaciones = new TextArea();
        txtObservaciones.setPromptText("Observaciones adicionales...");
        txtObservaciones.setPrefRowCount(3);
        txtObservaciones.setPrefWidth(300);

        // Actualizar tutores cuando cambia la empresa
        cmbEmpresa.setOnAction(e -> {
            Empresa empresaSeleccionada = cmbEmpresa.getValue();
            if (empresaSeleccionada != null) {
                List<TutorEmpresa> tutores = tutorEmpresaService.findByEmpresa(empresaSeleccionada);
                cmbTutor.setItems(FXCollections.observableArrayList(tutores));
            } else {
                cmbTutor.getItems().clear();
            }
        });

        // Si es edición, cargar datos
        if (!esNueva) {
            cmbEstudiante.setValue(fct.getEstudiante());
            cmbEstudiante.setDisable(true); // No permitir cambiar estudiante
            cmbEmpresa.setValue(fct.getEmpresa());
            // Cargar tutores de la empresa
            List<TutorEmpresa> tutores = tutorEmpresaService.findByEmpresa(fct.getEmpresa());
            cmbTutor.setItems(FXCollections.observableArrayList(tutores));
            cmbTutor.setValue(fct.getTutorEmpresa());
            cmbPeriodo.setValue(fct.getPeriodo());
            cmbPeriodo.setDisable(true); // No permitir cambiar periodo
            spnHoras.getValueFactory().setValue(
                fct.getHorasRealizadas() != null ? fct.getHorasRealizadas() : 0);
            txtObservaciones.setText(fct.getObservaciones());
        }

        grid.add(new Label("Estudiante: *"), 0, 0);
        grid.add(cmbEstudiante, 1, 0);
        grid.add(new Label("Empresa: *"), 0, 1);
        grid.add(cmbEmpresa, 1, 1);
        grid.add(new Label("Tutor empresa: *"), 0, 2);
        grid.add(cmbTutor, 1, 2);
        grid.add(new Label("Periodo: *"), 0, 3);
        grid.add(cmbPeriodo, 1, 3);
        if (!esNueva) {
            grid.add(new Label("Horas realizadas:"), 0, 4);
            grid.add(spnHoras, 1, 4);
        }
        grid.add(new Label("Observaciones:"), 0, esNueva ? 4 : 5);
        grid.add(txtObservaciones, 1, esNueva ? 4 : 5);

        dialog.getDialogPane().setContent(grid);

        // Convertir resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                try {
                    // Validaciones
                    if (cmbEstudiante.getValue() == null) {
                        throw new ValidacionException("estudiante", "es obligatorio");
                    }
                    if (cmbEmpresa.getValue() == null) {
                        throw new ValidacionException("empresa", "es obligatoria");
                    }
                    if (cmbTutor.getValue() == null) {
                        throw new ValidacionException("tutor", "es obligatorio");
                    }
                    if (cmbPeriodo.getValue() == null) {
                        throw new ValidacionException("periodo", "es obligatorio");
                    }

                    // Verificar duplicados (solo para nueva asignación)
                    if (esNueva) {
                        if (fctService.existeFctParaEstudianteEnPeriodo(
                                cmbEstudiante.getValue(), cmbPeriodo.getValue())) {
                            throw new OperacionNoPermitidaException(
                                "El estudiante ya tiene una FCT asignada en este periodo");
                        }
                    }

                    // Crear o actualizar FCT
                    FCT resultado;
                    if (esNueva) {
                        resultado = new FCT(
                            cmbEstudiante.getValue(),
                            cmbEmpresa.getValue(),
                            cmbTutor.getValue(),
                            cmbPeriodo.getValue()
                        );
                    } else {
                        resultado = fct;
                        resultado.setEmpresa(cmbEmpresa.getValue());
                        resultado.setTutorEmpresa(cmbTutor.getValue());
                        resultado.setHorasRealizadas(spnHoras.getValue());
                    }
                    resultado.setObservaciones(txtObservaciones.getText().trim());

                    return resultado;
                } catch (ValidacionException | OperacionNoPermitidaException e) {
                    mostrarError("Error de validación", e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<FCT> resultado = dialog.showAndWait();
        resultado.ifPresent(f -> {
            try {
                fctService.save(f);
                cargarDatos();
                mostrarExito(esNueva ? "Asignación creada" : "Asignación actualizada",
                    "La asignación FCT se ha guardado correctamente.");
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

    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
