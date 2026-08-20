# #019 — Consolidar DDD, Clean Architecture, SOLID e Clean Code

**Trilha:** Estrutura
**Estado:** Concluída
**Depende de:** #001–#004

## Objetivo

Tornar as fronteiras arquiteturais explícitas e verificáveis antes da evolução
para casos longos, mantendo o domínio da investigação independente de formato,
framework e composição, e tornar SOLID uma regra permanente de evolução.

## Escopo

- Definir o contexto delimitado Investigação narrativa e sua linguagem ubíqua.
- Identificar `Caso` e `SessaoInvestigacao` como raízes dos agregados de
  conteúdo e percurso.
- Manter modelos do domínio sem Android, serialização, banco, caminhos de
  arquivo ou versão do contrato externo.
- Representar JSON com DTOs em `data/caso/dto/` e convertê-los numa camada
  anticorrupção.
- Criar `application/` para casos de uso que combinem portas ou expressem
  política; mover `ObterCasoParaContinuar` para essa camada.
- Restringir o container à raiz de composição; telas e ViewModels recebem
  portas e casos de uso explícitos.
- Proteger as dependências de domínio, aplicação, dados e interface com testes
  arquiteturais.
- Aplicar os cinco princípios SOLID na divisão de responsabilidades, evolução
  de capacidades, contratos das portas, tamanho das interfaces e direção das
  dependências.
- Documentar critérios de Clean Code sem criar classes ou camadas cerimoniais.

## Critérios de aceite

- `domain/` não importa Android, AndroidX, serialização nem camadas externas.
- `application/` depende somente do domínio e das bibliotecas essenciais ao
  contrato assíncrono.
- `ui/` não importa `data/`, `di/` nem `navegacao/`.
- O adaptador JSON converte DTOs para o domínio antes da validação e entrega.
- Somente a composição conhece implementações concretas e o container inteiro.
- Revisões verificam responsabilidade coesa, extensão sem condicionais de caso,
  substituição segura das portas, interfaces segregadas e inversão de
  dependência.
- A suíte unitária, incluindo as regras arquiteturais, passa.
- `docs/arquitetura.md` e `CONTRIBUTING.md` descrevem as mesmas fronteiras.

## Verificação da etapa

- `./gradlew test` — aprovado em 20/08/2026.
