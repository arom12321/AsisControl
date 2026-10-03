export type Period = {
  id: number;
  nombre: string;
  orden: number;
  fechaInicio: string;
  fechaFin: string;
  estado: string;
  version: number;
};
export type Grade = {
  id: number;
  numero: number;
  capacidadDefault: number;
  activo: boolean;
  version: number;
};
export type Section = {
  id: number;
  nombre: string;
  grado: number;
  anioAcademico: number;
  capacidadMaxima: number;
  ocupacion: number;
  vacantes: number;
  activo: boolean;
  version: number;
};
export type AcademicYear = {
  id: number;
  anio: number;
  fechaInicio: string;
  fechaFin: string;
  estado: string;
  version: number;
  admisionAbierta: boolean;
  periodos: Period[];
  grados: Grade[];
  secciones: Section[];
};
export type History = {
  id: number;
  actor: string;
  rol: string;
  fecha: string;
  accion: string;
  motivo: string | null;
  anterior: string | null;
  nuevo: string | null;
};
export const stateLabel = (state: string) =>
  state
    .replaceAll("_", " ")
    .toLowerCase()
    .replace(/^./, (s) => s.toUpperCase());
export const dateLabel = (date: string | null) =>
  date
    ? new Date(date).toLocaleString("es-PE", { timeZone: "America/Lima" })
    : "—";
