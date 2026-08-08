import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CadastroPayload, LoginPayload, LoginResponse, Usuario } from './auth.models';

@Injectable({ providedIn: 'root' })
export class AuthApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1';

  login(payload: LoginPayload): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/auth/login`, payload);
  }

  cadastrar(payload: CadastroPayload): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/auth/cadastro`, payload);
  }

  usuarioAtual(): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.baseUrl}/usuarios/me`);
  }
}
