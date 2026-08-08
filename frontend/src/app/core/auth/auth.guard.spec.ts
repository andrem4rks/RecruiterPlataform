import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { authGuard, roleGuard } from './auth.guard';
import { AuthSessionService } from './auth-session.service';

describe('auth guards', () => {
  const loginTree = {} as UrlTree;
  const vagasTree = {} as UrlTree;
  const router = {
    createUrlTree: vi.fn((commands: string[]) => commands[0] === '/entrar' ? loginTree : vagasTree),
  };

  beforeEach(() => {
    sessionStorage.clear();
    router.createUrlTree.mockClear();
    TestBed.configureTestingModule({
      providers: [{ provide: Router, useValue: router }],
    });
  });

  it('redireciona visitante para o login', () => {
    const result = TestBed.runInInjectionContext(() => authGuard(
      {} as ActivatedRouteSnapshot,
      { url: '/vagas' } as RouterStateSnapshot,
    ));

    expect(result).toBe(loginTree);
  });

  it('recusa rota administrativa para candidato autenticado', () => {
    const session = TestBed.inject(AuthSessionService);
    session.iniciar({
      token: 'jwt',
      tipo: 'Bearer',
      expiraEm: '2026-08-08T00:15:00Z',
      usuario: { id: 4, nome: 'Pessoa', email: 'pessoa@empresa.com', perfil: 'CANDIDATO' },
    });

    const guard = roleGuard('ADMIN');
    const result = TestBed.runInInjectionContext(() => guard(
      {} as ActivatedRouteSnapshot,
      { url: '/administracao/usuarios' } as RouterStateSnapshot,
    ));

    expect(result).toBe(vagasTree);
  });
});
