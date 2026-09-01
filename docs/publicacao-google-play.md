# Publicação do Indício na Google Play

Este documento resume o processo para criar uma conta de desenvolvedor, preparar
o Indício e publicá-lo na Google Play. Os requisitos da loja podem mudar; antes
de uma publicação, confira os links oficiais indicados ao longo do texto.

## 1. Criar a conta de desenvolvedor

A conta é criada no [Google Play Console](https://play.google.com/console/signup).

O cadastro exige:

- uma Conta Google;
- idade mínima de 18 anos;
- aceite do contrato de distribuição;
- pagamento único de US$ 25;
- verificação de identidade, endereço, telefone e e-mail;
- para contas pessoais novas, verificação de acesso a um aparelho Android real.

Consulte o [guia oficial de cadastro](https://support.google.com/googleplay/android-developer/answer/6112435?hl=pt-BR).

### Escolher o tipo de conta

- **Pessoal:** para desenvolvedor independente, estudante ou hobby.
- **Organização:** para publicação em nome de uma empresa ou instituição. Exige
  um número D-U-N-S correspondente à organização.

Ambas permitem publicar e monetizar aplicativos. Veja a
[comparação oficial dos tipos de conta](https://support.google.com/googleplay/android-developer/answer/13634885?hl=pt-BR).

Se a W3TI for a proprietária jurídica do Indício, a escolha coerente é uma conta
de organização em nome dela. Se o aplicativo pertencer a uma pessoa física, use
uma conta pessoal.

> **Atenção:** contas pessoais criadas depois de 13 de novembro de 2023 precisam
> realizar um teste fechado com pelo menos 12 pessoas inscritas continuamente
> durante 14 dias antes de solicitar acesso à produção. Consulte os
> [requisitos oficiais do teste fechado](https://support.google.com/googleplay/android-developer/answer/14151465?hl=pt-BR).

## 2. Informações necessárias

### Para a conta

- nome público do desenvolvedor;
- nome e endereço legais;
- e-mail e telefone de contato;
- perfil de pagamentos;
- número D-U-N-S, caso seja uma organização.

### Para a página do Indício

- nome do aplicativo, com até 30 caracteres;
- descrição curta, com até 80 caracteres;
- descrição completa, com até 4.000 caracteres;
- categoria e tags;
- e-mail de suporte;
- site de suporte, recomendado;
- URL pública da política de privacidade;
- ícone da Play Store;
- capturas de tela;
- imagem de destaque de 1024 x 500 pixels;
- países de distribuição;
- informação sobre gratuidade, compras e anúncios.

Consulte a documentação sobre a
[página do aplicativo](https://support.google.com/googleplay/android-developer/answer/9859152?hl=pt-BR)
e os [recursos gráficos](https://support.google.com/googleplay/android-developer/answer/9866151?hl=pt-BR).

Também será necessário declarar no Play Console:

- se o aplicativo contém anúncios;
- público-alvo e faixa etária;
- classificação indicativa;
- instruções de acesso para os revisores;
- práticas de segurança, coleta e compartilhamento de dados;
- uso de permissões especiais;
- eventual enquadramento em categoria regulada.

## 3. Situação atual do Indício

O projeto está configurado com:

- identificador `br.com.w3ti.indicio`;
- `versionCode = 1`;
- `versionName = "0.1.0"`;
- `targetSdk = 37`;
- funcionamento offline;
- nenhuma permissão Android declarada.

O identificador `br.com.w3ti.indicio` deve ser considerado permanente. Depois do
primeiro envio ao Play Console, não é possível alterá-lo e manter o aplicativo
como o mesmo produto na loja.

Como o Indício funciona offline e aparentemente não transmite dados nem possui
anúncios ou contas, a declaração de segurança de dados provavelmente será que o
aplicativo não coleta nem compartilha dados. Isso deve ser conferido incluindo
todas as bibliotecas utilizadas. O formulário de
[Segurança dos dados](https://support.google.com/googleplay/android-developer/answer/10787469?hl=pt-BR)
é obrigatório.

Mesmo sem coleta de dados, deve ser preparada uma política de privacidade pública
que explique:

- que o aplicativo funciona offline;
- que não cria contas;
- que não envia dados ao desenvolvedor;
- quais informações ficam armazenadas somente no aparelho;
- como o backup do Android pode tratar esses dados;
- como solicitar suporte.

## 4. Gerar o arquivo para envio

O projeto ainda não possui uma configuração de assinatura de produção. Para
fazer o primeiro pacote pelo Android Studio:

1. Abra o projeto.
2. Acesse **Build > Generate Signed App Bundle or APK**.
3. Escolha **Android App Bundle**.
4. Crie um novo arquivo de chave, o *upload keystore*.
5. Guarde o arquivo e suas senhas em local seguro, com cópia de segurança.
6. Escolha a variante `release`.
7. Gere o arquivo `.aab`.

O Play Console utiliza o **Play App Signing**: o Google protege a chave final de
assinatura e a chave local é utilizada para autenticar os novos envios. Consulte
a documentação sobre [assinatura de aplicativos](https://developer.android.com/studio/publish/app-signing).

Nunca coloque o arquivo `.jks`, senhas ou outras credenciais no Git.

## 5. Enviar e publicar

1. Entre no Play Console e clique em **Criar app**.
2. Informe nome, idioma, tipo de produto e se ele é gratuito ou pago.
3. Complete todas as tarefas obrigatórias mostradas no painel.
4. Preencha a página da loja e os formulários de conteúdo.
5. Acesse **Teste e lançamento > Teste interno**.
6. Crie uma versão e envie o arquivo `.aab` assinado.
7. Instale o aplicativo pela própria Play Store e faça a validação.
8. Para conta pessoal nova, faça o teste fechado com 12 pessoas por 14 dias.
9. Solicite acesso à produção.
10. Em **Produção**, crie a versão, selecione os países e envie para revisão.

Consulte o procedimento oficial para
[preparar e lançar uma versão](https://support.google.com/googleplay/android-developer/answer/9859348?hl=pt-BR).

## Próximas decisões

Antes de iniciar a publicação:

1. decidir se a conta será pessoal ou pertencente à organização W3TI;
2. criar e verificar a conta no Play Console;
3. preparar a política de privacidade;
4. preparar os textos e recursos gráficos da página da loja;
5. configurar com segurança a assinatura da versão `release`;
6. gerar e validar o Android App Bundle;
7. organizar os participantes do teste fechado, se ele for obrigatório.
