# Regras de engenharia do Indício

Estas regras valem para todo o repositório.

- Nunca implemente uma solução que viole os princípios SOLID.
- Antes de alterar código, confira responsabilidade única, extensão sem editar
  políticas estáveis, substituição integral dos contratos, interfaces coesas e
  dependências apontando para as abstrações internas.
- Se uma solução parecer exigir uma violação, pare e redesenhe-a. Uma decisão
  arquitetural pode registrar o raciocínio, mas não autoriza quebrar SOLID.
- SOLID não justifica interfaces, classes ou camadas cerimoniais: toda abstração
  deve representar uma política, uma fronteira ou uma variação real.
- Preserve também as regras detalhadas em `docs/arquitetura.md` e
  `CONTRIBUTING.md`.
- Sempre que uma mudança produzir algo visual ou interativo que possa ser
  executado no emulador disponível, compile, instale e deixe o aplicativo
  aberto para o responsável pelo projeto acompanhar como está ficando.
