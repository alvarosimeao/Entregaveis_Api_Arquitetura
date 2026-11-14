# ADR 001 — E-commerce

**Status:** Aceito para o estudo de caso.

## Contexto

Tráfego de catálogo cresce em campanhas. Equipes separadas cuidam de catálogo, pedidos e pagamentos. O checkout exige idempotência e consistência local.

## Decisão

Microsserviços por domínio com API Gateway. Catálogo, Pedidos e Pagamentos são serviços independentes. Mudanças entre domínios são comunicadas por eventos; cada serviço controla seus dados.

## Consequências

- Benefícios: escala independente, responsabilidades claras e ciclos de entrega separados.
- Custos: operação distribuída, falhas de rede, rastreamento entre serviços e compensação de eventos.
- Condição: a equipe deve ter automação de deploy, observabilidade e testes de integração antes de dividir os serviços em produção.

Ver [análise completa](../README.md#adr-001--e-commerce).
