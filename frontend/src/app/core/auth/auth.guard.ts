import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthSessionService } from './auth-session.service';

export const authGuard: CanActivateFn = (_route, state) => {
  const session = inject(AuthSessionService);
  const router = inject(Router);
  return session.autenticado()
    ? true
    : router.createUrlTree(['/entrar'], { queryParams: { retorno: state.url } });
};
