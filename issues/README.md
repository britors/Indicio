# Backlog local — Indício

O backlog está dividido pelo tipo de entrega:

- [`estrutura/`](estrutura/): motor, formato dos casos, persistência, interface,
  acessibilidade, testes e empacotamento;
- [`casos/`](casos/): roteiro, conteúdo, artes e publicação de cada investigação.

A separação evita misturar uma capacidade reutilizável do aplicativo com uma
decisão narrativa da Taça Desaparecida. Se uma necessidade servirá a qualquer
caso, ela pertence a Estrutura. Se descreve enredo, pistas, personagens ou artes
de uma história, pertence a Casos.

Os números existentes foram preservados. As issues #012–#019 são locais e ainda
precisam ser criadas ou vinculadas no GitHub quando o trabalho começar.

## Situação atual

### Estrutura

| Issue | Entrega | Estado | Depende de |
|---|---|---|---|
| [#001 — Fundação Android](estrutura/001-fundacao-do-projeto.md) | Aplicativo Compose offline | Concluída | — |
| [#002 — Modelo e validação JSON](estrutura/002-modelo-e-validacao-de-casos.md) | Esquema `1` e carregamento estrito | Concluída | #001 |
| [#003 — Mecanismo narrativo](estrutura/003-mecanismo-narrativo.md) | Escolhas, pistas e finais | Concluída | #002 |
| [#004 — Persistência e preferências](estrutura/004-persistencia-e-preferencias.md) | Progresso por caso e configurações | Concluída | #001, #003 |
| [#006 — Navegação e telas-base](estrutura/006-navegacao-e-telas-estruturais.md) | Fluxo do MVP | Concluída | #001, #004 |
| [#007 — Experiência e narração](estrutura/007-experiencia-da-historia.md) | História jogável e TTS | Concluída | #003, #006 |
| [#008 — Sistema visual](estrutura/008-identidade-visual.md) | Identidade e componentes comuns | Concluída no MVP | #006, #007 |
| [#009 — Acessibilidade](estrutura/009-acessibilidade.md) | Validação final de acesso e conforto | Parcial | encerrar após #014, #018 |
| [#010 — Qualidade integrada](estrutura/010-testes-e-qualidade.md) | Regressão e fluxos ponta a ponta | Parcial | #009, #013, #014, #018 |
| [#011 — Documentação e entrega](estrutura/011-documentacao-e-entrega.md) | Projeto reproduzível e APK validado | Parcial | #010 |
| [#019 — DDD, Clean Architecture, SOLID e Clean Code](estrutura/019-ddd-clean-architecture-clean-code.md) | Fronteiras e linguagem protegidas | Concluída | #001–#004 |
| [#012 — Esquema narrativo longo](estrutura/012-esquema-narrativo-longo.md) | Contrato da versão `2` | Concluída | #002, #003, #019 |
| [#013 — Núcleo de casos longos](estrutura/013-implementar-casos-longos.md) | Domínio, validação e persistência | Concluída | #012, #019 |
| [#014 — Telas de investigação longa](estrutura/014-telas-investigacao-longa.md) | Retomada, Etapas e Caderno | Concluída | #013, #018 |
| [#018 — Direção visual contemporânea](estrutura/018-aplicar-direcao-visual.md) | Implementação Compose dos PNGs aprovados | Concluída | #008, #019 |

### Casos

| Issue | Entrega | Estado | Depende de |
|---|---|---|---|
| [#005 — Piloto da Taça Desaparecida](casos/005-caso-taca-desaparecida.md) | Prova curta do esquema `1` | Concluída como piloto | #002, #003 |
| [#015 — Taça: versão longa](casos/015-taca-versao-longa.md) | Primeiro roteiro de produção | Concluída | #012, #005 |
| [#016 — Artes da Taça longa](casos/016-artes-taca-versao-longa.md) | Recursos finais do caso | Pendente | #008, #015 |
| [#017 — Publicar a Taça](casos/017-integrar-publicar-taca.md) | Primeiro caso público validado | Pendente | #009–#016 e #018 aplicáveis |

## Ordem de entrega recomendada

```text
Base arquitetural:       #019 ─┬→ #012 → #013 ────────→ #014 ──────┐
                              │    └──→ #015 → #016 ───────────────┤
Sistema visual:          #008 ─┴→ #018 ────────────────────────────┤
Validação final:              #014 + #018 → #009 → #010 → #011
                                                        └─→ #017
```

Com a especificação #012, o núcleo #013, a interface #014 e a direção visual
#018 concluídos, a #015 entrega um roteiro expandido no JSON v2. O conteúdo `5`
restaura duas escolhas por cena após feedback de uso e precisa de uma inspeção
final antes da #016 produzir as artes definitivas.
A validação manual de acessibilidade #009 e o fechamento de qualidade #010/#011
acontecem sobre as telas finais, evitando repetir uma certificação do leiaute
antigo.

## O que significa estar preparado para novos casos

Ao concluir #017, adicionar uma investigação na mesma categoria e no esquema
vigente deve exigir somente:

1. escrever e validar o JSON;
2. adicionar a entrada no catálogo;
3. produzir os recursos no namespace do caso;
4. passar pelas revisões editorial, jurídica, visual e de acessibilidade;
5. executar a suíte de publicação.

Não deve ser necessário criar rota, ViewModel, tabela, tela ou condição Kotlin
para o novo enredo. Se isso for necessário, abre-se primeiro uma issue na trilha
Estrutura para a capacidade genérica que está faltando.

## Definição de pronto — Estrutura

- Contratos independentes de qualquer enredo.
- Domínio puro, casos de uso voltados para dentro e infraestrutura nas bordas.
- DTOs e entidades de persistência não vazam para os agregados.
- Revisão SOLID concluída: responsabilidade coesa, extensão sem condicionais de
  caso, contratos substituíveis, portas segregadas e dependências invertidas.
- Estados e falhas recuperáveis cobertos por teste.
- Sem regressão em casos do formato suportado.
- Acessibilidade considerada no componente, não corrigida caso a caso.
- Documentação e migrações atualizadas junto com o código.
- Testes pertinentes, lint e build executados.

## Definição de pronto — Casos

- Grafo completo, coerente e sem caminhos presos.
- Duração vem de conteúdo e descoberta, sem preenchimento ou retenção
  manipulativa.
- História leve, sem violência e com finais positivos.
- Nomes, símbolos e imagens passaram pelo portão jurídico.
- Toda informação necessária existe em texto e é acessível.
- Artes têm autoria/licença e funcionam offline.
- Leitura humana e testes de publicação concluídos.

## Regras globais

- Kotlin, Compose Material 3, Activity única, MVVM e fluxo unidirecional.
- DDD no contexto Investigação narrativa, SOLID, regra de dependência da Clean
  Architecture e critérios de Clean Code descritos em `docs/arquitetura.md`.
- Nenhuma pressa ou decisão arquitetural autoriza romper SOLID. Uma solução que
  pareça exigir isso deve ser redesenhada antes da implementação.
- Casos e transições vivem nos dados; o código não conhece soluções de enredo.
- Totalmente offline, sem conta, anúncios, telemetria ou coleta médica.
- Sem tempo limite, punição, morte, “game over” ou mensagens de fracasso.
- Botões são o meio principal de interação; gestos são opcionais.
- O aviso de saúde aparece somente em Sobre.
- Nenhuma issue termina com TODO essencial ou implementação simulada em
  produção.
