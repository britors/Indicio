# Indício

> **Toda escolha revela uma pista.**

**Indício** será um RPG narrativo leve de investigação para Android. Em investigações desenvolvidas para serem jogadas ao longo de várias sessões, o jogador assume o papel de detetive, observa cenários, conversa com personagens, reúne pistas e toma decisões entre dois caminhos.

O primeiro caso do MVP é uma prova completa do mecanismo, mas não define a
duração desejada para o produto final. A direção atual é criar histórias que
mantenham o interesse por bastante tempo, e não casos resolvidos em cerca de
dez minutos. Essa permanência deve vir de mistério, personagens, descoberta e
progressão narrativa — nunca de pressão, punição ou mecanismos manipulativos.

O tom permanece **leve, sem violência e genuinamente divertido**. Leve não
significa raso, e ausência de violência não significa ausência de suspense: o
entretenimento vem da curiosidade, das versões que não se encaixam, dos segredos
cotidianos, de personagens marcantes e da satisfação de compreender o caso.

O projeto prioriza uma experiência adulta, tranquila e acessível, especialmente confortável para idosos e pessoas com dificuldades cognitivas leves. Não haverá limite de tempo, pontuação negativa, morte, “game over”, anúncios ou exigência de conta e conexão com a internet.

## Primeiro caso

### O Mistério da Taça Desaparecida

Poucas horas antes de uma exposição comemorativa, uma famosa taça do futebol desaparece de uma sala aparentemente trancada. O jogador deverá conversar com testemunhas, observar o ambiente, comparar versões e encontrar a taça.

Todos os nomes, equipes, campeonatos e símbolos serão fictícios.
Os nomes usados nos protótipos são provisórios e não estão liberados como
marcas. O processo obrigatório de pesquisa e aprovação está em
[Revisão jurídica de nomes e conteúdo](docs/revisao-juridica-de-conteudo.md).

## MVP planejado

- Aplicativo Android nativo em Kotlin.
- Jetpack Compose e Material 3.
- Activity única, MVVM e fluxo unidirecional.
- Histórias extensíveis carregadas de arquivos JSON locais.
- Progresso e histórico persistidos com Room.
- Preferências armazenadas com DataStore.
- Narração offline por meio do TextToSpeech do Android.
- Texto grande, controles amplos, alto contraste e suporte a TalkBack.
- Funcionamento totalmente offline e sem coleta de dados médicos.

O planejamento está dividido em issues do GitHub. Os textos-fonte do backlog também estão disponíveis em [`issues/`](issues/README.md).

## Escrevendo histórias

Casos novos são arquivos JSON locais; não é preciso alterar código Kotlin para
acrescentar uma história. O formato, as regras do validador e as regras
editoriais estão em
[Como criar novos casos](docs/como-criar-novos-casos.md). A origem, a licença e
as regras de desenho das artes estão em
[Recursos visuais](docs/recursos-visuais.md). Nomes, entidades, eventos,
personagens e símbolos também precisam passar pela
[revisão jurídica de conteúdo](docs/revisao-juridica-de-conteudo.md).
As camadas, os fluxos, os contratos de identificadores e a evolução para vários
casos estão descritos em [Arquitetura](docs/arquitetura.md).
O contrato aprovado para investigações longas está em
[Esquema narrativo v2](docs/esquema-narrativo-v2.md). O motor já lê e valida
casos v1 e v2 simultaneamente; o catálogo público ainda contém apenas o piloto
v1 enquanto o primeiro caso longo é produzido.

## Desenvolvimento

Quem utiliza openSUSE Leap 16.0 pode preparar as ferramentas básicas com:

```bash
./scripts/setup-opensuse-leap.sh
```

Consulte o [guia de contribuição](CONTRIBUTING.md) para instalação do Android Studio, fluxo de branches, testes e critérios técnicos e editoriais.

## Estado do projeto

Prontos:

- **Fundação do projeto** — o aplicativo compila, instala e abre em API 26 ou
  superior, com Activity única, tema-base e uma tela inicial mínima.
- **Modelo e validação de casos** — histórias são arquivos JSON locais em
  `app/src/main/assets/casos/`, carregados e validados sem nenhuma regra de
  caso específico no código.
- **Arquitetura sustentável** — DDD organiza a linguagem e as regras da
  investigação; Clean Architecture mantém domínio e casos de uso independentes
  de Android, JSON e banco; SOLID é regra permanente para responsabilidades,
  contratos, extensões e dependências; testes automatizados protegem as
  fronteiras verificáveis.
- **Núcleo de casos longos** — o formato v2 oferece etapas, objetivos, retomada,
  pessoas, locais e conversas reveladas, com migração de progresso e suporte
  simultâneo ao piloto v1.
- **Mecanismo de escolhas e pistas** — o núcleo de domínio percorre o grafo da
  história, acumula pistas sem repetição e reconstrói a sessão a partir do
  progresso salvo.
- **Persistência** — progresso e histórico de conclusões em Room, preferências
  de leitura em DataStore, com falhas de armazenamento tratadas sem perder o
  estado em memória.
- **Primeiro caso** — *O Mistério da Taça Desaparecida*, 15 cenas e três finais
  positivos, escrito inteiramente em JSON.
- **Navegação e telas** — apresentação, início, catálogo, história, pausa,
  conclusão, configurações e sobre, com progresso e preferências restaurados ao
  reabrir o aplicativo.
- **Experiência jogável e narração** — leitura em voz alta com o TextToSpeech do
  Android em português, com o caso permanecendo utilizável quando não há voz
  instalada no aparelho.
- **Identidade visual** — a história é jogada como um baralho: cada cena é uma
  carta distribuída, que chega virada para baixo e é revelada, e cada escolha é
  uma carta a jogar. A direção contemporânea das maquetes já está aplicada ao
  sistema Compose reutilizável. As 15 artes do primeiro caso e o verso comum
  são vetores originais, em 68 KB no total, funcionando sem rede.
- **Acessibilidade** — contraste conferido contra a WCAG AA e travado por teste,
  alvo de toque de 64 dp, escolhas com papel de botão, imagens com texto
  equivalente, texto ampliável em dois tamanhos e uso em pé ou deitado.

O caso é jogável do início ao fim, ilustrado e narrado.

Faltam o roteiro manual com TalkBack e os testes integrados de percurso completo.
O que já está pronto, o que falta e as armadilhas
conhecidas estão em [Retomada](docs/retomada.md).

### Compilação e testes

```bash
./gradlew assembleDebug        # APK de depuração
./gradlew test                 # testes unitários
./gradlew lintDebug            # análise estática
./gradlew connectedCheck       # testes instrumentados (requer emulador/dispositivo)
```

O `local.properties` com `sdk.dir` é gerado pelo Android Studio ou pelo script
`scripts/configure-android-dev.sh`, e não é versionado.

## Licença

Este projeto é software livre, licenciado sob a **GNU General Public License v3.0
(GPLv3)**. O texto completo está em [`LICENSE`](LICENSE); os termos oficiais
também estão em <https://www.gnu.org/licenses/gpl-3.0.html>.

O Indício é distribuído na esperança de ser útil, mas **sem nenhuma garantia**,
nem mesmo a garantia implícita de comercialização ou adequação a uma finalidade
específica. Consulte a GPLv3 para mais detalhes.
