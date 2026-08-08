import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/auth/auth.guard';

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
  {
    path: 'gestao/vagas',
    canActivate: [authGuard, roleGuard('ADMIN', 'RESPONSAVEL')],
    loadComponent: () => import('./features/home/home.component').then((module) => module.HomeComponent),
    title: 'Gestão de vagas',
  },
  {
    path: 'administracao/usuarios',
    canActivate: [authGuard, roleGuard('ADMIN')],
    loadComponent: () => import('./features/home/home.component').then((module) => module.HomeComponent),
    title: 'Administração',
  },
  { path: '', pathMatch: 'full', redirectTo: 'vagas' },
  { path: '**', redirectTo: 'vagas' },
];
