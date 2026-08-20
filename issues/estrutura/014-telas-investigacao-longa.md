# #014 — Implementar as telas de investigação longa

**Trilha:** Estrutura
**Estado:** Concluída
**Depende de:** #013 e #018

## Objetivo

Transformar os dados de casos longos em orientação clara para o jogador voltar
depois de um intervalo sem se perder.

## Escopo

- Implementar a tela de Retomada com etapa, resumo, lembranças e objetivo.
- Implementar a visão de Etapas com estados concluída, atual e futura.
- Implementar o Caderno com Pistas, Pessoas, Locais e Conversas.
- Mostrar somente conteúdo já revelado pelo caminho do jogador.
- Permitir rever conversas existentes sem criar transições fora do grafo.
- Integrar História, Pausa, Retomada, Etapas e Caderno por rotas com `casoId`.
- Decidir quando abrir a Retomada com base no intervalo desde a última
  interação, sem interromper saídas curtas.
- Seguir as imagens aprovadas em `docs/telas/` e o sistema visual comum.
- Garantir texto grande, rolagem, foco, semântica e navegação por TalkBack.

## Critérios de aceite

- Ao voltar depois de um intervalo, o jogador entende onde está, o que descobriu
  e o que investigar em seguida.
- Etapas futuras não revelam título ou conteúdo narrativo oculto indevidamente.
- Caderno e Retomada são reconstruídos corretamente depois de fechar o app.
- Todas as ações possuem alternativa por botão e alvo adequado.
- Testes Compose cobrem estados vazio, parcial, longo e concluído.
- Nenhuma tela conhece ids ou textos do primeiro caso.
- Nenhuma tela ou ViewModel conhece implementações de `data/`, o container de
  composição ou o grafo de navegação.

## Verificação da etapa

- Executar testes unitários e instrumentados e percorrer manualmente as telas
  com TalkBack e texto muito grande.

## Entrega realizada

- `CarregarInvestigacao` reconstrói a sessão combinando somente as portas de
  casos e progresso com o mecanismo narrativo.
- `DecidirExibicaoDaRetomada` adota 30 minutos como intervalo mínimo: retornos
  curtos seguem direto para a história e retornos longos abrem o resumo.
- `ProjetorInvestigacao` entrega à interface somente etapas alcançadas e itens
  revelados; etapas futuras chegam sem título nem descrição.
- Retomada, Etapas e Caderno possuem rotas tipadas com `casoId` e estão ligados
  à História e à Pausa.
- Conversas são abertas como registros de leitura e não enviam escolhas ao
  mecanismo narrativo.
- Os componentes não contêm ids, textos ou condições da Taça Desaparecida.

## Verificação realizada em 20/08/2026

- 111 testes unitários aprovados.
- 50 testes instrumentados aprovados, incluindo estados vazio, parcial, longo
  e concluído.
- `lintDebug` e `assembleDebug` aprovados.
- Caderno percorrido no emulador em retrato e paisagem, com texto Grande e
  Muito grande; abas, rolagem e conteúdo permaneceram alcançáveis.
- TalkBack ativado no emulador para conferir a exposição dos controles e o
  foco inicial. O percurso manual completo de todas as telas permanece no
  fechamento consolidado de acessibilidade da #009.

O catálogo de produção ainda contém somente o piloto no esquema `1`. Retomada
e Etapas passam a ser exercitadas no fluxo público quando a #015 entregar o
primeiro conteúdo longo; isso não exige nova tela ou condição Kotlin.
