# Recursos visuais

Este documento registra a origem, a licença e as regras de desenho de todo
recurso visual do Indício. Ele existe porque a issue de identidade visual exige
que a procedência de cada recurso seja rastreável.

## Origem e licença

**Todo recurso visual do projeto é original, desenhado aqui, e é distribuído sob
a GPLv3, junto com o restante do código.**

Não há arte, ícone, fonte ou textura de terceiros no repositório — nem de banco
de imagens, nem gerada por serviço externo, nem sob licença separada. Não há
nada a atribuir a ninguém de fora.

> Se algum dia entrar um recurso de terceiro, ele precisa ser listado nesta
> seção com autor, origem e licença **antes** de ser usado. Um recurso sem essa
> linha é um recurso que não pode ser publicado.

Nenhuma arte contém marca, escudo, patrocinador ou símbolo de organização real.
Os nomes, as equipes e os campeonatos do produto são fictícios, e as
ilustrações seguem a mesma regra.

## Formato

As artes são **vector drawables**, não bitmaps. A escolha tem três motivos: o
conjunto inteiro ocupa 68 KB, funciona em qualquer densidade de tela sem
variantes, e é legível em texto — uma arte pode ser revisada em diff.

| Propriedade | Valor | Por quê |
|---|---|---|
| Proporção | 2:3, retrato | Proporção de carta de baralho |
| `viewport` | 400 × 600 | Grade de desenho; é o que define a arte |
| `width`/`height` | 132dp × 198dp | Tamanho intrínseco; ver abaixo |
| Prefixo | `cena_` | Exigido pelo `keep.xml`; ver abaixo |

O tamanho intrínseco fica **abaixo de 200dp de propósito**. O lint do Android
avisa (`VectorRaster`) que vetores maiores que 200 × 200 são caros de
rasterizar. Como a tela dimensiona a arte pelo modificador, e não pelo tamanho
intrínseco, reduzi-lo não custa qualidade nenhuma: o `viewport` continua
400 × 600 e a arte escala sem perda.

## Duas restrições que não são óbvias

### A moldura da carta não está no vetor

A borda dourada, o filete interno e o fundo de pergaminho são desenhados em
Compose (`ui/carta/Carta.kt`), não assados na arte.

O produto exige que a interface continue utilizável com o texto em "muito
grande". Um vetor de proporção fixa contendo a moldura cortaria o texto da cena
assim que ele crescesse, porque o vetor não estica só a parte de baixo. Com a
moldura em Compose, a carta acompanha a altura real do conteúdo.

O **verso** é a exceção: ele não contém texto nenhum, então é um único drawable
(`carta_verso.xml`), comum a todas as cartas.

### A arte precisa do `keep.xml`

`imagem.recurso` vem do JSON do caso e é resolvido com `getIdentifier`, em tempo
de execução. Não existe referência estática à arte em lugar nenhum do código.

O build de release usa `isShrinkResources = true`. Sem
`app/src/main/res/raw/keep.xml`, o encolhedor remove as artes do APK — o
aplicativo funciona em depuração e cai no texto de reserva em produção. Isso foi
verificado removendo o arquivo e reconstruindo o release: das 16 artes,
sobrevivia **uma**, justamente `carta_verso`, a única referenciada por
`R.drawable`.

O `keep.xml` preserva `@drawable/cena_*` por curinga, então arte nova com esse
prefixo já está coberta.

## Paleta

Os tokens vivem em `app/src/main/res/values/cores_ilustracao.xml`, separados da
paleta da interface (`ui/tema/Cores.kt`) porque quem os consome são os vetores.
Ambas derivam da mesma identidade de uma galeria contemporânea. A interface tem
esquemas claro e escuro em neutros frios, azul institucional e verde de
conservação, escolhidos conforme o modo do sistema; as ilustrações mantêm
iluminação própria nos dois modos. O dourado aparece somente onde há metal ou
um detalhe diretamente ligado à taça.

| Grupo | Tokens |
|---|---|
| Ambiente | `parede`, `parede_alta`, `piso`, `rodape`, `luz`, `sombra` |
| Madeira | `madeira`, `madeira_clara`, `feltro` |
| Identidade | `marinho`, `marinho_claro`, `dourado`, `dourado_claro` |
| Materiais | `vidro`, `papel`, `prata`, `prata_escura`, `tinta` |
| Figuras | `pele_clara`, `pele_media`, `pele_escura`, `cabelo`, `cabelo_grisalho`, `uniforme`, `macacao` |

Todos com o prefixo `ilustracao_`.

## Regras de desenho

- **Sem estética sombria, assustadora, infantil ou hospitalar.** O tom é o de
  uma investigação serena em uma exposição contemporânea: luz limpa, ambiente
  acolhedor, nada de ameaça.
- **Figuras sem traços de rosto.** As personagens são construídas com formas
  simples. Isso evita tanto o caricato quanto o inquietante, e mantém o desenho
  no registro adulto e sóbrio do produto.
- **As três personagens do primeiro caso são visualmente distintas** entre si em
  tom de pele, cabelo e roupa de trabalho.
- **A arte nunca carrega o caso sozinha.** Nenhuma pista indispensável pode
  depender só da imagem ou da cor; a informação está sempre no texto da cena, na
  pista ou nas escolhas. A `descricaoAcessivel` é obrigatória e é o que o leitor
  de tela anuncia.
- **Nada depende de rede.** Todo recurso é local.

## Inventário

### Verso e ícone

| Recurso | O que é |
|---|---|
| `carta_verso` | Verso comum das cartas: lupa sobre a pista iluminada, em azul, verde e dourado pontual |
| `ic_launcher_foreground` / `ic_launcher_background` | Ícone adaptativo, mesmo motivo da lupa |

### Cartas de "O Mistério da Taça Desaparecida"

| Arte | Cena | O que mostra |
|---|---|---|
| `cena_chegada` | `chegada` | Entrada do museu, sala iluminada, vitrine vazia |
| `cena_vitrine` | `vitrine` | Vitrine fechada sobre pedestal deslocado |
| `cena_ercilia` | `ercilia` | A curadora ao lado do painel da exposição |
| `cena_po` | `po` | Piso encerado com duas faixas de poeira deslocada |
| `cena_forro` | `forro` | Mancha de umidade no forro e a gota |
| `cena_chaves` | `chaves` | Quadro de chaves da portaria, com etiquetas |
| `cena_nivaldo` | `nivaldo` | O porteiro com o caderno de ocorrências |
| `cena_livro` | `livro` | A página de horários, última linha interrompida |
| `cena_zilda` | `zilda` | A manutenção no corredor, de luvas, com a escada |
| `cena_bilhete` | `atras-do-pedestal` | O bilhete caído junto ao rodapé |
| `cena_comparar` | `comparar` | Caderno, bilhete e folha de horários lado a lado |
| `cena_restauro` | `restauro` | A taça sobre a bancada forrada |
| `cena_final_seguranca` | `final-taca-em-seguranca` | As três pessoas à porta da sala de restauro |
| `cena_final_bilhete` | `final-bilhete` | Mãos alisando o bilhete, taça ao fundo |
| `cena_final_combinado` | `final-combinado` | O quadro de avisos novo, primeira anotação |

`cena_bilhete` é a única cujo nome não coincide com o da cena: ela ilustra
`atras-do-pedestal`.

### Cartas de "O Silêncio da Galeria Nove"

As artes deste caso foram desenhadas como vetores originais para o Indício. As
cenas que retornam ao mesmo local reutilizam a arte correspondente, sem fazer
qualquer pista depender somente da ilustração.

| Arte | Uso principal | O que mostra |
|---|---|---|
| `cena_galeria_nove_chegada` | Chegada | Galeria noturna e vitrine vazia sob luzes de presença |
| `cena_galeria_nove_vitrine` | Vitrine e lacre | Fechadura intacta, pedestal e fibra recolocada |
| `cena_galeria_nove_central` | Central e ronda | Monitores, cronômetro e registros técnicos |
| `cena_galeria_nove_corredor` | Rota interna | Painel móvel, corredor técnico e marcas de rodas |
| `cena_galeria_nove_laboratorio` | Conservação | Duas embalagens, etiquetas e bancada organizada |
| `cena_galeria_nove_planta` | Planta antiga | Mapas sobrepostos com uma porta omitida |
| `cena_galeria_nove_deposito` | Depósito e reserva | Caixas semelhantes, balança e acondicionamento |
| `cena_galeria_nove_ala` | Ala fechada | Painéis cobertos, fotografias e sensor de peso |
| `cena_galeria_nove_arquivo` | Arquivo e laudo | Pasta numerada, anexo ausente e documentos técnicos |
| `cena_galeria_nove_confronto` | Diretoria e mesa conjunta | Versões impressas e documentos reunidos |
| `cena_galeria_nove_gravacao` | Microgravação | Mecanismo aberto com inscrição sob a lente |
| `cena_galeria_nove_final_transparencia` | Final da transparência | Galeria aberta sobre o processo de pesquisa |
| `cena_galeria_nove_final_restituicao` | Final da restituição | Peça protegida e dossiê de procedência |

### Cartas de "O Sumiço da Múmia"

Este caso evita representar restos humanos. As cartas mostram somente
documentos, materiais de arquivo, paisagem, instrumentos e áreas protegidas.
Todas foram desenhadas como vetores originais para o Indício.

| Arte | Uso principal | O que mostra |
|---|---|---|
| `cena_mumia_arquivo` | Chegada ao arquivo | Arquivo claro da capital fictícia, caixas e fichas sobre a mesa |
| `cena_mumia_caixa` | Caixa e selo | Divisórias de placas fotográficas numa caixa histórica |
| `cena_mumia_inventario` | Fichas e suplemento | Registros com números repetidos e livro suplementar |
| `cena_mumia_negativos` | Acervo fotográfico | Negativos sobre mesa de luz, um deles invertido |
| `cena_mumia_caderno` | Caderno e cartas | Duas tintas, horários e correspondência de cautela |
| `cena_mumia_horizonte` | Fotografias e superfície | Horizonte desértico fictício, carrinho e área protegida |
| `cena_mumia_mapa` | Rotas e cronologia | Duas metades de mapa com coordenadas cobertas |
| `cena_mumia_etiqueta` | Identificação provisória | Etiqueta a lápis com ponto de interrogação |
| `cena_mumia_amuleto` | Revisão do contexto | Bandeja de triagem com objetos separados |
| `cena_mumia_laudo` | Exame de 1938 | Laudo com identidade mantida em aberto |
| `cena_mumia_medidas` | Câmara documentada | Planta, cotas e orientação do espaço protegido |
| `cena_mumia_mesa` | Conselho e proteção | Documentos e limites de pesquisa sobre a mesa |
| `cena_mumia_final_historia` | Final documental | Exposição de arquivo sem restos humanos ou coordenadas |
| `cena_mumia_final_pesquisa` | Final não invasivo | Leitura de superfície limitada à proteção do terreno |

## Verificação

`ArteDasCartasTest`, em `./gradlew test`, falha se uma cena publicada apontar
para arte inexistente, se o verso sumir, ou se existir arte que nenhuma cena
usa. É a rede de proteção contra o erro que o compilador não pega, já que os
nomes vêm do JSON.
