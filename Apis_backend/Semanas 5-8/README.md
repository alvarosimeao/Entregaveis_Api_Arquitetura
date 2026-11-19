# API de alunos — semana 5

Marco de validação de entrada e tratamento global de erros. O módulo reaproveita o CRUD das semanas 3 e 4. Requer Java 21, Maven e PostgreSQL. Defina BANCO_URL, BANCO_USUARIO e BANCO_SENHA pelo ambiente, execute mvn spring-boot:run e use registros fictícios. Para testar, execute mvn test.

POST e PUT em /alunos exigem nome e idade entre 1 e 130. A matrícula, quando informada, é única. A API responde a JSON inválido, rota ausente, conflito e falha interna com status, error, message, details e timestamp.

Expor um stack trace revelaria componentes, caminhos e partes da implementação. Um contrato estável permite que o frontend associe erros a campos e apresente o resultado de forma consistente.

## Semana 6 — paginação, ordenação e filtros

GET /alunos aceita page de 1 a 1000000, limit de 1 a 100, sort (id, nome, idade ou matricula), order (asc ou desc), nome por trecho e idadeMin/idadeMax inclusivas. Parâmetros inválidos retornam 400. A resposta separa data de meta, que contém totalItems, itemsPerPage, currentPage e totalPages.

Consultar a tabela inteira aumenta consumo de memória e de rede e pode atrasar outras requisições. A paginação é aplicada no banco. Metadados consistentes permitem usar o mesmo componente de navegação em várias telas.

## Semana 7 — autenticação e Swagger

Defina JWT_SECRET_BASE64 com pelo menos 32 bytes aleatórios codificados em Base64 antes de executar a aplicação. POST /register cria somente contas USER. POST /login retorna um Bearer de 15 minutos. Use Authorization: Bearer nos endpoints protegidos. DELETE /alunos/{id} e DELETE /usuarios/{id} exigem ADMIN. A autorização confere o perfil atual da conta em cada requisição. Abra /api-docs para usar o Swagger UI.

BCrypt aplica sal e custo computacional. Em caso de acesso indevido ao banco, o hash dificulta tentativas de recuperação de senhas e não deve ser enviado ao cliente. O OpenAPI descreve entradas, saídas e regras de autenticação e permite conferir requisições durante a integração.
