# Roteiro de apresentação da API de alunos

| Tempo | O que abrir | O que demonstrar |
| --- | --- | --- |
| 0:00–1:00 | README e Swagger | Apresentar as quatro revisões e mostrar o projeto Spring Boot, a persistência PostgreSQL, os DTOs e a separação entre controlador, serviço e repositório. |
| 1:00–2:30 | Semana 5 | Enviar um nome vazio ou uma idade inválida. Mostrar o HTTP 400 e os campos do JSON. Explicar o tratamento de conflitos e o retorno genérico quando há falha interna. |
| 2:30–4:00 | Semana 6 | Consultar a lista com filtro por nome e faixa de idade, ordenação e página. Mostrar os campos do objeto meta. |
| 4:00–6:30 | Semana 7 | Cadastrar uma conta, autenticar e usar o Bearer no Swagger. Mostrar a resposta 401 sem token e 403 quando a conta comum tenta uma rota administrativa. |
| 6:30–8:45 | Semana 8 | Abrir a coleção Postman e as evidências. Mostrar consulta por ID e o retorno 404 de um aluno inexistente. Indicar o uso de BCrypt e conferir que a API não retorna senha. |
| 8:45–10:00 | Fechamento | Explicar como testes, documentação e configuração por ambiente permitem validar a API antes da publicação. Indicar a etapa de hospedagem pública pendente. |

Use contas e registros fictícios. Remova senhas e tokens de qualquer captura que acompanhar a atividade. A demonstração deste roteiro usa a URL local configurada no Swagger e na coleção.
