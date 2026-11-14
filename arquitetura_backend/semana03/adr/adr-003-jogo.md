# ADR 003 — Jogo multiplayer

**Status:** Aceito para o estudo de caso.

## Contexto

Partidas rápidas precisam de sincronização de estado, validação de comandos e latência controlada por região.

## Decisão

Cliente-servidor autoritativo. Cada instância regional de partida valida comandos e publica o estado; matchmaking organiza as partidas.

## Consequências

- Benefícios: regras centralizadas, menos confiança no cliente e escala por instância de partida.
- Custos: servidores ativos, reconexão e latência para jogadores distantes.
- Condição: medir o transporte e dimensionar regiões com dados de uso antes de assumir uma meta de latência.

Ver [análise completa](../README.md#adr-003--jogo-multiplayer).
