export type Page<T> = {
  content: T[];
  page: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
};
export type Persona = {
  id: number;
  nombres: string;
  apellidoPaterno: string;
  apellidoMaterno: string;
  nombreCompleto: string;
  tipoDocumento: string;
  numeroDocumento: string;
  sexo: string;
  fechaNacimiento: string;
  correo: string;
  telefono: string | null;
  direccion: string | null;
  nacionalidadId: number | null;
};
export type Student = {
  id: number;
  codigoAlumno: string;
  persona: Persona;
  activo: boolean;
  apoderados: {
    id: number;
    apoderadoId: number;
    nombreCompleto: string;
    parentesco: string;
    principal: boolean;
  }[];
};
export type Account = {
  id: number;
  personaId: number;
  username: string;
  correo: string;
  nombreCompleto: string;
  rolId: number;
  rol: string;
  estado: string;
  requiereCambioContrasena: boolean;
  correoVerificado: boolean;
  contrasenaTemporalExpira: string | null;
};
export type Permission = {
  id: number;
  codigo: string;
  nombre: string;
  descripcion: string | null;
  categoria: string | null;
};
export type Role = {
  id: number;
  nombre: string;
  descripcion: string;
  permisos: Permission[];
};
export type PersonChoice = {
  id: number;
  nombreCompleto: string;
  numeroDocumento: string;
  perfiles: string[];
  rolesConAcceso: string[];
};
export type Audit = {
  id: number;
  actorIdentificador: string;
  rolActor: string;
  accion: string;
  modulo: string;
  entidad: string;
  registroId: string;
  fechaHora: string;
  resultado: string;
  motivo: string | null;
};
export const roleLabel = (r: string) =>
  ({
    ADMINISTRADOR: "Administrador",
    DOCENTE: "Docente",
    ALUMNO: "Estudiante",
    ESTUDIANTE: "Estudiante",
    APODERADO: "Apoderado",
  })[r] || r;
