-- ============================================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS PARA GESTIÓN FCT
-- Proyecto Intermodular 2º DAM - Curso 2025-2026
-- Autor: Adrián Iglesias Rino
-- Versión: 3.0 CORREGIDA
-- ============================================================
-- IMPORTANTE: Este script coincide EXACTAMENTE con las entidades JPA
-- ============================================================

-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS gestionfct 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE gestionfct;

-- ============================================================
-- TABLA: usuarios
-- Columnas según Usuario.java:
-- id, nombre, apellidos, email, password, telefono, rol, activo, fecha_creacion, ultimo_acceso
-- Enum Rol: ADMINISTRADOR, PROFESOR, TUTOR_EMPRESA, ESTUDIANTE, DOCENTE
-- ============================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    rol ENUM('ADMINISTRADOR', 'PROFESOR', 'TUTOR_EMPRESA', 'ESTUDIANTE', 'DOCENTE') NOT NULL,
    activo BIT(1) NOT NULL DEFAULT 1,
    fecha_creacion DATETIME,
    ultimo_acceso DATETIME
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: cursos_academicos
-- Columnas según CursoAcademico.java:
-- id, nombre, descripcion, activo
-- ============================================================
CREATE TABLE IF NOT EXISTS cursos_academicos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(9) NOT NULL UNIQUE,
    descripcion VARCHAR(500),
    activo BIT(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: empresas
-- Columnas según Empresa.java:
-- id, nombre, nif, direccion, localidad, codigo_postal, provincia,
-- telefono, email, persona_contacto, sector, activa, observaciones
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
    activa BIT(1) NOT NULL DEFAULT 1,
    observaciones VARCHAR(1000)
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: tutores_empresa
-- Columnas según TutorEmpresa.java:
-- id, nombre, apellidos, dni, telefono, email, cargo, activo, usuario_id, empresa_id
-- ============================================================
CREATE TABLE IF NOT EXISTS tutores_empresa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(15),
    telefono VARCHAR(20),
    email VARCHAR(100),
    cargo VARCHAR(100),
    activo BIT(1) NOT NULL DEFAULT 1,
    usuario_id BIGINT UNIQUE,
    empresa_id BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    FOREIGN KEY (empresa_id) REFERENCES empresas(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: periodos
-- Columnas según Periodo.java:
-- id, nombre, curso, tipo, fecha_inicio, fecha_fin, horas_totales, activo, curso_academico_id
-- Enum TipoPeriodo: ORDINARIO, EXTRAORDINARIO
-- ============================================================
CREATE TABLE IF NOT EXISTS periodos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    curso INT NOT NULL,
    tipo ENUM('ORDINARIO', 'EXTRAORDINARIO') NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    horas_totales INT,
    activo BIT(1) NOT NULL DEFAULT 1,
    curso_academico_id BIGINT NOT NULL,
    FOREIGN KEY (curso_academico_id) REFERENCES cursos_academicos(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: estudiantes
-- Columnas según Estudiante.java:
-- id, nombre, apellidos, dni, fecha_nacimiento, telefono, email, direccion,
-- ciclo, grupo, curso_actual, activo, usuario_id, profesor_tutor_id
-- ============================================================
CREATE TABLE IF NOT EXISTS estudiantes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(15),
    fecha_nacimiento DATE,
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion VARCHAR(300),
    ciclo VARCHAR(100),
    grupo VARCHAR(50),
    curso_actual INT,
    activo BIT(1) NOT NULL DEFAULT 1,
    usuario_id BIGINT UNIQUE,
    profesor_tutor_id BIGINT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    FOREIGN KEY (profesor_tutor_id) REFERENCES usuarios(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: fcts
-- Columnas según FCT.java:
-- id, estado, fecha_inicio, fecha_fin, horas_realizadas, horas_totales,
-- observaciones, fecha_creacion, fecha_modificacion,
-- estudiante_id, empresa_id, tutor_empresa_id, periodo_id, curso_academico_id
-- Enum EstadoFCT: ACTIVA, FINALIZADA, CANCELADA
-- ============================================================
CREATE TABLE IF NOT EXISTS fcts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estado ENUM('ACTIVA', 'FINALIZADA', 'CANCELADA') NOT NULL DEFAULT 'ACTIVA',
    fecha_inicio DATE,
    fecha_fin DATE,
    horas_realizadas INT DEFAULT 0,
    horas_totales INT,
    observaciones VARCHAR(1000),
    fecha_creacion DATETIME,
    fecha_modificacion DATETIME,
    estudiante_id BIGINT NOT NULL,
    empresa_id BIGINT NOT NULL,
    tutor_empresa_id BIGINT NOT NULL,
    periodo_id BIGINT NOT NULL,
    curso_academico_id BIGINT NOT NULL,
    CONSTRAINT uk_estudiante_periodo UNIQUE (estudiante_id, periodo_id),
    FOREIGN KEY (estudiante_id) REFERENCES estudiantes(id) ON DELETE RESTRICT,
    FOREIGN KEY (empresa_id) REFERENCES empresas(id) ON DELETE RESTRICT,
    FOREIGN KEY (tutor_empresa_id) REFERENCES tutores_empresa(id) ON DELETE RESTRICT,
    FOREIGN KEY (periodo_id) REFERENCES periodos(id) ON DELETE RESTRICT,
    FOREIGN KEY (curso_academico_id) REFERENCES cursos_academicos(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: documentos
-- Columnas según Documento.java:
-- id, nombre, nombre_almacenado, ruta, tipo, content_type, tamano,
-- descripcion, fecha_subida, fct_id, autor_id
-- Enum TipoDocumento: CONVENIO, INFORME_SEGUIMIENTO, EVALUACION, INFORME_FINAL, OTRO
-- ============================================================
CREATE TABLE IF NOT EXISTS documentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    nombre_almacenado VARCHAR(255) NOT NULL,
    ruta VARCHAR(500) NOT NULL,
    tipo ENUM('CONVENIO', 'INFORME_SEGUIMIENTO', 'EVALUACION', 'INFORME_FINAL', 'OTRO') NOT NULL,
    content_type VARCHAR(100),
    tamano BIGINT,
    descripcion VARCHAR(500),
    fecha_subida DATETIME NOT NULL,
    fct_id BIGINT NOT NULL,
    autor_id BIGINT NOT NULL,
    FOREIGN KEY (fct_id) REFERENCES fcts(id) ON DELETE CASCADE,
    FOREIGN KEY (autor_id) REFERENCES usuarios(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- DATOS INICIALES
-- ============================================================

-- 1. USUARIOS (primero porque otras tablas dependen de él)
INSERT INTO usuarios (nombre, apellidos, email, password, rol, activo, fecha_creacion) VALUES
('Admin', 'Sistema', 'admin@gestionfct.com', 'admin123', 'ADMINISTRADOR', 1, NOW()),
('Luis', 'García Pérez', 'docente@gestionfct.com', 'docente123', 'DOCENTE', 1, NOW()),
('María', 'López Fernández', 'docente2@gestionfct.com', 'docente123', 'PROFESOR', 1, NOW());

-- 2. CURSOS ACADÉMICOS
INSERT INTO cursos_academicos (nombre, descripcion, activo) VALUES
('2025-2026', 'Curso académico actual', 1),
('2024-2025', 'Curso académico anterior', 0);

-- 3. EMPRESAS
INSERT INTO empresas (nombre, nif, direccion, localidad, codigo_postal, provincia, telefono, email, persona_contacto, sector, activa) VALUES
('TechSolutions S.L.', 'B12345678', 'Calle Tecnología 123', 'Gijón', '33201', 'Asturias', '985123456', 'info@techsolutions.com', 'Carlos Martínez', 'Desarrollo de Software', 1),
('DataCenter Asturias', 'B87654321', 'Polígono Industrial Norte', 'Avilés', '33400', 'Asturias', '985654321', 'contacto@datacenter.com', 'Ana García', 'Servicios TI', 1),
('WebDev Pro', 'A11223344', 'Av. de la Constitución 45', 'Oviedo', '33001', 'Asturias', '985111222', 'hola@webdevpro.es', 'Pedro López', 'Desarrollo Web', 1);

-- 4. TUTORES DE EMPRESA (dependen de empresas)
INSERT INTO tutores_empresa (nombre, apellidos, dni, telefono, email, cargo, activo, empresa_id) VALUES
('Carlos', 'Martínez Ruiz', '12345678A', '666111222', 'carlos@techsolutions.com', 'Director Técnico', 1, 1),
('Ana', 'García Sánchez', '87654321B', '666333444', 'ana@datacenter.com', 'Jefa de Proyectos', 1, 2),
('Pedro', 'López Álvarez', '11223344C', '666555666', 'pedro@webdevpro.es', 'CTO', 1, 3);

-- 5. PERIODOS (dependen de cursos_academicos)
INSERT INTO periodos (nombre, curso, tipo, fecha_inicio, fecha_fin, horas_totales, activo, curso_academico_id) VALUES
('FCT Ordinaria 2º DAM', 2, 'ORDINARIO', '2026-03-01', '2026-06-15', 400, 1, 1),
('FCT Extraordinaria 2º DAM', 2, 'EXTRAORDINARIO', '2026-06-16', '2026-09-15', 400, 1, 1),
('FCT Ordinaria 1º DAM', 1, 'ORDINARIO', '2026-05-01', '2026-06-30', 200, 1, 1);

-- 6. ESTUDIANTES (pueden tener profesor_tutor_id = usuario docente)
INSERT INTO estudiantes (nombre, apellidos, dni, telefono, email, ciclo, grupo, curso_actual, activo, profesor_tutor_id) VALUES
('Juan', 'Pérez González', '11111111A', '666001001', 'juan@alumno.es', 'DAM', '2A', 2, 1, 2),
('María', 'Rodríguez López', '22222222B', '666002002', 'maria@alumno.es', 'DAM', '2A', 2, 1, 2),
('Pedro', 'Fernández García', '33333333C', '666003003', 'pedro@alumno.es', 'DAW', '2B', 2, 1, 3),
('Laura', 'Martínez Sánchez', '44444444D', '666004004', 'laura@alumno.es', 'DAM', '2A', 2, 1, 2),
('Carlos', 'Gómez Ruiz', '55555555E', '666005005', 'carlos@alumno.es', 'ASIR', '2A', 2, 1, 3);

-- ============================================================
-- FIN DEL SCRIPT
-- ============================================================
-- Para ejecutar:
-- 1. Abrir phpMyAdmin o MySQL Workbench
-- 2. Copiar y pegar este script completo
-- 3. Ejecutar
-- 
-- Credenciales de acceso:
-- - Admin: admin@gestionfct.com / admin123
-- - Docente: docente@gestionfct.com / docente123
-- - Profesor: docente2@gestionfct.com / docente123
-- ============================================================
