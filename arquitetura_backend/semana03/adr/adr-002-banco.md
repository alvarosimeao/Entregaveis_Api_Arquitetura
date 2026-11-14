# ADR 002 — App bancário

**Status:** Aceito para o estudo de caso.

## Contexto

Equipe pequena, exigência de débito e crédito atômicos, auditoria e controle rigoroso de acesso.

## Decisão

Monólito modular com fronteiras internas para Contas, Transferências e Auditoria. Uma transação local controla cada transferência.

## Consequências

- Benefícios: consistência transacional direta e menor custo operacional inicial.
- Custos: deploy conjunto e risco de acoplamento caso os módulos ignorem suas interfaces.
- Condição: rever a divisão quando equipe, volume e necessidades de evolução independente justificarem outro estilo.

Ver [análise completa](../README.md#adr-002--app-bancário).
