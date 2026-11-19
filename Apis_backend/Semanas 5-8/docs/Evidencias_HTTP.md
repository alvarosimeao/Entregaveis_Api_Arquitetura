# Evidências de execução da API

Execução HTTP local com PostgreSQL 18.6. Contas, matrículas e nomes de teste são fictícios. Foram omitidos os valores de senha, hash e JWT.

| Verificação | Resultado observado |
| --- | --- |
| Especificação OpenAPI | 200 |
| Swagger UI local | 200 |
| Cadastro público | 201 |
| PostgreSQL · senha persistida | Hash BCrypt, valor omitido |
| Cadastro duplicado | 409 — CONFLICT |
| Senha incorreta | 401 — UNAUTHORIZED |
| Perfil sem token | 401 — UNAUTHORIZED |
| Perfil com token inválido | 401 — UNAUTHORIZED |
| Login com JWT | 200 |
| Perfil autenticado | 200 |
| Usuário comum na rota administrativa | 403 — FORBIDDEN |
| Validação de nome e idade | 400 — VALIDATION_ERROR |
| JSON truncado | 400 — MALFORMED_JSON |
| Rota inexistente | 404 — NOT_FOUND |
| Aluno inexistente | 404 — NOT_FOUND |
| Criação de aluno | 201 |
| Conflito de matrícula | 409 — CONFLICT |
| Atualização de aluno | 204 |
| Consulta de aluno | 200 |
| Filtro, ordenação e paginação combinados | 200 |
| Limite acima do máximo | 400 — VALIDATION_ERROR |
| Falha de acesso à tabela em banco descartável | 500 — INTERNAL_ERROR |
| API após restauração da tabela | 200 |
| Login do perfil administrativo | 200 |
| Admin exclui aluno | 204 |
| Admin exclui conta de demonstração | 204 |
| Token da conta removida é recusado | 401 — UNAUTHORIZED |

A paginação retornou 2 itens totais, 1 item por página, página atual 2 e 2 páginas totais. No teste de falha, a tabela de alunos foi renomeada apenas no banco descartável, provocando resposta 500 genérica, e restaurada antes da última consulta.

Swagger verificado no ambiente local. A etapa de deploy público depende de hospedagem.

A suíte Maven executou quatro testes, todos aprovados. A coleção Postman foi executada pelo Newman e passou 16 requisições e 29 asserções. A tabela acima reúne as 27 verificações da execução HTTP independente com PostgreSQL.

- [Swagger UI local](Swagger_local.png)
- [Cadastro, login e acesso ao perfil](Fluxos_HTTP_Autenticacao.png)
- [Paginação, validação e tratamento de falhas](Fluxos_HTTP_Dados_e_Erros.png)
