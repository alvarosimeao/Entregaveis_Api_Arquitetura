# APIs e arquitetura de software

Repositório de atividades acadêmicas em Java. O exemplo principal é uma API REST de alunos; os demais diretórios registram a evolução do exercício e estudos de arquitetura. A API é um projeto de aprendizagem, sem dados reais de estudantes.

## Por onde começar

- **[API de alunos — semanas 3 e 4](Apis_backend/Semana%203_4/):** cadastro, consulta, atualização e exclusão com Spring Boot, DTOs, JPA e PostgreSQL.
- **[Estudos de arquitetura](arquitetura_backend/README.md):** diagramas, estilos arquiteturais e registros de decisões para cenários de estudo.

A separação entre controlador, serviço e repositório deixa as responsabilidades visíveis. Os DTOs separam os dados recebidos e devolvidos pela API da entidade persistida.

## Como executar a API

Requer **JDK 21**, **Maven** e **PostgreSQL**. Crie um banco local chamado `ceuma`. No PowerShell, defina valores de demonstração e execute a aplicação:

```powershell
$env:BANCO_URL = "jdbc:postgresql://localhost:5432/ceuma"
$env:BANCO_USUARIO = "postgres"
$env:BANCO_SENHA = "<senha-local>"
$env:NOME_ALUNO = "Exemplo"
$env:RA_ALUNO = "000000"
Set-Location "Apis_backend/Semana 3_4"
mvn spring-boot:run
```

O módulo usa `spring.jpa.hibernate.ddl-auto=update` para o exercício local. Não use essa configuração como estratégia de migração em produção. Mantenha a senha e qualquer RA real fora do repositório.

## Como verificar

Com a aplicação iniciada em `localhost:8080`, crie e consulte um registro fictício:

```powershell
curl.exe -X POST http://localhost:8080/alunos -H "Content-Type: application/json" -d '{"nome":"Ana","idade":20}'
curl.exe http://localhost:8080/alunos
```

A API também oferece `GET /alunos/{id}`, `GET /alunos?nome=...`, `PUT /alunos/{id}` e `DELETE /alunos/{id}`. O endpoint `GET /alunos/ra` exibe apenas os valores configurados nas variáveis de ambiente.

## Limites atuais

O módulo ainda não possui testes automatizados, validação de entrada nem resposta HTTP específica para aluno inexistente. Esses pontos devem ser tratados antes de usá-lo fora de um ambiente de estudo. Os diagramas de arquitetura representam cenários conceituais; não descrevem sistemas internos reais de empresas citadas nos exercícios.
