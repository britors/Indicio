# #012 — Especificar o esquema narrativo para casos longos

**Trilha:** Estrutura
**Estado:** Concluída
**Depende de:** #002, #003, #019 e `docs/arquitetura.md`

## Objetivo

Definir, antes de escrever o segundo formato de conteúdo, o contrato que dará
suporte a investigações longas, retomáveis e compreensíveis ao longo de várias
sessões.

## Escopo

- Especificar a versão `2` do esquema de casos, sem acrescentar campos soltos à
  versão `1`.
- Definir etapas ou capítulos, objetivo atual e resumos de etapa.
- Definir índices de personagens, locais e conversas já reveladas.
- Definir resumo de retomada escrito pelo autor e lembranças essenciais.
- Decidir quais dados são declarados no JSON, derivados do caminho e
  persistidos no banco.
- Definir `versaoConteudo` e a política para progresso criado numa revisão
  anterior do mesmo caso.
- Definir compatibilidade com casos do esquema `1` e mensagens para versões não
  suportadas.
- Especificar ids estáveis, referências cruzadas e todas as novas regras do
  validador.
- Separar explicitamente o contrato JSON versionado dos agregados e valores do
  domínio, usando a linguagem ubíqua de Investigação narrativa.
- Atualizar `docs/arquitetura.md` e `docs/como-criar-novos-casos.md` com o
  contrato aprovado.
- Criar um fixture completo de exemplo, sem conteúdo destinado à publicação.

## Fora do escopo

- Implementar telas Compose.
- Escrever a versão longa da Taça.
- Baixar casos por rede.

## Critérios de aceite

- Há uma especificação inequívoca de todos os campos, obrigatoriedade e valores
  padrão.
- Um exemplo mostra catálogo, etapas, personagens, locais, cenas, escolhas,
  pistas, retomada e final.
- Está decidido como um progresso do esquema `1` será preservado, migrado ou
  reiniciado.
- Nenhum campo depende de nome ou regra específica da Taça Desaparecida.
- A especificação permite montar as telas de Retomada, Etapas e Caderno já
  desenhadas em `docs/telas/`.

## Verificação da etapa

- Contrato normativo publicado em `docs/esquema-narrativo-v2.md`.
- Catálogo e dois casos completos em `docs/exemplos/esquema-v2/`.
- Revisado contra um caso orientado por objetos e outro orientado por pessoas e
  conversas.
- JSON validado sintaticamente com `jq` e semanticamente quanto a ids,
  referências, etapas, objetivos, revelações, alcançabilidade, ciclos e finais
  em 20/08/2026.
