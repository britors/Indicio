# #013 — Implementar o núcleo de casos longos

**Trilha:** Estrutura
**Estado:** Concluída
**Depende de:** #012 e #019

## Objetivo

Implementar no domínio, carregamento e persistência o esquema aprovado para
investigações longas, mantendo o motor independente de qualquer caso. A fonte
normativa é `docs/esquema-narrativo-v2.md`.

## Escopo

- Implementar DTOs serializáveis, mapeadores e modelos puros de domínio conforme
  a política de compatibilidade definida na #012.
- Ler o catálogo `2` e casos `1` e `2` simultaneamente, mantendo DTOs e
  mapeadores separados quando os contratos divergirem.
- Validar etapas, objetivos, personagens, locais, conversas, resumos e todas as
  referências cruzadas.
- Expor na sessão a etapa atual, o objetivo e o conteúdo revelado do caderno.
- Derivar informações da sequência de escolhas sempre que possível, evitando
  duplicação no banco.
- Persistir o instante da última interação e os metadados mínimos necessários à
  decisão de retomada.
- Persistir `versaoEsquema` e `versaoConteudo` e aplicar a matriz de
  compatibilidade sem apagar progresso silenciosamente.
- Criar a migração Room necessária sem apagar progresso ou histórico.
- Manter `RepositorioCasos` como fronteira de origem do conteúdo.
- Tratar progresso incompatível conforme a política definida, sem falha fatal.
- Testar simultaneamente dois casos, com progresso independente.

## Critérios de aceite

- Um fixture do esquema longo é carregado, validado, jogado, salvo e
  reconstruído deterministicamente; os dois fixtures da #012 cobrem formatos
  narrativos diferentes.
- Etapa, objetivo e caderno refletem apenas o caminho já percorrido.
- Um caso não consegue revelar conteúdo futuro de outro caminho ou de outro
  caso.
- A migração do banco é coberta por teste.
- Casos do esquema anterior seguem a política documentada.
- Não existe `if`, `when`, id de cena ou título específico da Taça no fluxo de
  produção.
- Domínio e aplicação continuam sem dependências de Android, JSON, banco ou
  interface, conforme os testes arquiteturais.

## Verificação da etapa

- `./gradlew test`: 107 testes unitários aprovados.
- `./gradlew lintDebug`: aprovado.
- `./gradlew connectedDebugAndroidTest`: 46 testes aprovados no emulador.
- Migração Room 1→2 validada contra o schema exportado, preservando progresso e
  histórico legados como revisão `1/1`.
- Os dois fixtures v2 foram carregados, validados, percorridos e reconstruídos;
  o catálogo misto manteve um caso v1 e dois casos v2 simultaneamente.
