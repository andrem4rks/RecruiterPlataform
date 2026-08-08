import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthService } from '../../core/auth/auth.service';
import { AuthSessionService } from '../../core/auth/auth-session.service';
import { mensagemDaApi } from '../../core/auth/api-error';

function senhasIguais(control: AbstractControl): ValidationErrors | null {
  const senha = control.get('senha')?.value as string | undefined;
  const confirmacao = control.get('confirmacaoSenha')?.value as string | undefined;
  return senha === confirmacao ? null : { senhasDiferentes: true };
}

@Component({
  selector: 'app-cadastro',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './cadastro.component.html',
  styleUrl: './cadastro.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CadastroComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly session = inject(AuthSessionService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  readonly enviando = signal(false);
  readonly erro = signal<string | null>(null);
  readonly hoje = new Date().toISOString().slice(0, 10);
  readonly form = this.formBuilder.nonNullable.group(
    {
      nome: ['', [Validators.required, Validators.maxLength(120)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(180)]],
      dataAdmissao: ['', [Validators.required]],
      senha: [
        '',
        [
          Validators.required,
          Validators.minLength(8),
          Validators.maxLength(72),
          Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\p{L}\p{N}]).+$/u),
        ],
      ],
      confirmacaoSenha: ['', [Validators.required]],
    },
    { validators: senhasIguais },
  );

  cadastrar(): void {
    if (this.form.invalid || this.enviando()) {
      this.form.markAllAsTouched();
      return;
    }

    const valores = this.form.getRawValue();
    const payload = {
      nome: valores.nome,
      email: valores.email,
      senha: valores.senha,
      dataAdmissao: valores.dataAdmissao,
    };
    this.enviando.set(true);
    this.erro.set(null);
    this.auth.cadastrar(payload).pipe(
      finalize(() => this.enviando.set(false)),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe({
      next: () => void this.router.navigateByUrl(this.session.rotaInicial()),
      error: (error: unknown) => {
        this.erro.set(mensagemDaApi(error, 'Não foi possível criar sua conta. Tente novamente.'));
      },
    });
  }
}
