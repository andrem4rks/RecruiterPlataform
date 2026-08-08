import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { authGuard } from './auth.guard';

describe('auth guards', () => {
  const loginTree = {} as UrlTree;
  const router = {
    createUrlTree: vi.fn(() => loginTree),
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
});
