# Telas do Indício

Maquetes das oito telas, em 360 × 800, para validar a direção visual antes de
mexer no código.

Não são capturas do aplicativo: são desenhos. Mas as cores, os tipos e as
medidas foram lidos do código, não de memória — creme `#FBF6EC`, sépia
`#F1E7D6`, marinho `#1B2A41`, dourado `#8A6A1F`, serifada nos títulos, corpo
sem serifa em 20/30, botões com 64 dp de altura mínima. A arte da vitrine e o
verso da carta usam os mesmos caminhos vetoriais dos drawables.

## Arquivos

| Arquivo | Tela |
|---|---|
| `main` | História — a carta da cena |
| `main-muito-grande` | A mesma tela com o texto em "muito grande" |
| `cartavirando` | História — a carta chegando virada para baixo |
| `inicio` | Início |
| `catalogo` | Catálogo |
| `conclusao` | Conclusão |
| `pausa` | Pausa |
| `configuracoes` | Configurações |
| `sobre` | Sobre |

Cada tela tem um `.html` (abre em qualquer navegador) e um `.png`.

`fonte/` guarda os artboards `.dc.html` e o `canvas.json` do canvas de design,
caso seja preciso montá-lo de novo.

## Refazer os PNGs

```bash
cd docs/telas
for f in *.html; do
  firefox --headless --window-size 360,800 --screenshot "$PWD/${f%.html}.png" "file://$PWD/$f"
done
```

## Ressalvas

- As telas mostram o que cabe antes de rolar. Na História, a segunda carta de
  escolha aparece cortada no pé — é assim no aparelho também, e o resto vem com
  a rolagem.
- O título na barra do topo quebra em duas linhas aqui e cabe em uma no
  aparelho: diferença de métrica entre a fonte serifada do sistema Android e a
  do navegador que gerou o PNG.
- Na carta virando, as escolhas aparecem esmaecidas apenas para indicar que
  ficam inertes durante a virada; no aplicativo elas seguem visíveis.
