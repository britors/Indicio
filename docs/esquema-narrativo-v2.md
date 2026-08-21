# Esquema narrativo v2

**Estado:** contrato implementado pela issue estrutural #013
**Escopo:** casos longos, totalmente offline, retomáveis em várias sessões

Este documento é a fonte normativa do formato `2`. Ele define o contrato JSON,
as regras derivadas pelo domínio, o mínimo persistido e a compatibilidade com o
formato `1`. Os exemplos completos ficam em [`exemplos/esquema-v2/`](exemplos/esquema-v2/).

## Objetivos do formato

O formato `2` acrescenta orientação para histórias longas sem transformar o
motor em código específico de um enredo. Ele precisa responder, em qualquer
ponto da investigação:

- em qual etapa o jogador está;
- o que ele está tentando descobrir agora;
- o que já foi revelado, sem antecipar conteúdo futuro;
- quais pessoas, locais e conversas já conhece;
- como retomar depois de um intervalo;
- se o progresso salvo ainda é compatível com a revisão instalada do caso.

O grafo de cenas e a sequência de escolhas continuam sendo a fonte da verdade.
Etapa, objetivo e caderno são projeções determinísticas desse percurso.

## Separação entre catálogo e caso

O catálogo passa a ter sua própria versão, independente do formato de cada
caso. Isso permite que um mesmo aplicativo ofereça simultaneamente casos `1` e
`2`.

```json
{
  "versaoCatalogo": 2,
  "casos": [
    {
      "id": "catalogo-fora-de-ordem",
      "titulo": "O Catálogo Fora de Ordem",
      "sinopse": "Cartões de uma pequena coleção foram reorganizados durante a noite.",
      "categoria": "misterios_policiais",
      "arquivo": "casos/catalogo-fora-de-ordem.json",
      "disponivel": true,
      "versaoEsquema": 2,
      "versaoConteudo": 1
    }
  ]
}
```

| Campo | Obrigatório | Regra |
|---|---|---|
| `versaoCatalogo` | sim | Inteiro `2`. Não é a versão dos casos. |
| `casos` | sim | Lista; ids não se repetem. |
| `id` | sim | Id estável do caso. |
| `titulo` | sim | Título sem revelar a solução. |
| `sinopse` | sim | Resumo curto para o catálogo. |
| `categoria` | sim | Chave do vocabulário controlado existente. |
| `arquivo` | se disponível | Caminho local a partir de `assets/`. |
| `disponivel` | não | Padrão `false`. |
| `versaoEsquema` | se disponível | `1` ou `2`, conforme o arquivo apontado. |
| `versaoConteudo` | se disponível | Inteiro positivo, iniciado em `1`. |

Para uma entrada disponível, as duas versões declaradas precisam coincidir com
o arquivo carregado. Entrada indisponível omite `arquivo`, `versaoEsquema` e
`versaoConteudo`.

## Estrutura do caso

```json
{
  "versaoEsquema": 2,
  "versaoConteudo": 1,
  "id": "catalogo-fora-de-ordem",
  "titulo": "O Catálogo Fora de Ordem",
  "sinopse": "...",
  "categoria": "misterios_policiais",
  "cenaInicial": "chegada",
  "etapas": [],
  "caderno": {},
  "lembrancas": [],
  "cenas": []
}
```

| Campo | Obrigatório | Regra |
|---|---|---|
| `versaoEsquema` | sim | Inteiro `2`. |
| `versaoConteudo` | sim | Inteiro positivo e crescente por caso publicado. |
| `id` | sim | Id estável e igual ao catálogo. |
| `titulo` | sim | Igual ao catálogo. |
| `sinopse` | sim | Igual ao catálogo. |
| `categoria` | sim | Igual ao catálogo. |
| `cenaInicial` | sim | Referência para uma cena da primeira etapa. |
| `etapas` | sim | Lista ordenada e não vazia. |
| `caderno` | sim | Definições reveláveis do caso. |
| `lembrancas` | sim | Lembretes curtos revelados pelo percurso. Pode ser vazia. |
| `cenas` | sim | Grafo completo do caso. |

Campos desconhecidos são erro. Não existem valores `null`: campos opcionais
são omitidos e listas opcionais usam o padrão vazio definido neste documento.

## Etapas e objetivos

```json
{
  "id": "comparar-registros",
  "titulo": "Comparar os registros",
  "descricao": "Duas sequências parecem plausíveis, mas só uma explica todas as etiquetas.",
  "resumoConclusao": "Os registros apontaram onde a ordem começou a mudar.",
  "resumoRetomada": "Você começou a comparar etiquetas, horários e anotações.",
  "objetivos": [
    {
      "id": "encontrar-primeira-troca",
      "texto": "Encontrar o primeiro cartão que saiu do lugar",
      "perguntaEmAberto": "Onde a sequência começou a mudar?"
    }
  ]
}
```

### Etapa

| Campo | Obrigatório | Uso |
|---|---|---|
| `id` | sim | Referência estável, única no caso. |
| `titulo` | sim | Exibido somente depois que a etapa for alcançada. |
| `descricao` | sim | Contexto curto da etapa atual. |
| `resumoConclusao` | sim | Texto seguro para uma etapa concluída em qualquer caminho. |
| `resumoRetomada` | sim | “Onde você parou” quando esta é a etapa atual. |
| `objetivos` | sim | Um ou mais objetivos possíveis nesta etapa. |

A ordem do array é a ordem narrativa. Etapas futuras aparecem com rótulo
genérico — por exemplo, “Novas perguntas” — e não expõem `titulo`, `descricao`,
`resumoConclusao`, `resumoRetomada` ou objetivos.

### Objetivo

| Campo | Obrigatório | Uso |
|---|---|---|
| `id` | sim | Único em todo o caso. |
| `texto` | sim | Próxima ação mostrada na Retomada e em Etapas. |
| `perguntaEmAberto` | sim | Pergunta destacada no Caderno. |

Cada cena comum aponta para um objetivo da própria etapa. Assim o objetivo
atual é o da cena reconstruída, sem coluna adicional no banco.

## Caderno

O caderno declara conteúdo, mas não o libera. Um item só aparece depois de seu
id ser alcançado por `revelacoes` de uma cena ou escolha.

O objeto `caderno` sempre contém as quatro listas `pistas`, `pessoas`, `locais`
e `conversas`. Elas são obrigatórias, podem estar vazias e não aceitam `null`.

```json
{
  "pistas": [
    {
      "id": "etiqueta-azul",
      "titulo": "Uma etiqueta azul",
      "descricao": "A etiqueta azul aparece entre duas séries que deveriam estar separadas.",
      "relevancia": "A cor permite identificar onde as duas sequências começaram a se misturar."
    }
  ],
  "pessoas": [
    {
      "id": "bibliotecaria",
      "nome": "A bibliotecária",
      "papel": "Organização da coleção",
      "imagem": {
        "recurso": "pessoa_catalogo_bibliotecaria",
        "descricaoAcessivel": "Bibliotecária diante de um armário de fichas."
      },
      "anotacoes": [
        {
          "id": "bibliotecaria-separou-series",
          "texto": "Ela separou as duas séries antes de encerrar o expediente."
        }
      ]
    }
  ],
  "locais": [
    {
      "id": "sala-de-consulta",
      "nome": "Sala de consulta",
      "imagem": {
        "recurso": "local_catalogo_sala_consulta",
        "descricaoAcessivel": "Sala clara com mesas e um armário baixo de fichas."
      },
      "anotacoes": [
        {
          "id": "sala-armario-compartilhado",
          "texto": "O armário é usado pela manhã para consulta e à tarde para oficinas."
        }
      ]
    }
  ],
  "conversas": [
    {
      "id": "conversa-bibliotecaria-etiquetas",
      "pessoaId": "bibliotecaria",
      "titulo": "A separação das etiquetas",
      "texto": "A bibliotecária conta que deixou cada série presa por uma faixa de papel.",
      "narracao": "A bibliotecária conta que deixou cada série presa por uma faixa de papel."
    }
  ]
}
```

### Pista

`id`, `titulo` e `descricao` são obrigatórios. `relevancia` é a frase curta
mostrada no momento da descoberta para explicar, sem antecipar a solução, por
que o detalhe merece ser guardado. Uma pista é imutável e aparece uma única vez
na definição; diferentes caminhos revelam o mesmo id.

### Pessoa

`id`, `nome`, `papel` e uma lista não vazia de `anotacoes` são obrigatórios.
`imagem` é opcional; quando existir, `recurso` e `descricaoAcessivel` são
obrigatórios. Cada anotação possui `id` e `texto`. A pessoa se torna visível ao
receber sua primeira anotação ou conversa; a tela mostra as anotações reveladas
na ordem em que foram descobertas.

### Local

`id`, `nome` e uma lista não vazia de `anotacoes` são obrigatórios. `imagem` é
opcional e segue a mesma regra de acessibilidade. O local se torna visível ao
receber sua primeira anotação.

### Conversa

`id`, `pessoaId`, `titulo` e `texto` são obrigatórios. `narracao` é opcional e,
quando omitida, usa `texto`. Rever uma conversa abre este registro; não move a
sessão para uma cena antiga e não cria uma transição paralela no grafo.

Ids são únicos dentro de sua coleção. Ids de anotações de pessoas são únicos
entre todas as pessoas; ids de anotações de locais são únicos entre todos os
locais.

## Lembranças de retomada

```json
{
  "id": "series-estavam-separadas",
  "texto": "As duas séries estavam separadas no fim da tarde.",
  "essencial": true
}
```

| Campo | Obrigatório | Padrão |
|---|---|---|
| `id` | sim | — |
| `texto` | sim | — |
| `essencial` | não | `false` |

Lembranças não são uma segunda coleção de pistas. São frases de orientação,
escritas para a tela de Retomada e reveladas pelo percurso. A tela seleciona no
máximo três: primeiro as essenciais mais recentes e depois as demais mais
recentes, sempre preservando a ordem em que foram descobertas.

## Cenas, escolhas e revelações

```json
{
  "id": "armario",
  "tipo": "comum",
  "etapaId": "comparar-registros",
  "objetivoId": "encontrar-primeira-troca",
  "pontoDePausa": true,
  "texto": "As fichas formam duas sequências quase iguais.",
  "imagem": {
    "recurso": "cena_catalogo_armario",
    "descricaoAcessivel": "Duas fileiras de cartões com etiquetas de cores diferentes."
  },
  "narracao": "As fichas formam duas sequências quase iguais.",
  "revelacoes": {
    "pistas": ["etiqueta-azul"],
    "anotacoesPessoas": ["bibliotecaria-separou-series"],
    "anotacoesLocais": ["sala-armario-compartilhado"],
    "conversas": ["conversa-bibliotecaria-etiquetas"],
    "lembrancas": ["series-estavam-separadas"]
  },
  "escolhas": [
    {
      "id": "armario-a",
      "texto": "Comparar as cores das etiquetas",
      "proximaCena": "etiquetas",
      "dica": "Algumas cores só contam a verdade quando ficam lado a lado.",
      "revelacoes": {
        "pistas": ["faixa-de-papel"]
      }
    },
    {
      "id": "armario-b",
      "texto": "Conferir a lista de consulta",
      "proximaCena": "lista"
    }
  ]
}
```

### Cena comum

| Campo | Obrigatório | Padrão/regra |
|---|---|---|
| `id` | sim | Único no caso. |
| `tipo` | não | `comum`. |
| `etapaId` | sim | Etapa existente. |
| `objetivoId` | sim | Objetivo pertencente à mesma etapa. |
| `pontoDePausa` | não | `false`; indica um encerramento natural de sessão. |
| `texto` | sim | Texto narrativo. |
| `imagem` | sim | Recurso e descrição acessível. |
| `narracao` | não | Usa `texto`. |
| `revelacoes` | não | Todas as listas usam padrão vazio. |
| `escolhas` | sim | Exatamente duas. |
| `desfecho` | proibido | Exclusivo de cena final. |

### Escolha

`id`, `texto` e `proximaCena` são obrigatórios. `revelacoes` e `dica` são
opcionais. Ids de escolha são únicos em todo o caso, pois são persistidos e
reproduzidos.

`dica` é a mensagem que o Anônimo apresenta quando o algoritmo recomenda essa
escolha. Ela deve insinuar uma observação útil no universo da cena, sem repetir
o texto do botão nem usar instruções explícitas como “siga por”. Exemplo: para
“Examinar a vitrine”, prefira “Se eu fosse você, iria ajustar o cabelo; a
vitrine pode ser um bom espelho”.

Ao escolher, o domínio aplica primeiro as revelações da escolha e depois as da
cena de destino. Repetições são eliminadas pelo id, preservando a primeira ordem
de descoberta.

As duas escolhas precisam ter texto e destino distintos. Isso protege uma
decisão real de apresentação, mesmo quando os caminhos convergem mais adiante.

### Revelações

| Lista | Referencia |
|---|---|
| `pistas` | `caderno.pistas[].id` |
| `anotacoesPessoas` | `caderno.pessoas[].anotacoes[].id` |
| `anotacoesLocais` | `caderno.locais[].anotacoes[].id` |
| `conversas` | `caderno.conversas[].id` |
| `lembrancas` | `lembrancas[].id` |

O objeto e suas listas são opcionais, com padrão vazio. A mesma revelação pode
ser alcançada por caminhos diferentes, mas é adicionada uma única vez à sessão.

### Cena final

Cena final possui `tipo: "final"`, `etapaId`, texto, imagem, revelações
opcionais e `desfecho` no mesmo formato do esquema `1`. Ela omite `objetivoId`,
`pontoDePausa` e `escolhas`. Todo final pertence à última etapa.

## Regras do grafo e do validador

Além das regras do esquema `1`, o formato `2` exige:

- ids no padrão `^[a-z0-9]+(?:-[a-z0-9]+)*$`;
- `versaoConteudo >= 1` e versões iguais às declaradas no catálogo;
- ids únicos para etapas, objetivos, cenas, escolhas e cada coleção do caderno;
- cena inicial na primeira etapa;
- toda cena, etapa, objetivo e conteúdo revelável alcançável ou referenciado;
- cada cena comum ligada a um objetivo da própria etapa;
- cada transição permanece na etapa atual ou avança exatamente uma etapa;
- nenhuma transição volta para uma etapa anterior ou salta uma etapa;
- cenas finais somente na última etapa;
- grafo acíclico, com todos os caminhos terminando em final positivo;
- exatamente duas escolhas distintas em cada cena comum;
- toda referência em `revelacoes` existente;
- conversa vinculada a pessoa existente;
- em todo caminho que revela uma conversa, sua pessoa também fica visível no
  mesmo evento ou em evento anterior;
- todo texto obrigatório não vazio e toda imagem com descrição acessível;
- `resumoConclusao` verdadeiro para todos os caminhos que deixam a etapa —
  responsabilidade editorial revisada por pessoa, não inferível por teste;
- nenhum título ou texto de etapa futura entregue ao estado da interface.

O validador acumula problemas e os informa com caminho completo, por exemplo:

```text
caso "catalogo-fora-de-ordem", cena "armario", campo
"revelacoes.conversas[0]": a conversa "conversa-ausente" não existe
```

## Estado derivado

Para reconstruir uma sessão, o domínio:

1. abre `cenaInicial` e aplica suas revelações;
2. reproduz, em ordem, cada id de escolha salvo;
3. aplica as revelações da escolha e da cena de destino;
4. usa a cena resultante para obter etapa e objetivo atuais;
5. considera concluídas as etapas anteriores à etapa atual;
6. ordena pistas, anotações, conversas e lembranças pela primeira descoberta;
7. considera a sessão concluída somente ao alcançar um desfecho.

Se uma escolha não pertencer à cena reconstruída, a reprodução é incompatível
e para sem entregar estado parcial.

`pontoDePausa` não bloqueia nem força nada. Ele permite que a interface sugira
uma pausa depois de uma descoberta importante. Fechar o aplicativo em qualquer
cena continua seguro.

## O que é declarado, derivado e persistido

| Natureza | Dados |
|---|---|
| Declarado no JSON | Textos, etapas, objetivos, caderno, lembranças, cenas, escolhas, revelações e desfechos. |
| Derivado do percurso | Cena, etapa e objetivo atuais; etapas concluídas; itens revelados; ordem das descobertas; resumo e lembranças de retomada. |
| Persistido como fonte da verdade | `casoId`, `versaoEsquema`, `versaoConteudo`, sequência ordenada de escolhas e `atualizadoEm`. |
| Persistido como índice/cache | Cena atual e desfecho alcançado, para consultas sem abrir o caso. Podem ser recalculados. |
| Não persistido | Textos narrativos, títulos, imagens e cópias completas do caderno. |

O marcador visual “Nova” pertence ao evento da sessão atual. Ele não precisa
sobreviver ao fechamento do aplicativo e não integra o contrato de conteúdo.

## Retomada

A Retomada é aberta quando existe progresso válido e o intervalo definido pela
aplicação foi ultrapassado. O conteúdo vem de:

- `etapa.titulo` e `etapa.resumoRetomada` da etapa atual;
- `objetivo.texto` da cena atual;
- até três lembranças já reveladas;
- contagens de itens já revelados para o atalho do Caderno.

Se nenhuma lembrança tiver sido revelada, a tela omite a lista. O intervalo que
decide mostrar a tela é política da aplicação, não campo do caso.

## Compatibilidade e migração

`versaoEsquema` descreve a forma do arquivo. `versaoConteudo` descreve uma
revisão de um mesmo caso dentro dessa forma. A versão de conteúdo começa em `1`
e aumenta sempre que ids, transições ou significado narrativo puderem afetar um
progresso existente; correções exclusivamente ortográficas podem mantê-la.

| Progresso salvo | Caso instalado | Política |
|---|---|---|
| esquema e conteúdo iguais | iguais | Reproduzir normalmente. |
| mesmo esquema, conteúdo instalado maior | revisão nova | Tentar reproduzir todas as escolhas; se funcionar, atualizar a versão salva. |
| mesmo esquema, reprodução inválida | ids/caminho incompatíveis | Preservar o registro até o jogador confirmar o recomeço; explicar que o caso foi atualizado. |
| esquema `1`, caso continua `1` | piloto preservado | Reproduzir normalmente, usando `versaoConteudo = 1` na migração do banco. |
| esquema `1`, mesmo id migrou para `2` | caso ampliado | Não converter automaticamente; oferecer recomeço informado. Histórico de conclusões permanece. |
| esquema salvo maior que o suportado ou downgrade de conteúdo | incompatível | Não abrir parcialmente; manter dados e informar que esta versão do app não pode retomá-los. |
| caso removido do catálogo | órfão | Não oferecer em “Continuar”; manter dado local até política explícita de limpeza. |

O aplicativo nunca apaga silenciosamente progresso incompatível. Reiniciar
remove apenas o progresso ativo daquele caso; o histórico de conclusões é
preservado com as versões em que foi obtido.

## Contrato das telas

| Tela | Estado fornecido pelo domínio/aplicação |
|---|---|
| Retomada | Título do caso, etapa atual, resumo, até três lembranças, objetivo e id do caso. |
| Etapas | Etapas reveladas em ordem, estado concluída/atual/futura, resumo seguro e objetivo atual. |
| Caderno — Pistas | Pistas reveladas na ordem inversa de descoberta e pergunta em aberto. |
| Caderno — Pessoas | Pessoas visíveis, papel, imagem opcional, anotações e conversas reveladas. |
| Caderno — Locais | Locais visíveis, imagem opcional e anotações reveladas. |
| Caderno — Conversas | Conversas reveladas, agrupáveis por pessoa, sem alterar o grafo. |

Nenhuma tela recebe DTO, entidade Room, item ainda oculto ou texto de etapa
futura.

## Casos usados para revisar o contrato

- **O Catálogo Fora de Ordem:** investigação orientada por objetos, etiquetas,
  locais e registros escritos; valida pistas em escolhas e convergência de
  caminhos.
- **A Transmissão Incompleta:** investigação orientada por pessoas e conversas;
  valida anotações progressivas, releitura de falas e finais distintos.

Os dois são fixtures técnicos curtos, não casos destinados à publicação. Eles
demonstram formatos narrativos diferentes sem usar nomes, eventos ou entidades
reais e sem introduzir regra específica no motor.

## Implementação de referência

- DTOs e mapeadores: `data/caso/dto/`;
- modelos puros: `domain/model/caso/`;
- regras v2: `domain/validacao/ValidadorCasoLongo.kt`;
- projeções e reconstrução: `SessaoInvestigacao` e `MecanismoNarrativo`;
- compatibilidade v1/v2: `RepositorioCasosJson`;
- versões persistidas e migração 1→2: `data/banco/`.

O catálogo de produção contém casos no formato `2`, incluindo *O Silêncio da
Galeria Nove* e *O Sumiço da Múmia*. Eles foram integrados somente por JSON e
artes locais, confirmando que roteiro, etapas, caderno e finais não exigem
condicionais específicas no motor.
