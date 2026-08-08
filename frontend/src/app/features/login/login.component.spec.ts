import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { NEVER } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AuthService } from '../../core/auth/auth.service';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  const auth = {
    login: vi.fn(() => NEVER),
  };

  beforeEach(async () => {
    auth.login.mockClear();
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [provideRouter([]), { provide: AuthService, useValue: auth }],
    }).compileComponents();
  });

  it('não envia credenciais quando o formulário está inválido', () => {
    const fixture = TestBed.createComponent(LoginComponent);

    fixture.componentInstance.entrar();

    expect(auth.login).not.toHaveBeenCalled();
    expect(fixture.componentInstance.form.controls.email.touched).toBe(true);
  });

  it('envia e-mail e senha quando o formulário está válido', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    fixture.componentInstance.form.setValue({
      email: 'pessoa@empresa.com',
      senha: 'Senha@123',
    });

    fixture.componentInstance.entrar();

    expect(auth.login).toHaveBeenCalledWith({
      email: 'pessoa@empresa.com',
      senha: 'Senha@123',
    });
  });
});
