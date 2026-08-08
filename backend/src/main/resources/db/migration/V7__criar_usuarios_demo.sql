-- Usuários exclusivos para demonstração e desenvolvimento local.
-- As senhas estão codificadas com BCrypt (custo 12).

INSERT INTO competencias (nome, descricao)
VALUES ('Colaboração', 'Competência inicial dos usuários de demonstração')
ON CONFLICT DO NOTHING;

INSERT INTO usuarios (
    nome,
    email,
    senha_hash,
    perfil,
    data_admissao
)
SELECT
    dados.nome,
    dados.email,
    dados.senha_hash,
    dados.perfil,
    dados.data_admissao
FROM (
    VALUES
        (
            'Administrador Demo',
            'admin@empresa.com',
            '$2a$12$8mpJ0Sy./F.Roal.lGw0teMg8bFnPEVLIOQKe.tnZ/J7SWDOAdnk.',
            'ADMIN',
            DATE '2018-01-08'
        ),
        (
            'Responsável Demo',
            'responsavel@empresa.com',
            '$2a$12$dIBlyIhBa3BwEHqyUaFb7OGR816jhxNQQLe536gyHCPPZN7VAqZRi',
            'RESPONSAVEL',
            DATE '2019-03-11'
        ),
        (
            'Candidato Demo',
            'candidato@empresa.com',
            '$2a$12$l5gJZYQoPLcnkw3Dt3O1bO.Dp9tezPJ7nl8KF8pWRjHkDgHkk3UTK',
            'CANDIDATO',
            DATE '2022-06-20'
        )
) AS dados(nome, email, senha_hash, perfil, data_admissao)
ON CONFLICT DO NOTHING;

INSERT INTO usuarios_competencias (
    usuario_id,
    competencia_id,
    nivel,
    meses_experiencia
)
SELECT
    usuario.id,
    competencia.id,
    'INTERMEDIARIO',
    12
FROM usuarios usuario
CROSS JOIN competencias competencia
WHERE LOWER(usuario.email) IN (
    'admin@empresa.com',
    'responsavel@empresa.com',
    'candidato@empresa.com'
)
AND LOWER(competencia.nome) = LOWER('Colaboração')
ON CONFLICT DO NOTHING;
