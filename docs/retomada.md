# Retomada — estado do projeto

Escrito em 20/08/2026 para que o trabalho possa ser retomado por outra pessoa,
ou pela mesma depois de um intervalo longo, sem arqueologia.

Leia também o [README](../README.md) (visão do produto e comandos) e o
[guia de contribuição](../CONTRIBUTING.md) (padrões técnicos e editoriais).

## Onde o projeto está

O aplicativo é **jogável do início ao fim, ilustrado e narrado**. O primeiro
caso, *O Mistério da Taça Desaparecida*, tem 15 cenas e três finais positivos.
Tudo funciona sem rede, sem conta e sem permissão nenhuma.

Oito das onze issues do backlog estão prontas e comitadas. Nenhuma foi fechada
no GitHub — ninguém pediu.

| Backlog | GitHub | Assunto | Estado |
|---|---|---|---|
| #001 | #1 | Fundação do projeto | pronta |
| #002 | #3 | Modelo e validação de casos | pronta |
| #003 | #4 | Mecanismo narrativo | pronta |
| #004 | #11 | Persistência e preferências | pronta |
| #005 | #2 | Caso da Taça Desaparecida | pronta |
| #006 | #6 | Navegação e telas | pronta |
| #007 | #10 | Experiência jogável e narração | pronta |
| #008 | #9 | Identidade visual | pronta |
| #009 | **#7** | **Acessibilidade** | **quase — falta o roteiro TalkBack** |
| #010 | **#5** | **Testes integrados** | **aberta** |
| #011 | **#8** | **Documentação e entrega** | **parcial** |

> **A numeração do GitHub não bate com a do backlog local.** Diga sempre "issue
> #N do GitHub" ou "#00N do backlog"; a tabela acima é a conversão.

## A decisão de produto que não está no backlog

Em 20/08/2026 o dono do projeto redirecionou o produto: **o Indício é um jogo de
cartas.** Cada cena é uma carta distribuída, que chega virada para baixo e é
revelada; cada escolha é uma carta a jogar. Toda a arte é retrato 2:3.

As issues #007 e #008 do backlog falam em "ilustrações" e "identidade visual" —
não em baralho. Quem ler só o backlog não descobre isso.

A carta é **aparência, nunca mecânica**: não há baralho, mão com limite nem
combinação de cartas. O motor narrativo, as escolhas e os finais continuam como
sempre foram, e as escolhas seguem expondo papel de botão.

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

Adiantado: `docs/como-criar-novos-casos.md`, `docs/recursos-visuais.md`,
`docs/telas/` e este documento. Falta:

- documentar a **arquitetura** (MVVM, fluxo unidirecional, DI, armazenamento,
  onde ficam os assets) — hoje não existe documento disso;
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
./gradlew test                      # 91 testes unitários
./gradlew lintDebug                 # 0 erros; 3 avisos NewerVersionAvailable
./gradlew assembleDebug
./gradlew assembleRelease           # ~1,5 MB
./gradlew connectedDebugAndroidTest # 43 testes; exige emulador
```

Se o total de unitários não for 91, ou se o lint acusar algo além de
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
docs/telas/                    maquetes das oito telas, em PNG e HTML
```

## Convenções

- Mensagens de commit em **português sem acentos**, no padrão Conventional
  Commits, com corpo em tópicos.
- **Todo commit vai acompanhado de push** — foi pedido explicitamente.
- Nada de rede, conta, anúncio, telemetria ou permissão nova sem discussão
  prévia em issue.
- O aviso médico aparece **uma única vez**, apenas em Sobre — há teste para
  isso (`AvisoMedicoTest`).
