package com.adrian.gestionfct.config;

import com.adrian.gestionfct.modelo.*;
import com.adrian.gestionfct.repositorios.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DataInitializer - Carga datos iniciales automáticamente cuando arranca la aplicación.
 * 
 * CÓMO FUNCIONA:
 * - Implementa CommandLineRunner, que Spring ejecuta al iniciar
 * - Verifica si ya hay usuarios en la BD (para no duplicar datos)
 * - Si no hay datos, crea usuarios, cursos, empresas, tutores, periodos y estudiantes
 * 
 * IMPORTANTE: Este archivo se ejecuta CADA VEZ que arranca la aplicación,
 * pero solo inserta datos si la BD está vacía.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    // Inyección de dependencias de todos los repositorios
    private final UsuarioRepository usuarioRepository;
    private final CursoAcademicoRepository cursoAcademicoRepository;
    private final EmpresaRepository empresaRepository;
    private final TutorEmpresaRepository tutorEmpresaRepository;
    private final PeriodoRepository periodoRepository;
    private final EstudianteRepository estudianteRepository;

    // Constructor con inyección de dependencias
    public DataInitializer(
            UsuarioRepository usuarioRepository,
            CursoAcademicoRepository cursoAcademicoRepository,
            EmpresaRepository empresaRepository,
            TutorEmpresaRepository tutorEmpresaRepository,
            PeriodoRepository periodoRepository,
            EstudianteRepository estudianteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.cursoAcademicoRepository = cursoAcademicoRepository;
        this.empresaRepository = empresaRepository;
        this.tutorEmpresaRepository = tutorEmpresaRepository;
        this.periodoRepository = periodoRepository;
        this.estudianteRepository = estudianteRepository;
    }

    /**
     * Método que se ejecuta al arrancar la aplicación.
     * @Transactional asegura que todas las operaciones se hacen en una transacción.
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Solo carga datos si no hay usuarios (BD vacía)
        if (usuarioRepository.count() == 0) {
            System.out.println("========================================");
            System.out.println("BASE DE DATOS VACÍA - CARGANDO DATOS INICIALES");
            System.out.println("========================================");
            
            cargarDatosIniciales();
            
            System.out.println("========================================");
            System.out.println("DATOS CARGADOS CORRECTAMENTE");
            System.out.println("========================================");
            System.out.println("Credenciales de acceso:");
            System.out.println("  - Admin: admin@gestionfct.com / admin123");
            System.out.println("  - Docente: docente@gestionfct.com / docente123");
            System.out.println("  - Profesor: docente2@gestionfct.com / docente123");
            System.out.println("========================================");
        } else {
            System.out.println("Base de datos ya contiene datos. No se cargan datos iniciales.");
        }
    }

    /**
     * Carga todos los datos iniciales en orden correcto.
     * El orden es IMPORTANTE por las relaciones entre tablas:
     * 1. Usuarios (no depende de nadie)
     * 2. Cursos académicos (no depende de nadie)
     * 3. Empresas (no depende de nadie)
     * 4. Tutores de empresa (depende de Empresas y opcionalmente Usuarios)
     * 5. Periodos (depende de Cursos académicos)
     * 6. Estudiantes (depende opcionalmente de Usuarios)
     */
    private void cargarDatosIniciales() {
        // 1. CREAR USUARIOS
        System.out.println("Creando usuarios...");
        Usuario admin = crearUsuario("Admin", "Sistema", "admin@gestionfct.com", "admin123", Rol.ADMINISTRADOR);
        Usuario docente1 = crearUsuario("Luis", "García Pérez", "docente@gestionfct.com", "docente123", Rol.DOCENTE);
        Usuario docente2 = crearUsuario("María", "López Fernández", "docente2@gestionfct.com", "docente123", Rol.PROFESOR);
        
        // 2. CREAR CURSOS ACADÉMICOS
        System.out.println("Creando cursos académicos...");
        CursoAcademico cursoActual = crearCursoAcademico("2025-2026", "Curso académico actual", true);
        CursoAcademico cursoAnterior = crearCursoAcademico("2024-2025", "Curso académico anterior", false);
        
        // 3. CREAR EMPRESAS
        System.out.println("Creando empresas...");
        Empresa empresa1 = crearEmpresa("TechSolutions S.L.", "B12345678", "Calle Tecnología 123", 
                "Gijón", "33201", "Asturias", "985123456", "info@techsolutions.com", 
                "Carlos Martínez", "Desarrollo de Software");
        Empresa empresa2 = crearEmpresa("DataCenter Asturias", "B87654321", "Polígono Industrial Norte", 
                "Avilés", "33400", "Asturias", "985654321", "contacto@datacenter.com", 
                "Ana García", "Servicios TI");
        Empresa empresa3 = crearEmpresa("WebDev Pro", "A11223344", "Av. de la Constitución 45", 
                "Oviedo", "33001", "Asturias", "985111222", "hola@webdevpro.es", 
                "Pedro López", "Desarrollo Web");
        
        // 4. CREAR TUTORES DE EMPRESA
        System.out.println("Creando tutores de empresa...");
        TutorEmpresa tutor1 = crearTutorEmpresa("Carlos", "Martínez Ruiz", "12345678A", 
                "666111222", "carlos@techsolutions.com", "Director Técnico", empresa1);
        TutorEmpresa tutor2 = crearTutorEmpresa("Ana", "García Sánchez", "87654321B", 
                "666333444", "ana@datacenter.com", "Jefa de Proyectos", empresa2);
        TutorEmpresa tutor3 = crearTutorEmpresa("Pedro", "López Álvarez", "11223344C", 
                "666555666", "pedro@webdevpro.es", "CTO", empresa3);
        
        // 5. CREAR PERIODOS
        System.out.println("Creando periodos...");
        crearPeriodo("FCT Ordinaria 2º DAM", 2, TipoPeriodo.ORDINARIO, 
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 6, 15), 400, cursoActual);
        crearPeriodo("FCT Extraordinaria 2º DAM", 2, TipoPeriodo.EXTRAORDINARIO, 
                LocalDate.of(2026, 6, 16), LocalDate.of(2026, 9, 15), 400, cursoActual);
        crearPeriodo("FCT Ordinaria 1º DAM", 1, TipoPeriodo.ORDINARIO, 
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 30), 200, cursoActual);
        
        // 6. CREAR ESTUDIANTES
        System.out.println("Creando estudiantes...");
        crearEstudiante("Juan", "Pérez González", "11111111A", "666001001", 
                "juan@alumno.es", "DAM", "2A", 2, docente1);
        crearEstudiante("María", "Rodríguez López", "22222222B", "666002002", 
                "maria@alumno.es", "DAM", "2A", 2, docente1);
        crearEstudiante("Pedro", "Fernández García", "33333333C", "666003003", 
                "pedro@alumno.es", "DAW", "2B", 2, docente2);
        crearEstudiante("Laura", "Martínez Sánchez", "44444444D", "666004004", 
                "laura@alumno.es", "DAM", "2A", 2, docente1);
        crearEstudiante("Carlos", "Gómez Ruiz", "55555555E", "666005005", 
                "carlos@alumno.es", "ASIR", "2A", 2, docente2);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private Usuario crearUsuario(String nombre, String apellidos, String email, String password, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setEmail(email);
        usuario.setPassword(password);  // En producción debería estar encriptada
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    private CursoAcademico crearCursoAcademico(String nombre, String descripcion, boolean activo) {
        CursoAcademico curso = new CursoAcademico();
        curso.setNombre(nombre);
        curso.setDescripcion(descripcion);
        curso.setActivo(activo);
        return cursoAcademicoRepository.save(curso);
    }

    private Empresa crearEmpresa(String nombre, String nif, String direccion, String localidad,
            String codigoPostal, String provincia, String telefono, String email,
            String personaContacto, String sector) {
        Empresa empresa = new Empresa();
        empresa.setNombre(nombre);
        empresa.setNif(nif);
        empresa.setDireccion(direccion);
        empresa.setLocalidad(localidad);
        empresa.setCodigoPostal(codigoPostal);
        empresa.setProvincia(provincia);
        empresa.setTelefono(telefono);
        empresa.setEmail(email);
        empresa.setPersonaContacto(personaContacto);
        empresa.setSector(sector);
        empresa.setActiva(true);
        return empresaRepository.save(empresa);
    }

    private TutorEmpresa crearTutorEmpresa(String nombre, String apellidos, String dni,
            String telefono, String email, String cargo, Empresa empresa) {
        TutorEmpresa tutor = new TutorEmpresa();
        tutor.setNombre(nombre);
        tutor.setApellidos(apellidos);
        tutor.setDni(dni);
        tutor.setTelefono(telefono);
        tutor.setEmail(email);
        tutor.setCargo(cargo);
        tutor.setEmpresa(empresa);
        tutor.setActivo(true);
        return tutorEmpresaRepository.save(tutor);
    }

    private Periodo crearPeriodo(String nombre, int curso, TipoPeriodo tipo,
            LocalDate fechaInicio, LocalDate fechaFin, int horasTotales, CursoAcademico cursoAcademico) {
        Periodo periodo = new Periodo();
        periodo.setNombre(nombre);
        periodo.setCurso(curso);
        periodo.setTipo(tipo);
        periodo.setFechaInicio(fechaInicio);
        periodo.setFechaFin(fechaFin);
        periodo.setHorasTotales(horasTotales);
        periodo.setCursoAcademico(cursoAcademico);
        periodo.setActivo(true);
        return periodoRepository.save(periodo);
    }

    private Estudiante crearEstudiante(String nombre, String apellidos, String dni,
            String telefono, String email, String ciclo, String grupo, int cursoActual, Usuario profesorTutor) {
        Estudiante estudiante = new Estudiante();
        estudiante.setNombre(nombre);
        estudiante.setApellidos(apellidos);
        estudiante.setDni(dni);
        estudiante.setTelefono(telefono);
        estudiante.setEmail(email);
        estudiante.setCiclo(ciclo);
        estudiante.setGrupo(grupo);
        estudiante.setCursoActual(cursoActual);
        estudiante.setProfesorTutor(profesorTutor);
        estudiante.setActivo(true);
        return estudianteRepository.save(estudiante);
    }
}
