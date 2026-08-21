# Como criar novos casos

Um caso do Indício é um arquivo JSON. Não é preciso escrever nem alterar código
Kotlin para acrescentar uma história: o mecanismo narrativo apenas percorre o
grafo declarado no arquivo.

Este documento descreve o formato `1`, atualmente executado pelo aplicativo, as
regras que o validador exige e o caminho completo para publicar um caso novo.
O contrato `2`, destinado a investigações longas com etapas, objetivos,
retomada e caderno, está implementado e documentado em
[Esquema narrativo v2](esquema-narrativo-v2.md). Este guia detalha o formato `1`;
para um caso longo, a especificação v2 é a fonte normativa e exige duas
escolhas distintas por cena comum.

## Onde os arquivos ficam

```
app/src/main/assets/casos/
├── catalogo.json              índice de todos os casos
├── taca-desaparecida.json     primeiro caso
└── <id-do-caso>.json          um arquivo por caso novo
```

O catálogo é lido de `casos/catalogo.json`; esse caminho é fixo. Os arquivos de
caso ficam ao lado dele e são referenciados pelo campo `arquivo` do catálogo.

O `id` é um contrato permanente: ele identifica a rota, o progresso e o
histórico no banco. Depois que um caso for publicado, não renomeie seu `id`, os
ids de cenas ou os ids de escolhas sem planejar uma migração. A arquitetura e
o fluxo completo estão em [Arquitetura do Indício](arquitetura.md).

## Passo a passo

1. Escreva o arquivo do caso em `app/src/main/assets/casos/<id-do-caso>.json`.
2. Acrescente uma entrada no `catalogo.json` apontando para ele.
3. Rode `./gradlew test`. O validador aponta erro por erro, com caso, cena e
   campo.
4. Peça as ilustrações das cenas (veja [Imagens](#imagens)) antes de marcar o
   caso como `disponivel`.
5. Conclua e registre a
   [revisão jurídica de nomes e conteúdo](revisao-juridica-de-conteudo.md).

Enquanto o caso estiver incompleto, deixe `"disponivel": false` e **omita** o
campo `arquivo`. O catálogo então anuncia a história como futura, sem oferecê-la
para jogar.

## O catálogo

```json
{
  "versaoEsquema": 1,
  "casos": [
    {
      "id": "taca-desaparecida",
      "titulo": "O Mistério da Taça Desaparecida",
      "sinopse": "Poucas horas antes de uma exposição, uma famosa taça desaparece.",
      "categoria": "futebol",
      "arquivo": "casos/taca-desaparecida.json",
      "disponivel": true
    }
  ]
}
```

| Campo | Obrigatório | Descrição |
|---|---|---|
| `versaoEsquema` | sim | Sempre `1` nesta versão do aplicativo. |
| `id` | sim | Identificador único, em minúsculas com hífens. |
| `titulo` | sim | Nome exibido no catálogo. |
| `sinopse` | sim | Uma ou duas frases, sem revelar a solução. |
| `categoria` | sim | Uma das cinco chaves da tabela abaixo. |
| `arquivo` | só se disponível | Caminho do arquivo do caso, a partir de `assets/`. |
| `disponivel` | não (padrão `false`) | `true` libera o caso para jogar. |

### Categorias

| Chave no JSON | Exibido como |
|---|---|
| `futebol` | Futebol |
| `misterios_policiais` | Mistérios policiais |
| `faroeste` | Faroeste |
| `romances_classicos` | Romances clássicos |
| `cultura_popular_antiga` | Desenhos e cultura popular antigos |

As cinco categorias já existem no código. Para acrescentar uma sexta, é
necessário editar o enum `Categoria` — é a única mudança de código prevista para
conteúdo, e vale abrir uma issue antes.

## O arquivo do caso

```json
{
  "versaoEsquema": 1,
  "id": "taca-desaparecida",
  "titulo": "O Mistério da Taça Desaparecida",
  "sinopse": "Poucas horas antes de uma exposição, uma famosa taça desaparece.",
  "categoria": "futebol",
  "cenaInicial": "chegada",
  "cenas": [ ... ]
}
```

`id` e `categoria` precisam ser **idênticos** aos do catálogo; divergência é erro
de validação. `cenaInicial` precisa ser o `id` de uma cena existente.

### Cena comum

Toda cena que não é final oferece **exatamente duas** escolhas.

```json
{
  "id": "vitrine",
  "tipo": "comum",
  "texto": "A vitrine está intacta. O pedestal, porém, não está centralizado.",
  "imagem": {
    "recurso": "cena_vitrine",
    "descricaoAcessivel": "Vitrine de vidro fechada sobre um pedestal deslocado."
  },
  "narracao": "A vitrine está intacta. O pedestal, porém, não está centralizado.",
  "pista": {
    "id": "pedestal-deslocado",
    "titulo": "O pedestal fora do lugar",
    "descricao": "O pedestal está alguns centímetros à esquerda da marca no piso."
  },
  "escolhas": [
    { "id": "vitrine-a", "texto": "Examinar o chão", "proximaCena": "po" },
    { "id": "vitrine-b", "texto": "Olhar o forro", "proximaCena": "forro" }
  ]
}
```

| Campo | Obrigatório | Descrição |
|---|---|---|
| `id` | sim | Único dentro do caso. |
| `tipo` | não (padrão `comum`) | `comum` ou `final`. |
| `texto` | sim | O trecho narrativo exibido. Curto: três a cinco frases. |
| `imagem` | sim | Ver [Imagens](#imagens). |
| `narracao` | não | Texto lido em voz alta. Omitido, narra-se o `texto`. |
| `pista` | não | Pista descoberta ao **chegar** nesta cena. |
| `escolhas` | sim em cenas comuns | Exatamente duas. |
| `desfecho` | proibido em cenas comuns | Só em cenas finais. |

### Escolha

| Campo | Obrigatório | Descrição |
|---|---|---|
| `id` | sim | Único dentro da cena. |
| `texto` | sim | O rótulo do botão. Curto e concreto. |
| `proximaCena` | sim | `id` de uma cena existente. |
| `pista` | não | Pista revelada **por fazer** esta escolha. |

### Cena final

Cenas finais não têm escolhas e precisam de `desfecho`.

```json
{
  "id": "final-taca-em-seguranca",
  "tipo": "final",
  "texto": "A taça volta para a vitrine limpa e seca. A exposição abre no horário.",
  "imagem": {
    "recurso": "cena_final_seguranca",
    "descricaoAcessivel": "Três pessoas reunidas olhando para a taça de prata."
  },
  "desfecho": {
    "titulo": "A taça em segurança",
    "mensagem": "A taça nunca saiu do museu; ela foi protegida de uma goteira.",
    "explicacaoPistas": "A vitrine intacta e o pedestal deslocado mostravam que ninguém forçou nada. A mancha no forro explicava o motivo, e o registro das seis e dez dava a hora."
  }
}
```

`explicacaoPistas` é o fechamento gentil do caso: retoma o raciocínio, sem
cobrar do jogador o que ele deixou de notar.

### Pistas

Uma pista tem `id`, `titulo` e `descricao`, todos obrigatórios.

A **mesma pista pode aparecer em vários pontos** do arquivo — é assim que
caminhos diferentes levam à mesma descoberta. Nesse caso, o `id` se repete e o
conteúdo precisa ser **idêntico**, palavra por palavra. Dois textos diferentes
sob o mesmo `id` são erro de validação: o jogador veria duas versões da mesma
descoberta.

O mecanismo acumula pistas sem repetição e as preserva até a conclusão.

## Imagens

Cada cena é uma **carta**, e `imagem.recurso` é a arte dessa carta: o nome de um
drawable do Android, sem extensão e sem prefixo (`cena_vitrine` corresponde a
`res/drawable/cena_vitrine.xml`).

O nome só é resolvido em tempo de execução, então o compilador não acusa um erro
de digitação. Quem acusa é o teste `ArteDasCartasTest`, que roda em
`./gradlew test` e falha se alguma cena publicada apontar para uma arte que não
existe — ou se sobrar arte que nenhuma cena usa.

> **Use sempre `cena_<caso>_<cena>` em casos novos**, convertendo hífens em
> sublinhados. Por exemplo, a cena `arquivo` do caso `cartas-perdidas` usa
> `cena_cartas_perdidas_arquivo`. O primeiro caso conserva nomes mais curtos por
> compatibilidade. O prefixo `cena_` é obrigatório: o arquivo
> `app/src/main/res/raw/keep.xml` preserva `@drawable/cena_*` do encolhedor de
> recursos; uma arte fora desse padrão não tem nenhuma referência estática, é
> removida do APK de release e o caso cairia no texto de reserva justamente na
> versão publicada — funcionando em depuração o tempo todo.

O formato, a paleta e as regras de desenho estão em
[Recursos visuais](recursos-visuais.md).

`imagem.descricaoAcessivel` é obrigatória e é lida por leitores de tela. Descreva
o que se vê, não o que significa: "vitrine de vidro sobre um pedestal deslocado",
não "a cena do crime".

> **Nenhuma pista indispensável pode depender apenas da imagem ou da cor.** Toda
> informação necessária para resolver o caso precisa estar no `texto`, na
> `pista` ou no texto das escolhas. A ilustração acompanha; ela não carrega o
> caso sozinha.

## Narração

O campo `narracao` alimenta o TextToSpeech. Use-o quando o texto escrito não for
lido bem em voz alta — principalmente diálogos com travessão:

```json
"texto": "— Tranquei esta sala às sete — diz dona Ercília.",
"narracao": "Dona Ercília diz: Tranquei esta sala às sete."
```

Omitido, o aplicativo narra o próprio `texto`. O caso precisa funcionar por
completo mesmo sem nenhuma voz instalada no aparelho.

## Regras do validador

O carregamento é **estrito**: um campo desconhecido derruba a leitura com erro,
em vez de ser ignorado em silêncio. Um `"sinopsee"` digitado errado aparece como
problema, e não como uma sinopse que some sem explicação.

### Estrutura do caso

- `id`, `titulo` e `sinopse` não podem estar em branco.
- O caso precisa ter ao menos uma cena.
- Identificadores de cena não podem se repetir.
- `cenaInicial` precisa existir.
- Toda cena precisa de `texto`, `imagem.recurso` e `imagem.descricaoAcessivel`.
- Toda pista precisa de `id`, `titulo` e `descricao`.
- Toda cena precisa ser **alcançável** a partir da cena inicial.
- A mesma pista não pode aparecer com conteúdos diferentes.

### Cenas comuns

- Exatamente duas escolhas. Nenhuma, ou uma, ou três é erro.
- Identificadores de escolha não podem se repetir dentro da cena.
- Toda escolha precisa de `id`, `texto` e `proximaCena` existente.
- Não podem ter `desfecho`.

### Cenas finais

- Não podem ter escolhas.
- Precisam de `desfecho` com `titulo`, `mensagem` e `explicacaoPistas`.

### Catálogo

- `versaoEsquema` precisa ser `1`.
- Identificadores de caso não podem se repetir.
- Caso marcado como `disponivel` precisa apontar para um `arquivo`.
- `id` e `categoria` do arquivo precisam bater com os do catálogo.

O validador **junta todos os problemas** antes de responder, para que um arquivo
com vários erros seja corrigido de uma vez. Cada mensagem localiza o defeito:

```
caso "taca-desaparecida", cena "abertura", campo "escolhas[0].proximaCena": a cena "fantasma" não existe
```

## Regras editoriais verificadas por teste

Além da estrutura, `ConteudoPublicadoTest` protege as regras do produto e roda
em `./gradlew test`, sem emulador:

- ao menos 12 cenas por caso, sem teto artificial de duração;
- entre dois e três finais, todos alcançáveis;
- nenhuma escolha voltando para a própria cena;
- ao menos três pistas distintas;
- descrições acessíveis com pelo menos 30 caracteres;
- ausência de termos de violência, punição, linguagem de fracasso, alegações
  médicas e marcas reais.

Esse último teste compara **palavras inteiras**. Se um termo legítimo for
acusado, corrija a lista em vez de reescrever o texto para contorná-la.

## Regras editoriais que nenhum teste verifica

Estas dependem de leitura humana e são tão obrigatórias quanto as demais:

- Português do Brasil simples, adulto e respeitoso. Sem infantilização e sem
  tom hospitalar.
- Sem violência, sustos, angústia, morte, fracasso ou "game over".
- Todos os finais são positivos. Ninguém é humilhado, nem mesmo o responsável
  pelo mistério.
- Escolhas menos adequadas **continuam a história** — de preferência revelando
  uma pista — em vez de punir.
- Times, campeonatos, marcas, pessoas e símbolos são inteiramente fictícios.
- Todo nome começa como provisório. Um caso não pode ser publicado antes da
  busca e da aprovação descritas na
  [revisão jurídica de conteúdo](revisao-juridica-de-conteudo.md).
- Sem alegações de tratar, prevenir ou retardar qualquer condição de saúde. O
  aviso médico do produto vive apenas na tela Sobre.

## Desenhando o grafo

Um formato que funciona bem: a cena inicial ramifica em dois caminhos de
investigação, que se reencontram em cenas comuns, e a convergência final leva a
dois ou três desfechos distintos.

Antes de publicar, vale percorrer o grafo inteiro. Este script lista todos os
caminhos possíveis e confirma que nenhum fica sem saída:

```bash
python3 - <<'PY'
import json
d = json.load(open('app/src/main/assets/casos/SEU-CASO.json'))
cenas = {c['id']: c for c in d['cenas']}
finais = {i for i, c in cenas.items() if c.get('tipo') == 'final'}
caminhos, presos = [], []

def anda(atual, passos):
    if atual in finais:
        caminhos.append((passos, atual)); return
    if len(passos) > 40:
        presos.append(passos); return
    for e in cenas[atual]['escolhas']:
        anda(e['proximaCena'], passos + [e['id']])

anda(d['cenaInicial'], [])
print('caminhos até um final:', len(caminhos))
print('caminhos sem saída:', len(presos))
print('finais alcançados:', sorted({f for _, f in caminhos}))
print('finais declarados:', sorted(finais))
PY
```

## Versão do esquema

`versaoEsquema` vale `1` e precisa bater no catálogo e em cada caso. Se o formato
mudar, o aplicativo recusa arquivos de versão diferente com uma mensagem clara,
em vez de tentar interpretá-los pela metade.

Não acrescente campos do formato `2` a um arquivo `1`: a leitura é estrita e o
arquivo será recusado. Os fixtures de `docs/exemplos/` são apenas entradas de
teste e nunca devem ser marcados como conteúdo disponível.

No formato `2`, catálogo e casos passam a ter versões independentes:

- `versaoCatalogo` identifica apenas o índice;
- `versaoEsquema` identifica a forma de cada caso;
- `versaoConteudo` identifica revisões do mesmo caso que possam afetar o
  progresso salvo.

Consulte a [especificação normativa do formato 2](esquema-narrativo-v2.md) e os
[fixtures técnicos](exemplos/esquema-v2/) antes de projetar um caso longo.
