# #002 — Implementar modelo, carregamento e validação de casos JSON

**Trilha:** Estrutura
**Estado:** Concluída no esquema `1`; evolução planejada na #012

## Objetivo

Permitir que histórias sejam adicionadas como arquivos JSON locais, sem lógica narrativa fixa no código.

## Escopo

- Definir DTOs serializáveis para o contrato JSON e modelos puros de domínio
  para caso, categoria, cena, imagem, escolha, pista e desfecho.
- Converter DTOs para o domínio numa camada anticorrupção em `data/`, sem
  anotações de serialização ou detalhes de arquivo nos agregados.
- Prever categorias: Futebol, Mistérios policiais, Faroeste, Romances clássicos e Desenhos e cultura popular antigos.
- Carregar catálogo e casos a partir de assets/raw locais com Kotlin Serialization.
- Criar repositório de casos com interface substituível em testes.
- Validar pelo menos:
  - identificadores duplicados;
  - cena inicial inexistente;
  - referências de próxima cena inexistentes;
  - cenas comuns sem exatamente duas escolhas completas;
  - cenas não finais sem saída;
  - finais sem metadados de conclusão;
  - cenas inalcançáveis;
  - ausência de texto, imagem ou descrição acessível obrigatória.
- Retornar erros legíveis com o caso, a cena e o campo problemático.

## Critérios de aceite

- Um caso válido é desserializado e disponibilizado ao domínio.
- JSON malformado ou grafo inválido falha de modo controlado, sem derrubar o app.
- Nenhuma cena ou transição do caso depende de `when`/`if` específico no código.
- O domínio compila sem depender de Kotlin Serialization ou Android.
- Testes cobrem casos válidos e cada classe principal de erro.

## Verificação da etapa

- Executar testes unitários do loader e do validador e compilar o app.
