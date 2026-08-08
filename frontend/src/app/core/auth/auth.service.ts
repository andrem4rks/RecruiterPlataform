import { inject, Injectable } from '@angular/core';
import { firstValueFrom, map, Observable, of, tap, catchError } from 'rxjs';
import { AuthApiService } from './auth-api.service';
import { CadastroPayload, LoginPayload, Usuario } from './auth.models';
import { AuthSessionService } from './auth-session.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = inject(AuthApiService);
  private readonly session = inject(AuthSessionService);

  login(payload: LoginPayload): Observable<Usuario> {
    return this.api.login(payload).pipe(
      tap((response) => this.session.iniciar(response)),
      map((response) => response.usuario),
    );
  }

  cadastrar(payload: CadastroPayload): Observable<Usuario> {
    return this.api.cadastrar(payload).pipe(
      tap((response) => this.session.iniciar(response)),
      map((response) => response.usuario),
    );
  }

  restaurarSessao(): Promise<void> {
    if (this.session.token() === null) {
      return Promise.resolve();
    }

    return firstValueFrom(
      this.api.usuarioAtual().pipe(
        tap((usuario) => this.session.definirUsuario(usuario)),
        map(() => undefined),
        catchError(() => {
          this.session.encerrar();
          return of(undefined);
        }),
      ),
    );
  }

  logout(): void {
    this.session.encerrar();
  }
}
