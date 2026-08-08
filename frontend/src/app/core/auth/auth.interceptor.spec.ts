import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { authInterceptor } from './auth.interceptor';
import { AuthSessionService } from './auth-session.service';
import { AuthApiService } from './auth-api.service';

describe('authInterceptor', () => {
  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        { provide: Router, useValue: { navigate: vi.fn() } },
      ],
    });
  });

  it('adiciona o bearer token às chamadas autenticadas', () => {
    const session = TestBed.inject(AuthSessionService);
    const api = TestBed.inject(AuthApiService);
    const http = TestBed.inject(HttpTestingController);
    session.iniciar({
      token: 'jwt-teste',
      tipo: 'Bearer',
      expiraEm: '2026-08-08T00:15:00Z',
      usuario: { id: 3, nome: 'Pessoa', email: 'pessoa@empresa.com', perfil: 'CANDIDATO' },
    });

    api.usuarioAtual().subscribe();

    const request = http.expectOne('/api/v1/usuarios/me');
    expect(request.request.headers.get('Authorization')).toBe('Bearer jwt-teste');
    request.flush({ id: 3, nome: 'Pessoa', email: 'pessoa@empresa.com', perfil: 'CANDIDATO' });
    http.verify();
  });
});
