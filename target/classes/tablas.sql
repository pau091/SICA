-- =========================================================
-- SCRIPT CONSOLIDADO: ESQUEMA + DATOS DE PRUEBA
-- Proyecto: SICA
-- =========================================================

CREATE DATABASE IF NOT EXISTS sica;
USE sica;

-- =========================================================
-- 0. LIMPIEZA DE TABLAS (ORDEN POR INVERSO DE DEPENDENCIAS)
-- =========================================================
DROP TABLE IF EXISTS bitacora_auditoria;
DROP TABLE IF EXISTS incidentes;
DROP TABLE IF EXISTS visitas;
DROP TABLE IF EXISTS personas;
DROP TABLE IF EXISTS empresas;
DROP TABLE IF EXISTS visita_estados;
DROP TABLE IF EXISTS persona_estados_acceso;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS rol_permisos;
DROP TABLE IF EXISTS permisos;
DROP TABLE IF EXISTS roles;

-- =========================================================
-- 1. TABLAS DE AUTENTICACIÓN Y AUTORIZACIÓN (RBAC)
-- =========================================================
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE permisos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_permiso VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT
);

CREATE TABLE rol_permisos (
    rol_id INT,
    permiso_id INT,
    PRIMARY KEY (rol_id, permiso_id),
    FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permiso_id) REFERENCES permisos(id) ON DELETE CASCADE
);

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol_id INT,
    esta_activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (rol_id) REFERENCES roles(id)
);

-- =========================================================
-- 2. TABLAS DE CONSULTA (LOOKUP TABLES)
-- =========================================================
CREATE TABLE persona_estados_acceso (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_estado VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE visita_estados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_estado VARCHAR(50) UNIQUE NOT NULL
);

-- =========================================================
-- 3. TABLAS OPERACIONALES DEL NEGOCIO
-- =========================================================
CREATE TABLE empresas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    contacto_principal VARCHAR(100)
);

CREATE TABLE personas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    documento_identidad VARCHAR(20) UNIQUE NOT NULL,
    empresa_id INT,
    tipo_persona ENUM('Trabajador', 'Invitado') NOT NULL,
    estado_acceso_id INT,
    url_foto VARCHAR(255),
    FOREIGN KEY (empresa_id) REFERENCES empresas(id),
    FOREIGN KEY (estado_acceso_id) REFERENCES persona_estados_acceso(id)
);

CREATE TABLE visitas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    persona_id INT,
    fecha_entrada DATETIME,
    fecha_salida DATETIME,
    estado_visita_id INT,
    vehiculo_placa VARCHAR(10),
    visita_aprobada_por INT,
    FOREIGN KEY (persona_id) REFERENCES personas(id),
    FOREIGN KEY (estado_visita_id) REFERENCES visita_estados(id),
    FOREIGN KEY (visita_aprobada_por) REFERENCES usuarios(id)
);

CREATE TABLE incidentes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    visita_id INT,
    reportado_por_id INT,
    fecha DATETIME NOT NULL,
    descripcion TEXT NOT NULL,
    FOREIGN KEY (visita_id) REFERENCES visitas(id),
    FOREIGN KEY (reportado_por_id) REFERENCES usuarios(id)
);

-- =========================================================
-- 4. TABLA DE AUDITORÍA
-- =========================================================
CREATE TABLE bitacora_auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    accion_realizada VARCHAR(255) NOT NULL,
    tabla_afectada VARCHAR(100),
    registro_id_afectado INT,
    detalles TEXT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);