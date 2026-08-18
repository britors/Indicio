# #010 — Consolidar testes integrados e qualidade do MVP

## Objetivo

Comprovar que o MVP é funcional, restaurável e navegável, além de impedir regressões no conteúdo.

## Escopo

- Consolidar testes unitários do mecanismo de escolhas.
- Cobrir carregamento e todas as regras de validação de JSON.
- Cobrir salvamento/restauração de progresso e preferências.
- Criar testes básicos de navegação Compose.
- Criar testes de acessibilidade/semântica para a tela narrativa e fluxos principais.
- Testar todos os caminhos do primeiro caso até finais positivos.
- Testar ausência de TTS e estados vazios/erros recuperáveis.
- Adicionar lint e verificações de formatação adequadas ao projeto.
- Eliminar TODOs essenciais, funções simuladas em produção e telas sem ação.

## Critérios de aceite

- Build debug, testes unitários, testes instrumentados definidos e lint passam.
- O teste de grafo garante inexistência de referências inválidas, cenas sem saída e finais inalcançáveis.
- O cenário “escolher → fechar app → reabrir → continuar” é verificado.
- Falhas apresentam mensagem respeitosa e caminho de recuperação.
- O app não pede internet, conta ou dado médico durante testes manuais.

## Verificação da etapa

- Registrar no README os comandos exatos executados e quaisquer requisitos de emulador.
