# #006 — Implementar navegação e telas estruturais

## Objetivo

Construir o fluxo completo do aplicativo com telas realmente conectadas ao estado.

## Escopo

- Apresentação com marca **Indício** e slogan **“Toda escolha revela uma pista.”**, com transição discreta e ignorável.
- Início com “Continuar”, “Escolher caso” e “Configurações”.
- Catálogo agrupado/preparado para as cinco categorias, diferenciando casos disponíveis e futuros sem promessas enganosas.
- Tela principal da história como destino navegável.
- Pausa com continuar, configurações, reiniciar caso e voltar ao início; confirmar ações que descartem progresso.
- Conclusão do caso com final, pistas explicadas e opções de jogar novamente/voltar ao catálogo.
- Configurações funcionais ligadas ao DataStore.
- Sobre com créditos/informações e, somente nela, o texto exato:
  > Indício é uma experiência de entretenimento e estímulo cognitivo. Não substitui avaliação, tratamento ou acompanhamento médico.
- Restaurar navegação coerente ao reabrir o aplicativo.

## Critérios de aceite

- Todos os oito tipos de tela solicitados são alcançáveis e funcionais.
- Voltar do sistema e navegação interna não criam loops nem perdem escolhas salvas.
- “Continuar” abre o último caso/cena válido.
- Não há aviso médico fora da tela Sobre.
- Testes básicos verificam rotas principais e ações dos botões.

## Verificação da etapa

- Executar testes de navegação e compilação/instrumentação disponíveis.

