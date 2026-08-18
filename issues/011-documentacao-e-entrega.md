# #011 — Documentar, revisar e empacotar o MVP

## Objetivo

Entregar um projeto reproduzível e preparar a expansão segura do catálogo.

## Escopo

- Escrever README com visão do produto, requisitos, configuração, compilação, testes e execução.
- Documentar arquitetura MVVM, fluxo unidirecional, DI, armazenamento e localização dos assets.
- Criar documento curto “Como criar novos casos” com esquema/campos, exemplo mínimo e regras do validador.
- Explicar categorias futuras e como adicionar catálogo, imagens, descrições e narração por TTS.
- Documentar privacidade/offline, ausência de permissões sensíveis e posicionamento não médico.
- Registrar limitações reais do MVP sem esconder comportamento não implementado.
- Realizar revisão final editorial e funcional do primeiro caso.
- Gerar APK debug e registrar ambiente/versões usados na validação.

## Critérios de aceite

- Uma pessoa nova consegue compilar, testar, executar e adicionar um caso seguindo os documentos.
- Todos os comandos documentados foram executados com sucesso em ambiente limpo compatível.
- O APK funciona offline em API 26 e em uma API atual definida para teste.
- A lista final de funcionalidades e limitações corresponde ao comportamento real.
- O aviso médico consta exatamente uma vez no produto, apenas em Sobre.

## Verificação da etapa

- Fazer a rodada final completa de build, testes, lint, instalação e percurso manual dos finais.

