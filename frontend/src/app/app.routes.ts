import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: 'entrar',
    loadComponent: () => import('./features/login/login.component').then((module) => module.LoginComponent),
    title: 'Entrar | Vagas internas',
  },
  {
    path: 'cadastro',
    loadComponent: () =>
      import('./features/cadastro/cadastro.component').then((module) => module.CadastroComponent),
    title: 'Criar conta | Vagas internas',
  },
  {
    path: 'vagas',
    canActivate: [authGuard],
    loadComponent: () => import('./features/home/home.component').then((module) => module.HomeComponent),
    title: 'Vagas internas',
  },
  { path: '', pathMatch: 'full', redirectTo: 'vagas' },
  { path: '**', redirectTo: 'vagas' },
];
