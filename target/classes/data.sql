-- =========================================================
-- 5. POBLADO DE DATOS
-- =========================================================

-- Roles
INSERT INTO roles (id, nombre_rol) VALUES 
(1, 'Superusuario'),
(2, 'Supervisor de Seguridad'),
(3, 'Guarda de Seguridad'),
(4, 'Funcionario de Empresa');

-- Permisos
INSERT INTO permisos (id, nombre_permiso, descripcion) VALUES
(1, 'crear_usuario', 'Permite crear, actualizar o desactivar usuarios en el sistema'),
(2, 'registrar_visita', 'Permite realizar check-in de visitas y personas'),
(3, 'aprobar_visita', 'Permite autorizar o rechazar ingresos no anunciados'),
(4, 'generar_reporte', 'Permite consultar auditorias e historial de accesos'),
(5, 'bloquear_persona', 'Permite restringir el acceso a una persona'),
(6, 'reportar_incidente', 'Permite registrar un incidente de seguridad'),
(7, 'resetear_datos_prueba', 'Permite borrar visitas, incidentes y bitácora para dejar el sistema como recién instalado'),
(8, 'crear_persona', 'Permite crear, actualizar o eliminar personas (trabajadores o invitados) en el sistema'),
(9, 'crear_empresa', 'Permite crear, actualizar o eliminar empresas del complejo');

-- Permisos asignados a roles
-- Nota: se agregó (4, 4) porque la pantalla del Funcionario ofrece "Ver Reportes" en su menú,
-- pero antes nunca se le había dado el permiso "generar_reporte" que esa acción exige.
-- (1, 7), (1, 8) y (1, 9) son a propósito solo para el Superusuario: crear/borrar datos maestros
-- son acciones delicadas que no le corresponden a los demás roles.
INSERT INTO rol_permisos (rol_id, permiso_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9),
(2, 2), (2, 4), (2, 5), (2, 6),
(3, 2),
(4, 3), (4, 4);

-- Estados de acceso de personas
INSERT INTO persona_estados_acceso (id, nombre_estado) VALUES 
(1, 'Activo'),
(2, 'Con Prohibicion de Ingreso');

-- Estados de visita normativos
INSERT INTO visita_estados (id, nombre_estado) VALUES 
(1, 'Dentro'),
(2, 'Fuera'),
(3, 'Pendiente de Aprobacion'),
(4, 'Aprobado'),
(5, 'Rechazado'),
(6, 'Cerrada por Sistema (Salida Olvidada)');

-- Empresas
INSERT INTO empresas (id, nombre, contacto_principal) VALUES 
(1, 'TechCorp Acme', 'contacto@techcorp.com'),
(2, 'Innovatech', 'admin@innovatech.com');

-- Usuarios del sistema
-- (antes no había ningún usuario con rol_id = 2, así que el rol "Supervisor de Seguridad"
-- no se podía ni probar; se agrega "Sofia Supervisora" para cubrir ese rol.)
INSERT INTO usuarios (id, nombre, email, password, rol_id, esta_activo) VALUES
(1, 'Admin General', 'admin@sica.com', '1234', 1, TRUE),
(2, 'Pedro Guarda', 'guarda@sica.com', '1234', 3, TRUE),
(3, 'Laura Funcional', 'laura@techcorp.com', '1234', 4, TRUE),
(4, 'Sofia Supervisora', 'supervisor@sica.com', '1234', 2, TRUE);

-- Personas registradas
INSERT INTO personas (id, nombre, documento_identidad, empresa_id, tipo_persona, estado_acceso_id, url_foto) VALUES 
(1, 'Juan Perez', '1098765432', 1, 'Trabajador', 1, 'https://fotos.acme.com/juan.jpg'),
(2, 'Maria Gomez', '1234567890', 2, 'Invitado', 1, 'https://fotos.acme.com/maria.jpg'),
(3, 'Laura Gómez', '9876543210', 1, 'Invitado', 1, 'https://fotos.acme.com/laura.jpg');

-- Visita activa de prueba (Laura Gómez en estado 'Dentro' = estado_visita_id 1)
INSERT INTO visitas (persona_id, fecha_entrada, estado_visita_id)
VALUES (3, NOW(), 1);

-- =========================================================
-- 6. CONSULTAS DE VERIFICACIÓN
-- =========================================================
SELECT v.id AS visita_id, p.nombre, p.documento_identidad, v.fecha_entrada, ve.nombre_estado AS estado_visita 
FROM visitas v
JOIN personas p ON v.persona_id = p.id
JOIN visita_estados ve ON v.estado_visita_id = ve.id
WHERE p.documento_identidad = '9876543210';