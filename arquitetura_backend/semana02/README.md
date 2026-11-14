# Semana 02 — estilos arquiteturais e estudo de caso

O estudo compara três **sistemas fictícios**. As premissas abaixo permitem justificar estilos diferentes. Os [slides da apresentação](apresentacao/estilos_arquiteturais.pdf) sintetizam a defesa oral; a versão [PPTX](apresentacao/estilos_arquiteturais.pptx) é editável.

## Premissas e requisitos críticos

| Sistema | Contexto assumido | Requisitos não funcionais críticos |
| --- | --- | --- |
| E-commerce | Operação nacional com picos sazonais e equipes separadas de catálogo, pedidos e pagamentos. | Escala elástica em consulta, disponibilidade durante campanhas e recuperação de falhas. Pedidos e pagamento exigem consistência local forte. |
| App bancário | Plataforma nova com uma equipe pequena e operações de saldo, transferência e auditoria. | Integridade transacional, segurança, rastreabilidade e controle rigoroso de mudanças. |
| Jogo multiplayer | Partidas rápidas com muitos jogadores conectados ao mesmo tempo. | Latência baixa, atualização frequente de estado e autoridade do servidor contra trapaças. |

## Decisões

### E-commerce: microsserviços por domínio

Catálogo, Pedidos e Pagamentos podem escalar e evoluir separadamente. O catálogo aceita cache e consistência eventual de informações não críticas; a confirmação de pedido mantém transação local e integração confiável com pagamento. Eventos comunicam mudanças entre domínios. **Custo assumido:** rede, observabilidade, dados distribuídos e operações mais complexas. Essa escolha pressupõe equipes e infraestrutura capazes de sustentar esses custos. Com equipe menor, um monólito modular seria um ponto de partida mais econômico.

### App bancário: monólito modular

Módulos de Contas, Transferências e Auditoria compartilham uma fronteira de implantação e podem executar uma transação de saldo sob regras claras. A separação interna de módulos mantém o código compreensível sem adicionar chamadas de rede entre passos da transferência. **Custo assumido:** deploy coordenado e escalabilidade independente limitada. A segurança depende de autenticação, autorização, criptografia, logs e controles operacionais, não do estilo sozinho.

### Jogo multiplayer: cliente-servidor autoritativo

O servidor decide o estado válido da partida; os clientes enviam comandos e recebem atualizações. Isso simplifica a sincronização e reduz a confiança em dados enviados pelo jogador. Servidores de partida podem ser distribuídos por região para reduzir latência. **Custo assumido:** infraestrutura persistente, reconexão e capacidade de suportar muitos fluxos simultâneos. O transporte em tempo real deve ser escolhido conforme o jogo e medido em testes.

## Reflexão

Não há estilo universal. Microsserviços ajudam quando há domínios e equipes independentes, mas exigem investimento operacional. O monólito modular favorece uma equipe pequena e transações concentradas, mas pode impor deploy conjunto. Cliente-servidor atende partidas autoritativas, mas não resolve sozinho latência ou disponibilidade. Orçamento, competência da equipe e prazo para lançar alteram a escolha tanto quanto os requisitos técnicos.

O [diagrama de blocos do jogo](diagramas/jogo_multiplayer.svg) ilustra a alternativa cliente-servidor de alto nível. A apresentação oral deve ser feita pelo grupo em sala; os slides estão prontos como material de apoio.

Fonte do enunciado: [AVA — Arquitetura de Software, Semana 02](https://ead.ceuma.br/mod/assign/view.php?id=25616).
