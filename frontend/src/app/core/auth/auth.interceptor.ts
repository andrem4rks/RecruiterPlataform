import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthSessionService } from './auth-session.service';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const session = inject(AuthSessionService);
  const router = inject(Router);
  const token = session.token();
  const autenticada = token === null
    ? request
    : request.clone({ setHeaders: { Authorization: `Bearer ${token}` } });

  return next(autenticada).pipe(
    catchError((error: HttpErrorResponse) => {
      const chamadaDeLogin = request.url.endsWith('/auth/login');
      const chamadaDeCadastro = request.url.endsWith('/auth/cadastro');
      if (error.status === 401 && !chamadaDeLogin && !chamadaDeCadastro) {
        session.encerrar();
        void router.navigate(['/entrar']);
      }
      return throwError(() => error);
    }),
  );
};
