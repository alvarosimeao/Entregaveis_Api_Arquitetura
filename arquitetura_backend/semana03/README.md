# Semana 03 — defesa arquitetural e trade-offs

Esta entrega aprofunda os três estudos fictícios da [semana 02](../semana02/README.md). As prioridades abaixo são hipóteses de projeto, não métricas observadas em sistemas reais. O [diagrama C4 simplificado do e-commerce](diagramas/ecommerce_componentes.svg) mostra a decisão de microsserviços em alto nível. Os [slides em PDF](apresentacao/defesa_arquitetural.pdf) e o [PPTX editável](apresentacao/defesa_arquitetural.pptx) apoiam a apresentação.

## Prioridades dos requisitos não funcionais

Usamos o modelo de qualidade de produto da [ISO/IEC 25010:2023](https://www.iso.org/standard/78176.html) como referência para nomear e comparar atributos de qualidade. Os critérios abaixo são **propostas para o exercício**, sujeitos a validação com stakeholders e testes.

| Sistema | Prioridade 1 | Prioridade 2 | Prioridade 3 | Critério proposto |
| --- | --- | --- | --- | --- |
| E-commerce | Confiabilidade | Eficiência de desempenho | Segurança | Confirmar pedido uma vez mesmo após repetição da requisição; manter busca responsiva em pico de campanha. |
| App bancário | Segurança | Confiabilidade | Manutenibilidade | Nenhuma transferência pode deixar débito sem crédito; toda operação sensível deixa trilha auditável. |
| Jogo multiplayer | Eficiência de desempenho | Confiabilidade | Segurança | Medir latência de comandos por região; permitir reconexão sem aceitar estado arbitrário do cliente. |

## ADR 001 — E-commerce

**Status:** Aceito para as premissas deste estudo.

**Contexto:** campanhas geram picos de leitura no catálogo; equipes distintas desenvolvem catálogo, pedidos e pagamento. O fluxo de compra exige consistência e deve tolerar repetição de mensagens.

**Decisão:** dividir Catálogo, Pedidos e Pagamentos em microsserviços por domínio, com API Gateway e eventos para integração. Cada serviço controla seus dados. Pedidos usa chave de idempotência e registra a intenção antes de publicar eventos.

**Consequências positivas:** escala independente do catálogo, deploys separados e limites de responsabilidade mais claros. **Consequências negativas:** chamadas de rede podem falhar, observabilidade distribuída é necessária e a consistência entre serviços exige fluxo de compensação. A equipe precisa de automação de deploy, métricas e rastreamento.

## ADR 002 — App bancário

**Status:** Aceito para as premissas deste estudo.

**Contexto:** equipe pequena, mudanças reguladas e transferências que exigem débito e crédito atômicos. O risco principal é inconsistência ou acesso indevido.

**Decisão:** monólito modular com módulos de Contas, Transferências e Auditoria, em uma fronteira transacional. Interfaces internas explícitas impedem acesso direto às tabelas de outros módulos.

**Consequências positivas:** transação ACID simples de raciocinar, deploy único e auditoria centralizada. **Consequências negativas:** releases exigem coordenação e módulos muito acoplados dificultariam evolução. Separar módulos só será considerado quando dados, equipe e custo operacional justificarem.

## ADR 003 — Jogo multiplayer

**Status:** Aceito para as premissas deste estudo.

**Contexto:** jogadores trocam comandos em partidas rápidas; clientes podem ter atraso ou enviar dados incorretos. O estado final precisa de uma autoridade única por partida.

**Decisão:** cliente-servidor autoritativo, com instâncias de partida por região, entrada regional e serviço de matchmaking separado. O servidor valida comandos e publica o estado resultante.

**Consequências positivas:** regras centralizadas e menor confiança no cliente, com escala por novas instâncias de partida. **Consequências negativas:** custo de servidores ativos, necessidade de reconexão e possibilidade de latência para regiões distantes. A escolha do protocolo exige medição com o perfil real de tráfego.

## Defesa para stakeholders

Cada ADR registra **por que** a equipe tomou a decisão e quais custos aceitou. O diagrama mostra as partes, mas não mostra orçamento, restrições e alternativas descartadas. Para uma diretoria, a defesa deve traduzir escolhas em impactos: investimento operacional para escalar o e-commerce em campanhas, menor risco de inconsistência no banco e custo de servidores regionais no jogo. Estimativas de dinheiro e prazo só podem ser apresentadas depois de medições e orçamento reais.

## Crise: tráfego 10 vezes maior em um mês

No e-commerce, aumentar instâncias de Catálogo e Gateway pode absorver consultas, apoiado por cache e CDN. Pedidos e Pagamentos têm limites próprios: filas controlam picos, chaves de idempotência evitam duplicidade e limites de taxa protegem serviços externos. O banco de pedidos pode continuar como gargalo; antes de prometer suporte a 10 vezes mais tráfego, a equipe precisa testar carga, estimar capacidade e definir degradação controlada (por exemplo, adiar recomendações, preservando checkout). O slide final da apresentação expõe esse risco.

A defesa oral deve ser apresentada pelo grupo em sala. Esta pasta fornece a documentação e os slides, sem alegar que a apresentação ocorreu.

Fonte do enunciado: [AVA — Arquitetura de Software, Semana 03](https://ead.ceuma.br/mod/assign/view.php?id=25615).
