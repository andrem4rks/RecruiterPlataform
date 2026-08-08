# Backend

API Spring Boot da plataforma de recrutamento interno.

## Autenticação

O backend usa autenticação stateless com JWT assinado por HMAC-SHA256. Senhas são
armazenadas com BCrypt (custo 12). As rotas estão sob o prefixo `/api/v1`:

- `POST /api/v1/auth/login` — público;
- `POST /api/v1/auth/cadastro` — público, restrito ao domínio corporativo e sempre `CANDIDATO`;
- `GET /api/v1/usuarios/me` — exige `Authorization: Bearer <token>`;
- `PATCH /api/v1/usuarios/{id}/perfil` — somente `ADMIN`;
- demais rotas de `/api/v1/**` — privadas;
- escrita em `/api/v1/vagas/**` — restrita a `ADMIN` e `RESPONSAVEL`.

Variáveis de ambiente:

- `JWT_SECRET`: segredo com pelo menos 32 bytes; obrigatório sobrescrever fora do ambiente local;
- `JWT_EXPIRATION`: duração do token, com padrão local de `15m`;
- `CORS_ALLOWED_ORIGINS`: origens separadas por vírgula, com padrão `http://localhost:4200`;
- `CORPORATE_EMAIL_DOMAIN`: domínio aceito no cadastro, com padrão `empresa.com`;
- `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`: conexão PostgreSQL.

Para executar localmente a partir da raiz do repositório:

```powershell
docker compose up -d postgres
cd backend
.\mvnw.cmd spring-boot:run
```

Usuários exclusivos para demonstração local:

| Perfil | E-mail | Senha |
|---|---|---|
| ADMIN | `admin@empresa.com` | `Admin@123` |
| RESPONSAVEL | `responsavel@empresa.com` | `Resp@123` |
| CANDIDATO | `candidato@empresa.com` | `Cand@123` |

Exemplo de login:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"candidato@empresa.com","senha":"Cand@123"}'
```

Exemplo de cadastro:

```bash
curl -X POST http://localhost:8080/api/v1/auth/cadastro \
  -H "Content-Type: application/json" \
  -d '{"nome":"Nova Colaboradora","email":"nova@empresa.com","senha":"Senha@123","dataAdmissao":"2024-01-08"}'
```

## Testes

```powershell
.\mvnw.cmd test
.\mvnw.cmd verify
```
