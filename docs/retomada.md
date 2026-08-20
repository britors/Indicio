# Retomada — estado do projeto

Escrito em 20/08/2026 para que o trabalho possa ser retomado por outra pessoa,
ou pela mesma depois de um intervalo longo, sem arqueologia.

Leia também o [README](../README.md) (visão do produto e comandos) e o
[guia de contribuição](../CONTRIBUTING.md) (padrões técnicos e editoriais).

## Onde o projeto está

O aplicativo é **jogável do início ao fim, ilustrado e narrado**. O primeiro
caso, *O Mistério da Taça Desaparecida*, tem 15 cenas e três finais positivos.
Tudo funciona sem rede, sem conta e sem permissão nenhuma.

As onze issues originais foram organizadas nas trilhas `issues/estrutura/` e
`issues/casos/`. Oito estão prontas; as três de validação final continuam
parciais. Oito novas issues locais (#012–#019) descrevem a arquitetura de casos
longos e a transformação da Taça no primeiro caso de produção. Nenhuma issue
foi fechada ou criada no GitHub — ninguém pediu essa alteração externa.

| Backlog | GitHub | Assunto | Estado |
|---|---|---|---|
| #001 | #1 | Fundação do projeto | pronta |
| #002 | #3 | Modelo e validação de casos | pronta |
| #003 | #4 | Mecanismo narrativo | pronta |
| #004 | #11 | Persistência e preferências | pronta |
| #005 | #2 | Piloto curto da Taça Desaparecida | pronta como piloto |
| #006 | #6 | Navegação e telas | pronta |
| #007 | #10 | Experiência jogável e narração | pronta |
| #008 | #9 | Identidade visual | pronta |
| #009 | **#7** | **Acessibilidade** | **parcial — validar TalkBack após as telas finais** |
| #010 | **#5** | **Testes integrados** | **aberta** |
| #011 | **#8** | **Documentação e entrega** | **parcial** |

> **A numeração do GitHub não bate com a do backlog local.** Diga sempre "issue
> #N do GitHub" ou "#00N do backlog"; a tabela acima é a conversão.

| Nova issue local | Trilha | Entrega | Estado |
|---|---|---|---|
| #012 | Estrutura | Esquema narrativo longo | concluída |
| #013 | Estrutura | Núcleo de casos longos | concluída |
| #014 | Estrutura | Retomada, Etapas e Caderno | concluída |
| #015 | Casos | Versão longa da Taça | concluída |
| #016 | Casos | Artes da versão longa | pendente |
| #017 | Casos | Integração e publicação | pendente |
| #018 | Estrutura | Aplicar direção visual contemporânea | concluída |
| #019 | Estrutura | DDD, Clean Architecture, SOLID e Clean Code | concluída |

A ordem e as dependências completas estão no
[backlog reorganizado](../issues/README.md).

O formato `2` está especificado em
[Esquema narrativo v2](esquema-narrativo-v2.md), acompanhado de dois fixtures
técnicos em `docs/exemplos/esquema-v2/`. O núcleo já lê, valida, percorre e
persiste casos v1 e v2 simultaneamente. A interface também já oferece Retomada,
Etapas e Caderno reconstruídos a partir do percurso. A Taça longa é agora o
primeiro caso v2 no catálogo de desenvolvimento. A bíblia, o mapa de seis
etapas e o grafo estão em
`docs/casos/taca-desaparecida/`. O conteúdo `4` oferece três escolhas por cena,
124 cenas comuns, três finais e 5.799–6.068 palavras de cena por percurso. O
responsável aceitou o conteúdo `3`; a terceira linha foi integrada depois desse
aceite e ainda requer inspeção no fluxo final. Não foi informado tempo
cronometrado. A próxima entrega, depois dessa conferência, é a produção das
artes (#016).

## A decisão de produto sobre as cartas

Em 20/08/2026 o dono do projeto redirecionou o produto: **o Indício é um jogo de
cartas.** Cada cena é uma carta distribuída, que chega virada para baixo e é
revelada; cada escolha é uma carta a jogar. Toda a arte é retrato 2:3.

Essa decisão também está registrada na issue estrutural #008.

A carta é **aparência, nunca mecânica**: não há baralho, mão com limite nem
combinação de cartas. O motor narrativo, as escolhas e os finais continuam como
sempre foram, e as escolhas seguem expondo papel de botão.

## Segunda decisão de produto: duração e envolvimento

O Indício **não deve ser um jogo que o usuário conclui em cerca de dez
minutos**. A intenção é criar investigações que prendam a atenção por bastante
tempo e possam ser retomadas ao longo de várias sessões.

O caso *O Mistério da Taça Desaparecida*, com 15 cenas, continua válido como
MVP e prova integral do mecanismo, mas **não é a referência de duração do
produto final**. O teto editorial de 18 cenas foi removido; os testes agora
exigem um mínimo de 12 sem impedir investigações longas.

O envolvimento deve vir de conteúdo e progressão:

- mistérios em camadas, com objetivos intermediários claros;
- personagens e versões que se desenvolvem ao longo da investigação;
- pistas, locais e linhas de investigação que se abrem gradualmente;
- pontos naturais de pausa e retomada, sem fazer o jogador se perder;
- sensação frequente de descoberta, sem alongamento artificial.

Continuam proibidos cronômetros, energia, punição por ausência, recompensas
diárias obrigatórias, notificações insistentes, finais bloqueados por espera e
outros mecanismos manipulativos de retenção. A meta é **envolvimento narrativo,
não dependência**.

## Tom narrativo: leve, seguro e interessante

O jogo deve ser **leve e sem violência**, mas precisa entreter de verdade.
“Leve” não é sinônimo de história rasa, previsível ou infantil, assim como
“sem violência” não significa eliminar suspense, surpresa ou conflito.

O interesse deve vir de:

- perguntas fortes que criem curiosidade desde o início;
- versões incompletas ou aparentemente contraditórias;
- segredos cotidianos, mal-entendidos e motivos humanos plausíveis;
- personagens adultos, distintos e agradáveis de acompanhar;
- pistas com significado que mudem a compreensão do caso;
- revelações frequentes e conclusões satisfatórias;
- humor discreto, afeto e alívio quando combinarem com a história.

Não usar agressão, ameaça, morte, susto, crueldade ou sofrimento como atalho
para criar interesse. A tensão é investigativa: o jogador quer compreender o
que aconteceu, não escapar de um perigo.

## Segurança jurídica de nomes e referências

Em 20/08/2026 foi definido que o projeto não publicará nomes de entidades,
eventos, equipes, personagens, símbolos ou marcas sem uma verificação formal.
Todo nome criado durante roteiro ou desenho de tela é **provisório**.

A busca preliminar encontrou coincidências nos nomes completos de personagens
e no nome do espaço usado pelo primeiro caso. Por cautela, os sobrenomes e os
nomes próprios ainda não liberados foram removidos do caso jogável. O nome
`Indício` continua como nome de trabalho, mas também precisa de busca no INPI e
revisão profissional antes de ser tratado como marca de lançamento.

O processo obrigatório, o resultado da auditoria inicial e seus limites estão
em [Revisão jurídica de nomes e conteúdo](revisao-juridica-de-conteudo.md).
Avisos de ficção e testes de palavras proibidas são apenas barreiras auxiliares;
não substituem busca de anterioridade, análise de semelhança nem revisão
jurídica antes de publicar.

## O que falta, em ordem

### 1. Acessibilidade — #7 do GitHub

Só resta o **roteiro manual com TalkBack**: ligar o leitor de tela no emulador e
percorrer o caso inteiro apenas por foco e toque duplo, conferindo ordem de foco
e ausência de anúncios repetidos. É o único item do escopo que nenhum teste
automatiza.

Já está feito e travado por teste: contraste WCAG AA, alvo de 64 dp, papel de
botão nas escolhas, texto equivalente das imagens, títulos como cabeçalho,
estado da narração anunciado, comportamento com o texto em "muito grande",
redução de movimentos e uso em paisagem.

### 2. Testes integrados — #5 do GitHub

Do escopo da issue, falta principalmente:

- percorrer **todos os caminhos** do caso até os três finais, por teste;
- o cenário **"escolher → fechar o app → reabrir → continuar"** ponta a ponta
  (hoje há cobertura das peças, não do percurso);
- ausência de TTS e estados de erro recuperáveis, na interface;
- verificação de formatação, se o projeto quiser adotar uma.

O teste de grafo já existe (`ConteudoPublicadoTest`) e cobre referências
inválidas, cenas sem saída e finais inalcançáveis.

### 3. Documentação e entrega — #8 do GitHub

Adiantado: `docs/arquitetura.md`, `docs/como-criar-novos-casos.md`,
`docs/recursos-visuais.md`, `docs/telas/` e este documento. Falta:

- registrar **limitações reais** do MVP sem maquiar;
- revisão final editorial do primeiro caso;
- gerar o APK e validar em **API 26** e numa API atual — só houve validação em
  API 37 (o emulador `Medium_Phone`);
- confirmar que uma pessoa nova consegue compilar e criar um caso seguindo só
  os documentos, em ambiente limpo.

## Como rodar

O `java` padrão da máquina é o **OpenJDK 25**, que o Android Gradle Plugin não
aceita. Todo comando Gradle precisa de:

```bash
export JAVA_HOME=/usr/lib64/jvm/java-21-openjdk
```

O script `scripts/configure-android-dev.sh` grava essa variável em `~/.bashrc` e
`~/.profile`, mas o shell em uso é **fish** — lá isso não tem efeito.

```bash
./gradlew test                      # 113 testes unitários
./gradlew lintDebug                 # 0 erros; 3 avisos NewerVersionAvailable
./gradlew assembleDebug
./gradlew assembleRelease           # ~1,5 MB
./gradlew connectedDebugAndroidTest # 50 testes; exige emulador
```

Se o total de unitários não for 113, ou se o lint acusar algo além de
`NewerVersionAvailable`, alguma coisa mudou desde 20/08/2026.

Os 3 avisos de lint são **deliberados**: o Kotlin está fixado em 2.3.21 porque o
KSP estável mais recente é compilado contra 2.3.20. O motivo está comentado em
`gradle/libs.versions.toml`. Só atualize o Kotlin junto com um KSP da mesma
série, ou a geração de código do Room quebra.

O emulador sobe assim (existe um único AVD, `Medium_Phone`; KVM disponível,
cerca de um minuto de boot):

```bash
$ANDROID_HOME/emulator/emulator -avd Medium_Phone -no-window -no-audio \
  -no-boot-anim -gpu swiftshader_indirect -no-snapshot
```

## Armadilhas conhecidas

Três coisas que já morderam e voltariam a morder.

### O encolhedor de recursos apaga a arte no release

`isShrinkResources = true`, e o nome da arte de cada cena vem do JSON, resolvido
por `getIdentifier`. Não existe referência estática a nenhuma delas.

`app/src/main/res/raw/keep.xml` preserva `@drawable/cena_*`. Sem esse arquivo,
das 16 artes sobrevive **uma** — o verso, a única referenciada por `R.drawable`.
O app funcionaria em depuração e cairia no texto de reserva em produção.

Toda arte nova precisa do prefixo `cena_`. Confira o APK com:

```bash
aapt2 dump resources app-release-unsigned.apk | grep -cE "drawable/(cena_|carta_verso)"  # 16
```

### Um token de cor esquecido vira cor do Material

A borda dos botões delineados esteve em **#CAC4D0** — padrão do Material 3, não
da paleta do Indício — porque o tema nunca definiu `outlineVariant`, e é dele
que o `OutlinedButton` desta versão tira a borda. Dava 1,58:1 de contraste,
contra os 3:1 que a WCAG 1.4.11 exige.

`ContrasteDoTemaTest` agora falha se um contorno voltar a ser herdado.

### Os testes não cobrem tudo o que parece

Os dois defeitos mais graves desta série **não vieram dos testes**:

- o contraste da borda saiu de **amostrar pixels** de uma captura do emulador;
- a arte engolindo a tela em **paisagem** saiu de girar o aparelho.

Os testes instrumentados rodam só em retrato e só enxergam o que a semântica
expõe. **Ao mexer em tema ou em layout, instale o app e gire a tela.**

## Onde as coisas ficam

```
app/src/main/assets/casos/     histórias em JSON — nenhuma regra de caso no código
app/src/main/res/drawable/     arte das cartas (cena_*) e o verso
app/src/main/res/raw/keep.xml  impede o encolhedor de apagar a arte
ui/carta/                      moldura, verso e a mecânica de distribuir
ui/tema/                       paleta, tipografia e o esquema de cores
docs/como-criar-novos-casos.md como escrever um caso novo
docs/recursos-visuais.md       origem, licença e regras de desenho da arte
docs/telas/                    maquetes aprovadas em PNG e seu README
```

## Convenções

- **SOLID é uma regra permanente.** Nenhuma entrega pode quebrar
  intencionalmente seus cinco princípios; se uma mudança parecer exigir isso,
  a implementação deve parar e a solução deve ser redesenhada. Nem uma decisão
  arquitetural autoriza a violação. A regra não justifica interfaces ou camadas
  cerimoniais sem variação real.
- Mensagens de commit em **português sem acentos**, no padrão Conventional
  Commits, com corpo em tópicos.
- **Todo commit vai acompanhado de push** — foi pedido explicitamente.
- Sempre que houver uma entrega visual ou interativa executável, instalar e
  deixar o aplicativo aberto no emulador para acompanhamento do responsável
  pelo projeto.
- Nada de rede, conta, anúncio, telemetria ou permissão nova sem discussão
  prévia em issue.
- O aviso médico aparece **uma única vez**, apenas em Sobre — há teste para
  isso (`AvisoMedicoTest`).
