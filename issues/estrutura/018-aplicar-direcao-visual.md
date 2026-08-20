# #018 — Aplicar a direção visual contemporânea

**Trilha:** Estrutura
**Estado:** Concluída
**Depende de:** #008 e #019; deve preservar os requisitos automatizados da #009

## Objetivo

Transformar as imagens aprovadas em `docs/telas/` no sistema visual Compose do
aplicativo, preservando comportamento, acessibilidade e independência dos casos.

## Escopo

- Consolidar tokens de cor, tipografia, espaçamento, forma, borda e elevação no
  tema do aplicativo.
- Criar componentes reutilizáveis para barras, cabeçalhos, cartões, ações,
  estados de catálogo, registros e navegação do caderno.
- Atualizar Início, Catálogo, História, revelação de carta, Conclusão, Pausa,
  Configurações e Sobre conforme as imagens aprovadas.
- Fornecer os componentes visuais usados por Retomada, Etapas e Caderno na #014.
- Manter estado e navegação fora dos componentes puramente visuais.
- Preservar alvos mínimos, contraste, rolagem, semântica e redução de movimentos.
- Validar texto grande e muito grande sem truncamento.
- Manter o conteúdo específico de casos apenas em parâmetros e previews.
- Manter componentes visuais independentes de dados, composição e navegação.

## Critérios de aceite

- Todas as telas-base seguem a mesma linguagem visual e os mesmos tokens.
- O código não repete medidas ou cores que já pertencem ao tema.
- Componentes funcionam com textos curtos, longos e sem conteúdo opcional.
- As telas continuam operáveis por TalkBack e por botões.
- Comparação manual com os PNGs não encontra divergências estruturais sem
  justificativa documentada.
- Testes de contraste e interface existentes continuam passando.

## Verificação da etapa

- Gerar capturas no emulador nas orientações retrato e paisagem, com texto
  grande e muito grande, e compará-las com `docs/telas/`.

Verificado em 20/08/2026:

- Início, Catálogo e História comparados no emulador API 37 em retrato.
- Configurações e História conferidas em paisagem com texto muito grande; a
  arte da cena recebeu limite responsivo para manter o texto na primeira vista.
- `./gradlew test` — 107 testes aprovados.
- `./gradlew lintDebug` — aprovado.
- `./gradlew assembleDebug` — aprovado.
- `./gradlew connectedDebugAndroidTest` — 46 testes aprovados no emulador.
