# Contrato do módulo

O módulo de alunos das semanas 5 a 8 preserva o CRUD do exercício anterior em uma pasta separada. O README descreve o contrato HTTP, o estado da implantação e as configurações obrigatórias. Os testes em `src/test` validam cada marco. As saídas de testes contra PostgreSQL e a coleção reproduzível ficam em `docs`.

A matrícula informada é única. A listagem usa paginação por página e limite, com desempate por ID. O acesso usa um JWT de curta duração e é revalidado contra o perfil atual da conta. Apenas administradores podem excluir alunos e contas. A publicação do contrato OpenAPI usa o mesmo código e os mesmos DTOs que atendem às requisições.
