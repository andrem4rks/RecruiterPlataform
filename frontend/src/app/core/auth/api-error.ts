import { HttpErrorResponse } from '@angular/common/http';
import { ApiProblem } from './auth.models';

export function mensagemDaApi(error: unknown, fallback: string): string {
  if (!(error instanceof HttpErrorResponse)) {
    return fallback;
  }
  const problem = error.error as ApiProblem | null;
  return problem?.detail?.trim() || fallback;
}
