# #007 — Implementar a experiência jogável e a narração

**Trilha:** Estrutura
**Estado:** Concluída para o fluxo do MVP

## Objetivo

Transformar qualquer caso entregue pelo motor em uma experiência completa de leitura, decisão e escuta.

## Escopo

- Exibir uma ilustração/cenário, trecho curto, botão de narração e duas escolhas grandes.
- Ligar os botões ao mecanismo narrativo e ao salvamento automático.
- Exibir pistas descobertas de modo discreto e também textual.
- Implementar TextToSpeech Android com tentativa de usar `pt-BR`.
- Permitir iniciar/parar/repetir narração e encerrar corretamente o TTS no ciclo de vida.
- Continuar funcionando normalmente quando não houver engine/voz/idioma instalado, com aviso não bloqueante.
- Evitar múltiplas escolhas em toques rápidos enquanto transição/salvamento ocorre.
- Usar animações breves; respeitar a preferência de reduzir movimentos.
- Se gesto de arrastar for adicionado, mantê-lo opcional e equivalente aos botões.

## Critérios de aceite

- O caso pode ser concluído usando exclusivamente toques nos botões.
- Cada escolha atualiza a cena e salva o progresso.
- O controle de narração anuncia estado e ação para tecnologia assistiva.
- A ausência de voz não impede leitura, escolha ou conclusão.
- Pausar/sair interrompe a fala sem vazamento de recursos.

## Verificação da etapa

- Executar testes do ViewModel/UI e teste manual com TTS disponível e indisponível.
