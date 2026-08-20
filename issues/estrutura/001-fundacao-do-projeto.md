# #001 — Criar a fundação do projeto Android

**Trilha:** Estrutura
**Estado:** Concluída

## Objetivo

Criar um projeto Android executável que sirva de base testável para todo o MVP.

## Escopo

- Configurar Gradle com Kotlin DSL, catálogo de versões e versões estáveis compatíveis.
- Criar módulo `app` com namespace/application ID `br.com.avoren.indicio` e `minSdk 26`.
- Usar Kotlin, Jetpack Compose, Material 3 e uma única Activity.
- Adicionar Navigation Compose, lifecycle/ViewModel, StateFlow/coroutines, Room, DataStore e Kotlin Serialization.
- Organizar pacotes por responsabilidades: `data`, `domain`, `ui`, `navigation` e `di` (ou equivalente documentado).
- Implementar injeção de dependências manual, simples e substituível em testes.
- Não declarar permissões de internet, conta, notificações ou dados sensíveis.
- Criar tema-base e uma tela inicial mínima funcional para provar a execução.

## Critérios de aceite

- O projeto sincroniza e compila em ambiente documentado.
- O app instala e abre em emulador/dispositivo API 26 ou superior.
- Há uma única Activity e nenhuma dependência exige conexão em tempo de execução.
- `./gradlew test` executa com sucesso.
- Não existem segredos, serviços remotos ou permissões desnecessárias.

## Verificação da etapa

- Executar compilação debug, testes unitários e inspeção do manifesto.
