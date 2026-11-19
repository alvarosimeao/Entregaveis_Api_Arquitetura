# API acadêmica de alunos

Projeto de Análise e Desenvolvimento de Sistemas da Universidade CEUMA. O módulo reúne as semanas 5 a 8 a partir do CRUD de alunos das semanas 3 e 4. Usa Java 21, Spring Boot, JPA, PostgreSQL, Spring Security e Swagger/OpenAPI. Use somente dados fictícios.

## Executar no Windows

Crie um banco PostgreSQL de estudo com um usuário autorizado a administrar o esquema do módulo. No PowerShell, defina a conexão e a chave JWT para a sessão.

```powershell
$env:BANCO_URL = 'jdbc:postgresql://localhost:5432/ceuma'
$env:BANCO_USUARIO = 'api_local'
$env:BANCO_SENHA = Read-Host 'Senha local' -MaskInput
$env:JWT_SECRET_BASE64 = [Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
mvn test
mvn spring-boot:run
```

A propriedade BANCO_DDL_AUTO usa update para o exercício local. Para implantar, use um esquema provisionado com migrações, BANCO_DDL_AUTO=validate, PostgreSQL e chave de assinatura definidos no serviço, e HTTPS na entrada pública. PORT permite trocar a porta 8080. NOME_ALUNO e RA_ALUNO são opcionais e os valores padrão são fictícios.

Abra Swagger UI em [localhost:8080/api-docs](http://localhost:8080/api-docs). O contrato está em [localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs). A URL pública de deploy depende de hospedagem.

## Contrato de rotas

| Rota | Acesso | Resultado principal |
| --- | --- | --- |
| POST /register | Público | 201 com id, nome, e-mail e perfil USER |
| POST /login | Público | 200 com token JWT, tipo Bearer e validade de 900 s |
| GET /perfil | Bearer | 200 com os dados da conta |
| GET /alunos e GET /alunos/{id} | Bearer | 200 com lista paginada ou aluno |
| POST /alunos e PUT /alunos/{id} | Bearer | 201 para inclusão e 204 para atualização |
| DELETE /alunos/{id} e DELETE /usuarios/{id} | ADMIN | 204 para exclusão |
| GET /alunos/ra | Bearer | 200 com nome e RA configurados para demonstração |

O cadastro público sempre cria o perfil USER. Para demonstrar ADMIN, o operador registra uma conta fictícia, promove-a na tabela app_users com acesso ao banco de estudo e autentica novamente. A verificação compara a conta e o perfil atuais com o token. O e-mail é normalizado e único. O cadastro aceita senha de 8 a 72 caracteres e até 72 bytes UTF-8. Apenas o hash BCrypt é persistido, com custo 12.

## Semanas 5 e 6

POST e PUT em /alunos exigem nome e idade de 1 a 130. A matrícula é opcional e, quando informada, deve ter entre 4 e 24 letras maiúsculas ou dígitos e ser única. Erros têm status, error, message, details e timestamp. O HTTP 400 cobre validação e JSON malformado. O 404 identifica recurso ou rota inexistente. O 409 indica conflito. O 500 retorna mensagem genérica e mantém a informação técnica no servidor.

A listagem aplica filtros e paginação no banco. Exemplo: GET /alunos?nome=ana&idadeMin=18&idadeMax=35&page=1&limit=10&sort=idade&order=asc. O filtro de nome procura um trecho sem diferenciar maiúsculas de minúsculas. Os limites de idade são inclusivos. page começa em 1, limit vai até 100 e sort aceita id, nome, idade ou matricula. A resposta contém data e meta com totalItems, itemsPerPage, currentPage e totalPages. Empates são resolvidos por id.

Expor um stack trace revelaria componentes e caminhos da implementação. Um formato de erro estável ajuda o frontend a reconhecer falhas e associá-las aos campos. Retornar uma tabela inteira consome memória e rede. Com metadados previsíveis, o frontend pode reutilizar a navegação entre páginas.

## Semanas 7 e 8

BCrypt usa sal e custo computacional para dificultar a recuperação de senhas mesmo diante de acesso indevido ao banco. Swagger documenta o contrato e permite usar Bearer Auth para explorar rotas protegidas. No Swagger UI, execute o login, copie o token para Authorize e teste o perfil. Evite capturar o token nas evidências.

- [Coleção Postman](docs/API_Alunos.postman_collection.json). Importe o JSON e execute as requisições em ordem. A coleção gera uma senha fictícia e guarda o token durante a sessão.
- [Evidências HTTP e PostgreSQL](docs/Evidencias_HTTP.md).
- [Roteiro de apresentação de 10 minutos](docs/Apresentacao_10_minutos.md).

A API local valida o funcionamento com dados fictícios. Um ambiente público precisa de HTTPS, configuração de segredos, persistência, migrações e monitoramento. A documentação explica como chamar a API e os testes verificam contrato, permissões e respostas antes de disponibilizar o serviço.
