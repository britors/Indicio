package br.com.avoren.indicio.data.caso.dto

import br.com.avoren.indicio.domain.model.caso.Caso
import br.com.avoren.indicio.domain.model.caso.AnotacaoCaderno
import br.com.avoren.indicio.domain.model.caso.CadernoCaso
import br.com.avoren.indicio.domain.model.caso.Catalogo
import br.com.avoren.indicio.domain.model.caso.Categoria
import br.com.avoren.indicio.domain.model.caso.Cena
import br.com.avoren.indicio.domain.model.caso.Desfecho
import br.com.avoren.indicio.domain.model.caso.Escolha
import br.com.avoren.indicio.domain.model.caso.Etapa
import br.com.avoren.indicio.domain.model.caso.Imagem
import br.com.avoren.indicio.domain.model.caso.Lembranca
import br.com.avoren.indicio.domain.model.caso.LocalCaso
import br.com.avoren.indicio.domain.model.caso.Objetivo
import br.com.avoren.indicio.domain.model.caso.PessoaCaso
import br.com.avoren.indicio.domain.model.caso.Pista
import br.com.avoren.indicio.domain.model.caso.Revelacoes
import br.com.avoren.indicio.domain.model.caso.RevisaoCaso
import br.com.avoren.indicio.domain.model.caso.ResumoCaso
import br.com.avoren.indicio.domain.model.caso.TipoCena
import br.com.avoren.indicio.domain.model.caso.Conversa

/** Camada anticorrupção entre o contrato de armazenamento e o domínio. */
internal fun CatalogoDto.paraDominio() = Catalogo(
    casos = casos.map(ResumoCasoDto::paraDominio),
)

internal fun ResumoCasoDto.paraDominio() = ResumoCaso(
    id = id,
    titulo = titulo,
    sinopse = sinopse,
    categoria = categoria.paraDominio(),
    disponivel = disponivel,
    revisao = RevisaoCaso.V1.takeIf { disponivel },
)

internal fun CasoDto.paraDominio() = Caso(
    id = id,
    titulo = titulo,
    sinopse = sinopse,
    categoria = categoria.paraDominio(),
    cenaInicial = cenaInicial,
    cenas = cenas.map(CenaDto::paraDominio),
    revisao = RevisaoCaso.V1,
)

internal fun ResumoCasoV2Dto.paraDominio() = ResumoCaso(
    id = id,
    titulo = titulo,
    sinopse = sinopse,
    categoria = categoria.paraDominio(),
    disponivel = disponivel,
    revisao = if (versaoEsquema != null && versaoConteudo != null) {
        RevisaoCaso(versaoEsquema, versaoConteudo)
    } else {
        null
    },
)

internal fun CasoV2Dto.paraDominio() = Caso(
    id = id,
    titulo = titulo,
    sinopse = sinopse,
    categoria = categoria.paraDominio(),
    cenaInicial = cenaInicial,
    cenas = cenas.map(CenaV2Dto::paraDominio),
    revisao = RevisaoCaso(versaoEsquema, versaoConteudo),
    etapas = etapas.map(EtapaDto::paraDominio),
    caderno = caderno.paraDominio(),
    lembrancas = lembrancas.map(LembrancaDto::paraDominio),
)

internal fun CategoriaDto.paraDominio(): Categoria = when (this) {
    CategoriaDto.FUTEBOL -> Categoria.FUTEBOL
    CategoriaDto.MISTERIOS_POLICIAIS -> Categoria.MISTERIOS_POLICIAIS
    CategoriaDto.FAROESTE -> Categoria.FAROESTE
    CategoriaDto.ROMANCES_CLASSICOS -> Categoria.ROMANCES_CLASSICOS
    CategoriaDto.CULTURA_POPULAR_ANTIGA -> Categoria.CULTURA_POPULAR_ANTIGA
}

private fun CenaDto.paraDominio() = Cena(
    id = id,
    tipo = tipo.paraDominio(),
    texto = texto,
    imagem = imagem.paraDominio(),
    narracao = narracao,
    pista = pista?.paraDominio(),
    escolhas = escolhas.map(EscolhaDto::paraDominio),
    desfecho = desfecho?.paraDominio(),
)

private fun TipoCenaDto.paraDominio(): TipoCena = when (this) {
    TipoCenaDto.COMUM -> TipoCena.COMUM
    TipoCenaDto.FINAL -> TipoCena.FINAL
}

private fun ImagemDto.paraDominio() = Imagem(
    recurso = recurso,
    descricaoAcessivel = descricaoAcessivel,
)

private fun PistaDto.paraDominio() = Pista(
    id = id,
    titulo = titulo,
    descricao = descricao,
    relevancia = relevancia,
)

private fun EscolhaDto.paraDominio() = Escolha(
    id = id,
    texto = texto,
    proximaCena = proximaCena,
    pista = pista?.paraDominio(),
)

private fun DesfechoDto.paraDominio() = Desfecho(
    titulo = titulo,
    mensagem = mensagem,
    explicacaoPistas = explicacaoPistas,
)

private fun EtapaDto.paraDominio() = Etapa(
    id = id,
    titulo = titulo,
    descricao = descricao,
    resumoConclusao = resumoConclusao,
    resumoRetomada = resumoRetomada,
    objetivos = objetivos.map(ObjetivoDto::paraDominio),
)

private fun ObjetivoDto.paraDominio() = Objetivo(id, texto, perguntaEmAberto)

private fun CadernoDto.paraDominio() = CadernoCaso(
    pistas = pistas.map(PistaDto::paraDominio),
    pessoas = pessoas.map(PessoaDto::paraDominio),
    locais = locais.map(LocalDto::paraDominio),
    conversas = conversas.map(ConversaDto::paraDominio),
)

private fun PessoaDto.paraDominio() = PessoaCaso(
    id = id,
    nome = nome,
    papel = papel,
    imagem = imagem?.paraDominio(),
    anotacoes = anotacoes.map(AnotacaoDto::paraDominio),
)

private fun LocalDto.paraDominio() = LocalCaso(
    id = id,
    nome = nome,
    imagem = imagem?.paraDominio(),
    anotacoes = anotacoes.map(AnotacaoDto::paraDominio),
)

private fun AnotacaoDto.paraDominio() = AnotacaoCaderno(id, texto)

private fun ConversaDto.paraDominio() = Conversa(id, pessoaId, titulo, texto, narracao)

private fun LembrancaDto.paraDominio() = Lembranca(id, texto, essencial)

private fun RevelacoesDto.paraDominio() = Revelacoes(
    pistas = pistas,
    anotacoesPessoas = anotacoesPessoas,
    anotacoesLocais = anotacoesLocais,
    conversas = conversas,
    lembrancas = lembrancas,
)

private fun CenaV2Dto.paraDominio() = Cena(
    id = id,
    tipo = tipo.paraDominio(),
    texto = texto,
    imagem = imagem.paraDominio(),
    narracao = narracao,
    escolhas = escolhas.map(EscolhaV2Dto::paraDominio),
    desfecho = desfecho?.paraDominio(),
    etapaId = etapaId,
    objetivoId = objetivoId,
    pontoDePausa = pontoDePausa,
    revelacoes = revelacoes.paraDominio(),
)

private fun EscolhaV2Dto.paraDominio() = Escolha(
    id = id,
    texto = texto,
    proximaCena = proximaCena,
    revelacoes = revelacoes.paraDominio(),
    dica = dica,
)
