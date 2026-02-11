-- ============================================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS PARA GESTIÓN FCT
-- Proyecto Intermodular 2º DAM - Curso 2025-2026
-- Autor: Adrián Iglesias Rino
-- Versión: 3.0
-- ============================================================

-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS gestionfct 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE gestionfct;

-- ============================================================
-- TABLA: usuarios
-- Gestiona los usuarios del sistema con sus roles
-- ============================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    rol ENUM('ADMINISTRADOR', 'DOCENTE', 'TUTOR_EMPRESA', 'ESTUDIANTE', 'PROFESOR') NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    ultimo_acceso DATETIME,
    INDEX idx_usuarios_email (email),
    INDEX idx_usuarios_rol (rol)
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: cursos_academicos
-- Define los años académicos del centro
-- ============================================================
CREATE TABLE IF NOT EXISTS cursos_academicos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    anio_inicio INT NOT NULL,
    anio_fin INT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_inicio DATE,
    fecha_fin DATE,
    INDEX idx_curso_activo (activo)
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: empresas
-- Empresas colaboradoras para las FCT
-- ============================================================
CREATE TABLE IF NOT EXISTS empresas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    nif VARCHAR(15) NOT NULL UNIQUE,
    direccion VARCHAR(300),
    localidad VARCHAR(100),
    codigo_postal VARCHAR(10),
    provincia VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(100) UNIQUE,
    persona_contacto VARCHAR(200),
    sector VARCHAR(500),
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    observaciones VARCHAR(1000),
    INDEX idx_empresas_nif (nif),
    INDEX idx_empresas_activa (activa)
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: tutores_empresa
-- Tutores de empresa que supervisan las FCT
-- ============================================================
CREATE TABLE IF NOT EXISTS tutores_empresa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(15) UNIQUE,
    telefono VARCHAR(20),
    email VARCHAR(100),
    cargo VARCHAR(100),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    empresa_id BIGINT NOT NULL,
    usuario_id BIGINT UNIQUE,
    FOREIGN KEY (empresa_id) REFERENCES empresas(id) ON DELETE RESTRICT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_tutores_empresa (empresa_id)
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: periodos
-- Periodos de prácticas FCT
-- ============================================================
CREATE TABLE IF NOT EXISTS periodos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    curso INT NOT NULL COMMENT '1 o 2 para 1º o 2º curso',
    tipo ENUM('PRIMER_TRIMESTRE', 'SEGUNDO_TRIMESTRE', 'TERCER_TRIMESTRE', 'ORDINARIO', 'EXTRAORDINARIO') NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    horas_totales INT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    curso_academico_id BIGINT NOT NULL,
    FOREIGN KEY (curso_academico_id) REFERENCES cursos_academicos(id) ON DELETE RESTRICT,
    INDEX idx_periodos_curso (curso_academico_id)
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: estudiantes
-- Estudiantes que realizan las FCT
-- ============================================================
CREATE TABLE IF NOT EXISTS estudiantes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(15) UNIQUE,
    fecha_nacimiento DATE,
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion VARCHAR(300),
    ciclo VARCHAR(100),
    grupo VARCHAR(50),
    curso_actual INT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    usuario_id BIGINT UNIQUE,
    profesor_tutor_id BIGINT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    FOREIGN KEY (profesor_tutor_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_estudiantes_ciclo (ciclo),
    INDEX idx_estudiantes_grupo (grupo)
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: fcts
-- Formación en Centro de Trabajo - Entidad central del sistema
-- ============================================================
CREATE TABLE IF NOT EXISTS fcts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estado ENUM('ACTIVA', 'FINALIZADA', 'CANCELADA') NOT NULL DEFAULT 'ACTIVA',
    fecha_inicio DATE,
    fecha_fin DATE,
    horas_realizadas INT DEFAULT 0,
    horas_totales INT,
    observaciones VARCHAR(1000),
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    estudiante_id BIGINT NOT NULL,
    empresa_id BIGINT NOT NULL,
    tutor_empresa_id BIGINT NOT NULL,
    periodo_id BIGINT NOT NULL,
    curso_academico_id BIGINT NOT NULL,
    FOREIGN KEY (estudiante_id) REFERENCES estudiantes(id) ON DELETE RESTRICT,
    FOREIGN KEY (empresa_id) REFERENCES empresas(id) ON DELETE RESTRICT,
    FOREIGN KEY (tutor_empresa_id) REFERENCES tutores_empresa(id) ON DELETE RESTRICT,
    FOREIGN KEY (periodo_id) REFERENCES periodos(id) ON DELETE RESTRICT,
    FOREIGN KEY (curso_academico_id) REFERENCES cursos_academicos(id) ON DELETE RESTRICT,
    UNIQUE KEY uk_estudiante_periodo (estudiante_id, periodo_id),
    INDEX idx_fcts_estado (estado),
    INDEX idx_fcts_estudiante (estudiante_id),
    INDEX idx_fcts_empresa (empresa_id)
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: documentos
-- Documentos asociados a las FCT
-- ============================================================
CREATE TABLE IF NOT EXISTS documentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    nombre_almacenado VARCHAR(255) NOT NULL,
    ruta VARCHAR(500) NOT NULL,
    tipo ENUM('ANEXO', 'INFORME', 'EVALUACION', 'MEMORIA', 'OTRO') NOT NULL,
    content_type VARCHAR(100),
    tamano BIGINT,
    descripcion VARCHAR(500),
    fecha_subida DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fct_id BIGINT NOT NULL,
    autor_id BIGINT NOT NULL,
    FOREIGN KEY (fct_id) REFERENCES fcts(id) ON DELETE CASCADE,
    FOREIGN KEY (autor_id) REFERENCES usuarios(id) ON DELETE RESTRICT,
    INDEX idx_documentos_fct (fct_id),
    INDEX idx_documentos_tipo (tipo)
) ENGINE=InnoDB;

-- ============================================================
-- DATOS INICIALES: Usuario administrador
-- ============================================================
INSERT INTO usuarios (nombre, apellidos, email, password, rol, activo, fecha_creacion) VALUES
('Administrador', 'Sistema', 'admin@gestionfct.com', 'admin123', 'ADMINISTRADOR', TRUE, NOW()),
('Luis', 'García Pérez', 'docente@gestionfct.com', 'docente123', 'DOCENTE', TRUE, NOW()),
('María', 'López Fernández', 'docente2@gestionfct.com', 'docente123', 'DOCENTE', TRUE, NOW());

-- ============================================================
-- DATOS INICIALES: Curso académico activo
-- ============================================================
INSERT INTO cursos_academicos (nombre, anio_inicio, anio_fin, activo, fecha_inicio, fecha_fin) VALUES
('2025-2026', 2025, 2026, TRUE, '2025-09-15', '2026-06-30'),
('2024-2025', 2024, 2025, FALSE, '2024-09-15', '2025-06-30');

-- ============================================================
-- DATOS INICIALES: Periodos de FCT para el curso activo
-- ============================================================
INSERT INTO periodos (nombre, curso, tipo, fecha_inicio, fecha_fin, horas_totales, activo, curso_academico_id) VALUES
('FCT Ordinaria 2º DAM 2025-26', 2, 'ORDINARIO', '2026-03-01', '2026-06-15', 400, TRUE, 1),
('FCT Extraordinaria 2º DAM 2025-26', 2, 'EXTRAORDINARIO', '2026-06-16', '2026-09-15', 400, TRUE, 1),
('FCT Ordinaria 1º DAM 2025-26', 1, 'ORDINARIO', '2026-05-01', '2026-06-30', 200, TRUE, 1);

-- ============================================================
-- DATOS INICIALES: Empresas de ejemplo
-- ============================================================
INSERT INTO empresas (nombre, nif, direccion, localidad, codigo_postal, provincia, telefono, email, persona_contacto, sector, activa) VALUES
('TechSolutions S.L.', 'B12345678', 'Calle Tecnología 123', 'Gijón', '33201', 'Asturias', '985123456', 'info@techsolutions.com', 'Carlos Martínez', 'Desarrollo de Software', TRUE),
('DataCenter Asturias', 'B87654321', 'Polígono Industrial Norte', 'Avilés', '33400', 'Asturias', '985654321', 'contacto@datacenter.com', 'Ana García', 'Servicios TI', TRUE),
('WebDev Pro', 'A11223344', 'Av. de la Constitución 45', 'Oviedo', '33001', 'Asturias', '985111222', 'hola@webdevpro.es', 'Pedro López', 'Desarrollo Web', TRUE);

-- ============================================================
-- DATOS INICIALES: Tutores de empresa
-- ============================================================
INSERT INTO tutores_empresa (nombre, apellidos, dni, telefono, email, cargo, activo, empresa_id) VALUES
('Carlos', 'Martínez Ruiz', '12345678A', '666111222', 'carlos@techsolutions.com', 'Director Técnico', TRUE, 1),
('Ana', 'García Sánchez', '87654321B', '666333444', 'ana@datacenter.com', 'Jefa de Proyectos', TRUE, 2),
('Pedro', 'López Álvarez', '11223344C', '666555666', 'pedro@webdevpro.es', 'CTO', TRUE, 3);

-- ============================================================
-- DATOS INICIALES: Estudiantes de ejemplo
-- ============================================================
INSERT INTO estudiantes (nombre, apellidos, dni, telefono, email, ciclo, grupo, curso_actual, activo) VALUES
('Juan', 'Pérez González', '11111111A', '666001001', 'juan@alumno.es', 'DAM', '2A', 2, TRUE),
('María', 'Rodríguez López', '22222222B', '666002002', 'maria@alumno.es', 'DAM', '2A', 2, TRUE),
('Pedro', 'Fernández García', '33333333C', '666003003', 'pedro@alumno.es', 'DAW', '2B', 2, TRUE),
('Laura', 'Martínez Sánchez', '44444444D', '666004004', 'laura@alumno.es', 'DAM', '2A', 2, TRUE),
('Carlos', 'Gómez Ruiz', '55555555E', '666005005', 'carlos@alumno.es', 'ASIR', '2A', 2, TRUE);

-- ============================================================
-- FIN DEL SCRIPT
-- ============================================================
