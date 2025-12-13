package com.adrian.gestionfct.view;

/**
 * Enum que define todas las vistas FXML del sistema.
 * 
 * Cada vista tiene asociado su archivo FXML y su título.
 */
public enum FxmlView {

	LOGIN {
		@Override
		public String getTitle() {
			return "GestionFCT - Iniciar Sesión";
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/Login.fxml";
		}
	},

	MENU_ADMIN {
		@Override
		public String getTitle() {
			return "GestionFCT - Panel de Administración";
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/MenuAdmin.fxml";
		}
	},

	MENU_DOCENTE {
		@Override
		public String getTitle() {
			return "GestionFCT - Panel Docente";
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/MenuDocente.fxml";
		}
	},

	MENU_TUTOR {
		@Override
		public String getTitle() {
			return "GestionFCT - Panel Tutor/a de Empresa";
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/MenuTutor.fxml";
		}
	},

	MENU_ESTUDIANTE {
		@Override
		public String getTitle() {
			return "GestionFCT - Panel Estudiante";
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/MenuEstudiante.fxml";
		}
	},

	GESTION_USUARIOS {
		@Override
		public String getTitle() {
			return "GestionFCT - Gestión de Usuarios";
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/GestionUsuarios.fxml";
		}
	},

	GESTION_EMPRESAS {
		@Override
		public String getTitle() {
			return "GestionFCT - Gestión de Empresas";
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/GestionEmpresas.fxml";
		}
	},

	GESTION_ESTUDIANTES {
		@Override
		public String getTitle() {
			return "GestionFCT - Gestión de Estudiantes";
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/GestionEstudiantes.fxml";
		}
	},

	ASIGNAR_FCT {
		@Override
		public String getTitle() {
			return "GestionFCT - Asignar FCT";
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/AsignarFCT.fxml";
		}
	},

	GESTION_DOCUMENTOS {
		@Override
		public String getTitle() {
			return "GestionFCT - Gestión de Documentos";
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/GestionDocumentos.fxml";
		}
	};

	public abstract String getTitle();

	public abstract String getFxmlFile();
}
