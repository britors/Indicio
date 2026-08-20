# Como contribuir com o Indício

Obrigado pelo interesse em contribuir. O Indício busca oferecer investigação narrativa tranquila, adulta e acessível, com funcionamento totalmente offline.

## Preparação no openSUSE Leap 16.0

O script do projeto instala OpenJDK 21, Git, ADB e utilitários necessários a partir dos repositórios do openSUSE:

```bash
./scripts/setup-opensuse-leap.sh
```

Para instalar também o Android Studio pelo Flathub:

```bash
./scripts/setup-opensuse-leap.sh --with-android-studio
flatpak run com.google.AndroidStudio
```

O pacote do Flathub é mantido pela comunidade. Quem preferir a distribuição oficial deve baixar o arquivo para Linux na [documentação do Android Studio](https://developer.android.com/studio/install) e seguir o assistente. Em ambos os casos, conclua o Setup Wizard para instalar o Android SDK.

O Android Studio requer Linux x86_64 com glibc 2.31 ou mais recente. O emulador também requer virtualização habilitada; um aparelho físico com depuração USB é uma alternativa.

## Fluxo de contribuição

1. Escolha uma [issue](https://github.com/britors/Indicio/issues) e confirme que ela ainda está disponível.
2. Crie uma branch curta a partir de `main`, como `feat/mecanismo-narrativo` ou `fix/restauracao-progresso`.
3. Faça alterações pequenas e focadas, acompanhadas de testes.
4. Execute as verificações aplicáveis antes de enviar a contribuição.
5. Abra um pull request explicando o problema, a solução, os testes e eventuais impactos de acessibilidade.

Os comandos principais são:

```bash
./gradlew test
./gradlew lint
./gradlew connectedCheck
./gradlew assembleDebug
```

`connectedCheck` requer emulador ou dispositivo conectado. Use sempre o Gradle Wrapper (`./gradlew`), que fixa a versão do Gradle usada pelo projeto.

## Padrões técnicos

- Use Kotlin e APIs compatíveis com `minSdk 26`.
- Preserve MVVM, fluxo unidirecional, DDD, SOLID e a regra de dependência da
  Clean Architecture.
- Respeite as fronteiras e os contratos descritos em [Arquitetura](docs/arquitetura.md); nenhuma regra de um caso específico deve entrar no motor ou na interface.
- Mantenha `domain/` livre de Android, serialização e persistência;
  `application/` depende somente do domínio; `ui/` não conhece `data/`, `di/`
  ou `navegacao/`.
- Represente JSON e banco com DTOs/entidades externos e converta-os na fronteira;
  não anote agregados do domínio para atender um framework.
- Injete dependências pelo construtor. Somente a raiz de composição pode
  conhecer o container completo.
- Prefira nomes da linguagem ubíqua (`Caso`, `Cena`, `Escolha`, `Pista`,
  `SessaoInvestigacao`) e evite `Manager`, `Helper`, `Utils` ou abstrações sem
  regra, porta ou variação real.
- Mantenha funções pequenas e coesas, estados imutáveis e falhas explícitas;
  comentários devem explicar decisões, não repetir o código.
- Mantenha histórias e transições nos arquivos JSON locais, sem regras específicas de um caso fixadas no código. Para escrever ou alterar um caso, siga [Como criar novos casos](docs/como-criar-novos-casos.md).
- Não adicione serviços de rede, contas, anúncios, telemetria ou permissões sem uma discussão prévia em issue.
- Não deixe TODOs essenciais, telas inoperantes ou implementações simuladas em código de produção.
- Prefira commits claros e focados; não misture reformatações amplas com mudanças funcionais.

Toda revisão de código deve confirmar os cinco princípios SOLID:

- a unidade alterada tem uma responsabilidade coesa e um único motivo para
  mudar;
- comportamento novo estende capacidades gerais sem condicionais de enredo no
  motor;
- implementações reais e dublês preservam integralmente os contratos das
  portas;
- interfaces expõem somente as operações necessárias a seus consumidores;
- regras de alto nível dependem de abstrações internas, e detalhes externos são
  ligados apenas na composição.

Não se usa SOLID como justificativa para abstrações sem política, fronteira ou
variação real. Se uma mudança parecer exigir a quebra de um princípio, pare e
redesenhe a solução antes de implementar; a decisão arquitetural pode registrar
o raciocínio, mas não autoriza a violação.

## Conteúdo narrativo e visual

- Escreva em português do Brasil simples, respeitoso e adulto.
- Use somente personagens, equipes, campeonatos, marcas e símbolos fictícios ou devidamente licenciados. Todo nome novo é provisório até cumprir a [revisão jurídica de conteúdo](docs/revisao-juridica-de-conteudo.md).
- Evite violência, sustos, infantilização, tom hospitalar, punição e mensagens de fracasso.
- Faça histórias leves, mas não rasas: sustente o interesse com curiosidade,
  versões conflitantes, personagens marcantes e revelações significativas.
- Toda escolha deve permitir continuar; caminhos menos adequados devem oferecer contexto ou uma nova pista.
- Imagens e demais recursos precisam funcionar offline e ter origem/licença registrada.

## Acessibilidade

Toda contribuição de interface deve considerar:

- TalkBack e descrições acessíveis;
- botões com pelo menos 64 dp de altura;
- texto grande e muito grande sem truncamento;
- alto contraste e áreas de toque amplas;
- informação nunca transmitida apenas por cor ou imagem;
- ausência de tempo limite, flashes e animações obrigatórias;
- controles por toque sempre disponíveis, mesmo quando houver gestos.

Não faça alegações de tratamento, prevenção ou retardo de demência. O aviso médico definido pelo produto deve permanecer somente na tela Sobre.

## Pull requests

Inclua na descrição:

- issue relacionada;
- resumo da mudança;
- comandos de teste executados e resultados;
- capturas de tela para alterações visuais;
- teste manual com TalkBack e tamanhos de texto, quando aplicável;
- limitações ou decisões que mereçam revisão.
- impacto da mudança sobre SOLID e as fronteiras arquiteturais, quando houver.

Ao contribuir, você concorda que sua contribuição será licenciada sob a GNU General Public License v3.0, a mesma licença do projeto.
