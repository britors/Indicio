# #016 — Produzir as artes da versão longa da Taça

**Trilha:** Casos
**Estado:** Pendente
**Depende de:** #008 e roteiro estabilizado na #015

## Objetivo

Criar o conjunto visual original e offline necessário à versão longa do primeiro
caso, sem copiar identidade de clubes, competições ou pessoas reais.

O conteúdo `4` possui 127 cenas: 124 comuns distribuídas em três caminhos por
camada e três finais positivos. A direção de arte deve planejar reutilização
intencional por local e momento, sem pressupor 127 composições inteiramente
diferentes.

## Escopo

- Definir a lista de cenas que exigem arte própria e onde a reutilização de um
  local é narrativa e visualmente aceitável.
- Nomear novos recursos no namespace `cena_taca_desaparecida_<cena>`.
- Produzir cartas em retrato 2:3 coerentes com o sistema visual do aplicativo.
- Evitar logotipos, escudos, uniformes, bandeiras, mascotes, troféus e
  combinações visuais associáveis a entidades reais.
- Escrever descrição acessível objetiva para cada arte.
- Registrar autoria, arquivo-fonte, licença e data de cada recurso.
- Conferir legibilidade com texto grande e uso em paisagem.
- Otimizar os arquivos e validar sua preservação no APK de release.

## Critérios de aceite

- Toda cena publicada resolve uma arte existente e nenhuma arte fica órfã.
- A informação indispensável também está disponível em texto.
- A revisão visual não encontra marcas ou pessoas reais reconhecíveis.
- Contraste, enquadramento e composição funcionam nos tamanhos suportados.
- A origem de todos os recursos está documentada.

## Verificação da etapa

- Executar `ArteDasCartasTest`, build release e inspeção manual no aparelho em
  retrato e paisagem.
