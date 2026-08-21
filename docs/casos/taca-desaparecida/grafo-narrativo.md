# Grafo narrativo — O Mistério da Taça Desaparecida

**Estado:** duas escolhas por cena restauradas no JSON v2, conteúdo `5`
**Versão editorial:** `0.5`
**Título:** provisório

Este documento oferece uma leitura editorial do grafo publicado em
[`taca-desaparecida.json`](../../../app/src/main/assets/casos/taca-desaparecida.json).
O JSON é a fonte de verdade para ids, transições e revelações.

## Forma do grafo

O motor exige exatamente duas escolhas com textos e destinos distintos em toda
cena comum. Para manter 42 cenas em qualquer percurso sem criar ciclos, o caso
usa 42 camadas:

- a primeira camada contém apenas `s1-chegada`;
- cada uma das 41 camadas seguintes contém duas cenas alternativas;
- qualquer cena de uma camada aponta para as duas cenas da camada seguinte;
- as duas cenas da camada 42 apontam para os dois finais positivos.

O grafo possui 83 cenas comuns e dois finais. Cada jogador visita exatamente
42 cenas comuns e um final. As escolhas produzem `2^42`, ou
4.398.046.511.104 percursos possíveis; a qualidade estrutural é
comprovada por programação dinâmica sobre o DAG, sem enumerar combinações
individualmente.

## Camadas e transições

Dentro de cada linha, as duas cenas oferecem acesso às duas cenas da linha
seguinte. A troca de etapa acontece somente entre as camadas 7/8, 14/15,
21/22, 28/29 e 35/36.

| Camada | Etapa | Cena A | Cena B | Função |
|---:|---:|---|---|---|
| 1 | 1 | `s1-chegada` | — | Apresentar o vazio e abrir a investigação. |
| 2 | 1 | `s1-vitrine` | `s1-curadora` | Começar pelo objeto ou pela memória. |
| 3 | 1 | `s1-marcas` | `s1-planta` | Comparar piso e circulação. |
| 4 | 1 | `s1-vidro-luz` | `s1-memoria-montagem` | Separar o estado da vitrine da mudança do pedestal. |
| 5 | 1 | `s1-poeira-tempo` | `s1-medidas-passagem` | Datar o movimento cuidadoso. |
| 6 | 1 | `s1-comparar-sinais` | `s1-pergunta-acessos` | Sintetizar e formular a próxima pergunta. |
| 7 | 1 | `s1-pausa-a` | `s1-pausa-b` | Pausa segura da sala vazia. |
| 8 | 2 | `s2-portaria` | `s2-forro` | Abrir horários ou causa material. |
| 9 | 2 | `s2-livro` | `s2-chave` | Conferir registro e acesso autorizado. |
| 10 | 2 | `s2-turno` | `s2-umidade` | Resolver o limite do turno e o motivo. |
| 11 | 2 | `s2-registro` | `s2-bilhete` | Encontrar o aviso interrompido. |
| 12 | 2 | `s2-conservadora` | `s2-protocolo` | Confirmar a retirada preventiva. |
| 13 | 2 | `s2-bancada` | `s2-base` | Provar o primeiro destino. |
| 14 | 2 | `s2-pausa-a` | `s2-pausa-b` | Pausa segura depois das 6h10. |
| 15 | 3 | `s3-materiais` | `s3-relato` | Procurar o recipiente alternativo. |
| 16 | 3 | `s3-feltro` | `s3-armario-tatil` | Ligar proteção e caixa tátil. |
| 17 | 3 | `s3-caixa-cinza` | `s3-medidas` | Confirmar a adequação da caixa. |
| 18 | 3 | `s3-etiqueta` | `s3-painel` | Explicar a identificação incompleta. |
| 19 | 3 | `s3-carrinho` | `s3-identidade` | Mostrar a mudança de contexto. |
| 20 | 3 | `s3-sequencia` | `s3-pergunta-movimento` | Sintetizar e seguir o carrinho. |
| 21 | 3 | `s3-pausa-a` | `s3-pausa-b` | Pausa segura da caixa sem nome. |
| 22 | 4 | `s4-roda` | `s4-prancheta` | Começar pelo trajeto ou pela etiqueta. |
| 23 | 4 | `s4-passagem` | `s4-reconhecer-caixa` | Explicar a ação do montador. |
| 24 | 4 | `s4-planta-antiga` | `s4-planta-atual` | Apresentar os documentos aparentemente divergentes. |
| 25 | 4 | `s4-datas` | `s4-rotas` | Ordenar as plantas. |
| 26 | 4 | `s4-mesa` | `s4-etiqueta-sequencia` | Levar o percurso até a mediação. |
| 27 | 4 | `s4-contexto` | `s4-proxima-pessoa` | Mudar a pergunta para o conteúdo esperado. |
| 28 | 4 | `s4-pausa-a` | `s4-pausa-b` | Pausa segura do caminho do carrinho. |
| 29 | 5 | `s5-reproducao` | `s5-lista` | Comparar o conteúdo esperado. |
| 30 | 5 | `s5-caixa-vazia` | `s5-roteiro` | Provar que a reprodução já estava fora. |
| 31 | 5 | `s5-armario` | `s5-peso` | Introduzir o armário mais pesado. |
| 32 | 5 | `s5-planta` | `s5-marcas-rodas` | Seguir o destino atual. |
| 33 | 5 | `s5-porta` | `s5-compartimento` | Encontrar a caixa sem abri-la. |
| 34 | 5 | `s5-inventario` | `s5-cuidado-abertura` | Preparar confirmações independentes. |
| 35 | 5 | `s5-pausa-a` | `s5-pausa-b` | Pausa segura do volume trocado. |
| 36 | 6 | `s6-fibra` | `s6-peso` | Confirmar pelos vestígios físicos. |
| 37 | 6 | `s6-base` | `s6-etiqueta` | Cruzar identidade e medidas. |
| 38 | 6 | `s6-linha-tempo` | `s6-relatos` | Reconstituir a corrente. |
| 39 | 6 | `s6-abertura` | `s6-confirmacao` | Recuperar a peça com cuidado. |
| 40 | 6 | `s6-cada-trecho` | `s6-mapa-completo` | Mostrar o limite de cada versão. |
| 41 | 6 | `s6-exposicao-segura` | `s6-aprendizado` | Preparar a decisão final. |
| 42 | 6 | `s6-pausa-a` | `s6-pausa-b` | Escolher a ênfase do desfecho. |

## Finais

| Cena | Desfecho | Pistas explicadas |
|---|---|---|
| `final-historia-cuidado` | A exposição conta como conservação, montagem e mediação sustentam o acervo. | Vitrine; umidade; 6h10; caixa; etiqueta; plantas; reprodução; peso; feltro e base. |
| `final-combinado-claro` | A equipe cria etiqueta presa, quadro compartilhado e confirmação de destino. | As mesmas pistas, enfatizando a passagem de contexto entre rotinas. |
Os dois finais preservam a peça, respeitam as pessoas e explicam as pistas
centrais. Acessibilidade, conservação e mediação nunca aparecem como causa do
problema; a fragilidade estava apenas na passagem de contexto durante uma
situação excepcional.

## Invariantes automatizados

- 85 cenas alcançáveis: 83 comuns e dois finais;
- seis etapas visitadas em ordem;
- exatamente duas escolhas distintas em cada cena comum;
- 42 cenas comuns em todo percurso, sem atalho;
- nenhuma transição volta, salta etapa ou forma ciclo;
- os dois finais são alcançáveis;
- todo conteúdo do Caderno é revelado por ao menos um caminho;
- nenhuma conversa aparece antes da pessoa correspondente;
- nenhuma cena futura vaza para Retomada, Etapas ou Caderno.

O teste `ConteudoPublicadoTest` calcula os comprimentos mínimo e máximo até um
final. Ambos precisam permanecer em 42; isso prova a propriedade para todas as
combinações do DAG.

## Verificação humana

O conteúdo `5` possui 5.916–6.062 palavras de cena por rota. Em 20/08/2026, o
responsável jogou e aceitou o conteúdo `3`, que possuía duas escolhas. A
terceira linha investigativa adicionada no conteúdo `4` aumentou a carga de
decisão e foi retirada após feedback de uso. Não foi fornecido tempo
cronometrado, portanto nenhum registro equivale a uma medição comprovada de 60
minutos.
