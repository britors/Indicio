# Telas do Indício

Maquetes das telas, em 360 × 800, para validar a direção visual antes de mexer
no código. Esta pasta guarda somente as imagens finais e este índice.

> **Nomes provisórios:** títulos, personagens, entidades e eventos mostrados
> nas maquetes servem apenas para testar conteúdo e leiaute. Nenhum deles está
> liberado como marca ou nome definitivo. Consulte a
> [revisão jurídica de conteúdo](../revisao-juridica-de-conteudo.md).

Não são capturas do aplicativo: são desenhos. Mas as cores, os tipos e as
medidas foram lidos do código, não de memória — creme `#FBF6EC`, sépia
`#F1E7D6`, marinho `#1B2A41`, dourado `#8A6A1F`, serifada nos títulos, corpo
sem serifa em 20/30, botões com 64 dp de altura mínima. A arte da vitrine e o
verso da carta usam os mesmos caminhos vetoriais dos drawables.

A direção foi implementada no Compose pela issue estrutural #018. As imagens
continuam sendo a referência visual; diferenças de conteúdo e métrica de fonte
podem alterar a altura dos cartões sem mudar sua estrutura.

## Arquivos

| Arquivo | Tela |
|---|---|
| [`main.png`](main.png) | História — a carta da cena |
| [`main-muito-grande.png`](main-muito-grande.png) | A mesma tela com o texto em "muito grande" |
| [`cartavirando.png`](cartavirando.png) | História — a carta chegando virada para baixo |
| [`inicio.png`](inicio.png) | Início |
| [`catalogo.png`](catalogo.png) | Catálogo |
| [`conclusao.png`](conclusao.png) | Conclusão |
| [`pausa.png`](pausa.png) | Pausa |
| [`configuracoes.png`](configuracoes.png) | Configurações |
| [`sobre.png`](sobre.png) | Sobre |
| [`retomada.png`](retomada.png) | Retomar uma investigação depois de um intervalo |
| [`investigacao.png`](investigacao.png) | Etapas narrativas reveladas e objetivo atual |
| [`caderno.png`](caderno.png) | Caderno aberto na seção de pistas |
| [`pessoas.png`](pessoas.png) | Caderno aberto na seção de personagens |

## Ressalvas

- As telas mostram o que cabe antes de rolar. Na História, a segunda carta de
  escolha aparece cortada no pé — é assim no aparelho também, e o resto vem com
  a rolagem.
- O título na barra do topo quebra em duas linhas aqui e cabe em uma no
  aparelho: diferença de métrica entre a fonte serifada do sistema Android e a
  do navegador que gerou o PNG.
- Na carta virando, as escolhas aparecem esmaecidas apenas para indicar que
  ficam inertes durante a virada; no aplicativo elas seguem visíveis.
