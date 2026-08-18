# #009 — Validar acessibilidade e conforto cognitivo

## Objetivo

Garantir uso confortável por idosos e pessoas com dificuldades cognitivas leves, sem alegações clínicas.

## Escopo

- Aplicar tamanho narrativo “grande” e “muito grande” em tempo real.
- Garantir altura mínima de 64 dp para botões de ação e escolha.
- Definir semântica/labels para imagens, controles, títulos, pistas e mudança de cena.
- Ordenar foco e anúncios para TalkBack sem repetição excessiva.
- Não comunicar estado ou informação importante somente por cor.
- Garantir áreas de toque amplas, estados de foco claros e espaçamento generoso.
- Limitar densidade de conteúdo e preservar duas escolhas visualmente distintas.
- Respeitar redução de movimentos e evitar flashes/estímulos piscantes.
- Revisar linguagem para evitar infantilização, tom hospitalar e alegações de tratar, prevenir ou retardar demência.
- Verificar orientação/tamanhos de tela definidos como suportados pelo MVP.

## Critérios de aceite

- Fluxo completo é operável com TalkBack e somente por botões.
- Testes Compose verificam labels, papéis, conteúdo descritivo e alvos mínimos relevantes.
- Texto muito grande não fica truncado nem cobre escolhas; rolagem acessível é fornecida quando necessária.
- Revisão de contraste atende WCAG AA como referência prática.
- Não há cronômetro, pressão, punição, anúncios ou notificações insistentes.

## Verificação da etapa

- Executar testes de acessibilidade e roteiro manual com TalkBack e fontes nas duas opções.

