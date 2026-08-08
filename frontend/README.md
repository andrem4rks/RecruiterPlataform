# Frontend

Aplicação Angular 21 para autenticação e acesso ao portal interno de vagas.

## Execução

```powershell
npm ci
npm start
```

O servidor de desenvolvimento usa `proxy.conf.json` para encaminhar `/api` ao backend em
`http://localhost:8080`.

## Verificações

```powershell
npm run lint
npm test
npm run build
```

O token JWT é mantido em memória e restaurado de `sessionStorage`. O cadastro aceita apenas
o domínio corporativo configurado no backend e sempre cria uma conta `CANDIDATO`.
