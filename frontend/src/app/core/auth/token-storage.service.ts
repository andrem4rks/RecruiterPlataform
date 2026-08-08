import { Injectable } from '@angular/core';

const TOKEN_KEY = 'recruiter-platform.token';

@Injectable({ providedIn: 'root' })
export class TokenStorageService {
  obter(): string | null {
    return sessionStorage.getItem(TOKEN_KEY);
  }

  salvar(token: string): void {
    sessionStorage.setItem(TOKEN_KEY, token);
  }

  remover(): void {
    sessionStorage.removeItem(TOKEN_KEY);
  }
}
