import { computed, inject, Injectable, signal } from '@angular/core';
import { LoginResponse, PerfilUsuario, Usuario } from './auth.models';
import { TokenStorageService } from './token-storage.service';

@Injectable({ providedIn: 'root' })
export class AuthSessionService {
  private readonly storage = inject(TokenStorageService);
  private readonly tokenState = signal<string | null>(this.storage.obter());
  private readonly usuarioState = signal<Usuario | null>(null);

  readonly token = this.tokenState.asReadonly();
  readonly usuario = this.usuarioState.asReadonly();
  readonly autenticado = computed(() => this.tokenState() !== null && this.usuarioState() !== null);

  iniciar(response: LoginResponse): void {
    this.storage.salvar(response.token);
    this.tokenState.set(response.token);
    this.usuarioState.set(response.usuario);
  }

  definirUsuario(usuario: Usuario): void {
    this.usuarioState.set(usuario);
  }

  encerrar(): void {
    this.storage.remover();
    this.tokenState.set(null);
    this.usuarioState.set(null);
  }

  possuiPerfil(...perfis: PerfilUsuario[]): boolean {
    const usuario = this.usuarioState();
    return usuario !== null && perfis.includes(usuario.perfil);
  }

  rotaInicial(): string {
    const perfil = this.usuarioState()?.perfil;
    if (perfil === 'ADMIN') {
      return '/administracao/usuarios';
    }
    if (perfil === 'RESPONSAVEL') {
      return '/gestao/vagas';
    }
    return '/vagas';
  }
}
