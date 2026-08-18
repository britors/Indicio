# Backlog local — Indício

Este diretório contém as issues do MVP Android de **Indício — “Toda escolha revela uma pista.”**

## Ordem sugerida

| Ordem | Issue | Entrega verificável | Depende de |
|---:|---|---|---|
| 1 | [#001 — Fundação do projeto](001-fundacao-do-projeto.md) | App Compose abre offline | — |
| 2 | [#002 — Modelo, carregamento e validação de casos](002-modelo-e-validacao-de-casos.md) | JSON local é carregado e validado | #001 |
| 3 | [#003 — Mecanismo narrativo](003-mecanismo-narrativo.md) | Escolhas, pistas e finais funcionam | #002 |
| 4 | [#004 — Persistência e preferências](004-persistencia-e-preferencias.md) | Progresso é salvo/restaurado | #001, #003 |
| 5 | [#005 — Caso “O Mistério da Taça Desaparecida”](005-caso-taca-desaparecida.md) | Primeiro caso completo e validado | #002, #003 |
| 6 | [#006 — Navegação e telas estruturais](006-navegacao-e-telas-estruturais.md) | Fluxo completo de telas | #001, #004 |
| 7 | [#007 — Experiência da história e narração](007-experiencia-da-historia.md) | Caso jogável com TTS | #003, #005, #006 |
| 8 | [#008 — Identidade visual e recursos gráficos](008-identidade-visual.md) | Visual clássico e recursos offline | #006, #007 |
| 9 | [#009 — Acessibilidade e conforto](009-acessibilidade.md) | Requisitos de acesso verificados | #006, #007, #008 |
| 10 | [#010 — Testes integrados e qualidade](010-testes-e-qualidade.md) | Suíte do MVP passa | #001–#009 |
| 11 | [#011 — Documentação e entrega do MVP](011-documentacao-e-entrega.md) | Projeto reproduzível e documentado | #010 |

## Marcos sugeridos

- **M1 — Núcleo narrativo:** #001–#003
- **M2 — Conteúdo e persistência:** #004–#005
- **M3 — Aplicativo jogável:** #006–#008
- **M4 — MVP pronto para avaliação:** #009–#011

## Regras globais de produto

- Kotlin, Jetpack Compose Material 3, Activity única, MVVM e fluxo unidirecional.
- `ViewModel`, `StateFlow`, coroutines, Navigation Compose, Room, DataStore e Kotlin Serialization.
- Namespace `br.com.avoren.indicio`, `minSdk 26` e dependências estáveis.
- Totalmente offline, sem conta, internet, anúncios, notificações insistentes ou coleta de dados médicos.
- Sem tempo limite, pontuação negativa, morte, “game over” ou mensagens de fracasso.
- Botões são sempre o meio principal de escolher; gestos, se existirem, são opcionais.
- O aviso de saúde aparece somente em **Sobre**.
- Nenhuma issue está concluída com TODO essencial, simulação no lugar de função real ou tela inoperante.
- Ao concluir cada issue: compilar e executar os testes pertinentes antes de iniciar a próxima.

