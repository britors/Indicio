# #003 — Implementar o mecanismo de escolhas e pistas

**Trilha:** Estrutura
**Estado:** Concluída para o esquema `1`

## Objetivo

Criar o núcleo de domínio que conduz a história a partir do JSON e produz estado observável para a interface.

## Escopo

- Representar sessão atual, cena, caminho percorrido, pistas descobertas e final alcançado.
- Iniciar/reiniciar um caso e aplicar uma das duas escolhas válidas.
- Resolver próxima cena, caminhos alternativos que se reencontram e finais positivos.
- Permitir que escolhas menos adequadas revelem pista e continuem a história.
- Impedir transições inválidas e escolhas repetidas por toque duplo.
- Expor estado e eventos por ViewModel/StateFlow com fluxo unidirecional.
- Manter o domínio independente de Compose, Android e persistência concreta.

## Critérios de aceite

- Toda escolha leva a uma cena válida ou a um final definido pelo JSON.
- Pistas são acumuladas sem duplicação e permanecem disponíveis na conclusão.
- Não há estado de derrota, vida, cronômetro ou pontuação negativa.
- O estado pode ser reconstruído deterministicamente a partir do caso e do progresso salvo.
- Testes cobrem ramificação, reencontro, pista, final e entrada inválida.

## Verificação da etapa

- Executar testes unitários do motor e compilar o app.
