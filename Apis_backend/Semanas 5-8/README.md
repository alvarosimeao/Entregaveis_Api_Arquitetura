# API de alunos — semana 5

Marco de validação de entrada e tratamento global de erros. O módulo reaproveita o CRUD das semanas 3 e 4. Requer Java 21, Maven e PostgreSQL. Defina BANCO_URL, BANCO_USUARIO e BANCO_SENHA pelo ambiente, execute mvn spring-boot:run e use registros fictícios. Para testar, execute mvn test.

POST e PUT em /alunos exigem nome e idade entre 1 e 130. A matrícula, quando informada, é única. A API responde a JSON inválido, rota ausente, conflito e falha interna com status, error, message, details e timestamp.

Expor um stack trace revelaria componentes, caminhos e partes da implementação. Um contrato estável permite que o frontend associe erros a campos e apresente o resultado de forma consistente.
