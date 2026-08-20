# Fixtures técnicos do esquema v2

Estes arquivos exercitam o contrato de
[`docs/esquema-narrativo-v2.md`](../../esquema-narrativo-v2.md) antes da
implementação do formato:

- `catalogo.json`: catálogo v2 com casos de formatos diferentes;
- `catalogo-fora-de-ordem.json`: caso orientado por objetos, locais e registros;
- `transmissao-incompleta.json`: caso orientado por pessoas e conversas.

Eles não são conteúdo de produção, não apontam para artes existentes e não
devem ser copiados para `app/src/main/assets/casos/`. Sua função é validar o
contrato da #012 e fornecer entradas para os testes da #013.

Os três arquivos devem ser JSON válido:

```bash
jq empty docs/exemplos/esquema-v2/*.json
```

Quando a #013 implementar DTOs e o validador v2, os dois casos devem ser
carregados nos testes a partir desta pasta ou de uma cópia mantida como fixture,
sem promover seu conteúdo ao catálogo do aplicativo.
