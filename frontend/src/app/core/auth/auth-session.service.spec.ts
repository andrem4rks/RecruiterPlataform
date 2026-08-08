import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { AuthSessionService } from './auth-session.service';
import { TokenStorageService } from './token-storage.service';

describe('AuthSessionService', () => {
  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({ providers: [AuthSessionService, TokenStorageService] });
  });

  it('mantém token no sessionStorage', () => {
    const session = TestBed.inject(AuthSessionService);

    session.iniciar({
      token: 'jwt-teste',
      tipo: 'Bearer',
      expiraEm: '2026-08-08T00:15:00Z',
      usuario: {
        id: 1,
        nome: 'Administradora',
        email: 'admin@empresa.com',
        perfil: 'ADMIN',
      },
    });

    expect(sessionStorage.getItem('recruiter-platform.token')).toBe('jwt-teste');
    expect(session.autenticado()).toBe(true);
  });

  it('limpa completamente a sessão no logout', () => {
    const session = TestBed.inject(AuthSessionService);
    session.iniciar({
      token: 'jwt-teste',
      tipo: 'Bearer',
      expiraEm: '2026-08-08T00:15:00Z',
      usuario: { id: 2, nome: 'Pessoa', email: 'pessoa@empresa.com', perfil: 'CANDIDATO' },
    });

    session.encerrar();

    expect(session.token()).toBeNull();
    expect(session.usuario()).toBeNull();
    expect(session.autenticado()).toBe(false);
  });
});
