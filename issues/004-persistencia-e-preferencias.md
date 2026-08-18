# #004 — Persistir progresso, histórico e preferências

## Objetivo

Salvar automaticamente a experiência e restaurá-la de forma confiável após fechar o aplicativo.

## Escopo

- Criar entidades/DAOs Room para progresso por caso e histórico de conclusões.
- Registrar cena atual, escolhas/caminho, pistas, data de atualização e final alcançado.
- Salvar depois de cada escolha antes de aceitar uma nova interação.
- Restaurar “Continuar” na última sessão válida e lidar com caso removido ou versão incompatível.
- Permitir reiniciar um caso sem apagar o histórico de conclusões.
- Usar DataStore para tamanho do texto (`grande`, `muito_grande`) e redução de movimentos.
- Expor repositórios por interfaces e fornecer doubles/fakes em testes.

## Critérios de aceite

- Fechar e reabrir o app retorna à cena e às pistas corretas.
- O botão “Continuar” só fica habilitado quando existe progresso válido.
- Preferências são aplicadas novamente após reinício.
- Falhas de armazenamento são tratadas com mensagem simples e sem perda silenciosa do estado em memória.
- Testes cobrem salvamento e restauração com Room em memória e repositórios falsos.

## Verificação da etapa

- Executar testes de persistência e compilação debug.

