export type PerfilUsuario = 'ADMIN' | 'RESPONSAVEL' | 'CANDIDATO';

export interface Usuario {
  id: number;
  nome: string;
  email: string;
  perfil: PerfilUsuario;
}

export interface LoginResponse {
  token: string;
  tipo: 'Bearer';
  expiraEm: string;
  usuario: Usuario;
}

export interface LoginPayload {
  email: string;
  senha: string;
}

export interface CadastroPayload {
  nome: string;
  email: string;
  senha: string;
  dataAdmissao: string;
}

export interface ApiProblem {
  status?: number;
  detail?: string;
}
