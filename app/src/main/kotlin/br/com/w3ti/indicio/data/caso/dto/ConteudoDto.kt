package br.com.w3ti.indicio.data.caso.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Representações exclusivas do contrato JSON. Nunca atravessam o repositório. */
@Serializable
internal data class CatalogoDto(
    val versaoEsquema: Int,
    val casos: List<ResumoCasoDto>,
)

@Serializable
internal data class ResumoCasoDto(
    val id: String,
    val titulo: String,
    val sinopse: String,
    val categoria: CategoriaDto,
    val arquivo: String? = null,
    val disponivel: Boolean = false,
    val imagem: ImagemDto? = null,
)

@Serializable
internal data class CasoDto(
    val versaoEsquema: Int,
    val id: String,
    val titulo: String,
    val sinopse: String,
    val categoria: CategoriaDto,
    val cenaInicial: String,
    val cenas: List<CenaDto>,
)

@Serializable
internal enum class CategoriaDto {
    @SerialName("futebol")
    FUTEBOL,

    @SerialName("misterios_policiais")
    MISTERIOS_POLICIAIS,

    @SerialName("faroeste")
    FAROESTE,

    @SerialName("romances_classicos")
    ROMANCES_CLASSICOS,

    @SerialName("cultura_popular_antiga")
    CULTURA_POPULAR_ANTIGA,
}

@Serializable
internal enum class TipoCenaDto {
    @SerialName("comum")
    COMUM,

    @SerialName("final")
    FINAL,
}

@Serializable
internal data class CenaDto(
    val id: String,
    val tipo: TipoCenaDto = TipoCenaDto.COMUM,
    val texto: String,
    val imagem: ImagemDto,
    val narracao: String? = null,
    val pista: PistaDto? = null,
    val escolhas: List<EscolhaDto> = emptyList(),
    val desfecho: DesfechoDto? = null,
)

@Serializable
internal data class ImagemDto(
    val recurso: String,
    val descricaoAcessivel: String,
)

@Serializable
internal data class PistaDto(
    val id: String,
    val titulo: String,
    val descricao: String,
    val relevancia: String? = null,
)

@Serializable
internal data class EscolhaDto(
    val id: String,
    val texto: String,
    val proximaCena: String,
    val pista: PistaDto? = null,
)

@Serializable
internal data class DesfechoDto(
    val titulo: String,
    val mensagem: String,
    val explicacaoPistas: String,
)

@Serializable
internal data class CatalogoV2Dto(
    val versaoCatalogo: Int,
    val casos: List<ResumoCasoV2Dto>,
)

@Serializable
internal data class ResumoCasoV2Dto(
    val id: String,
    val titulo: String,
    val sinopse: String,
    val categoria: CategoriaDto,
    val arquivo: String? = null,
    val disponivel: Boolean = false,
    val versaoEsquema: Int? = null,
    val versaoConteudo: Int? = null,
    val imagem: ImagemDto? = null,
)

@Serializable
internal data class CasoV2Dto(
    val versaoEsquema: Int,
    val versaoConteudo: Int,
    val id: String,
    val titulo: String,
    val sinopse: String,
    val categoria: CategoriaDto,
    val cenaInicial: String,
    val etapas: List<EtapaDto>,
    val caderno: CadernoDto,
    val lembrancas: List<LembrancaDto>,
    val cenas: List<CenaV2Dto>,
)

@Serializable
internal data class EtapaDto(
    val id: String,
    val titulo: String,
    val descricao: String,
    val resumoConclusao: String,
    val resumoRetomada: String,
    val objetivos: List<ObjetivoDto>,
)

@Serializable
internal data class ObjetivoDto(
    val id: String,
    val texto: String,
    val perguntaEmAberto: String,
)

@Serializable
internal data class CadernoDto(
    val pistas: List<PistaDto>,
    val pessoas: List<PessoaDto>,
    val locais: List<LocalDto>,
    val conversas: List<ConversaDto>,
)

@Serializable
internal data class PessoaDto(
    val id: String,
    val nome: String,
    val papel: String,
    val imagem: ImagemDto? = null,
    val anotacoes: List<AnotacaoDto>,
)

@Serializable
internal data class LocalDto(
    val id: String,
    val nome: String,
    val imagem: ImagemDto? = null,
    val anotacoes: List<AnotacaoDto>,
)

@Serializable
internal data class AnotacaoDto(
    val id: String,
    val texto: String,
)

@Serializable
internal data class ConversaDto(
    val id: String,
    val pessoaId: String,
    val titulo: String,
    val texto: String,
    val narracao: String? = null,
)

@Serializable
internal data class LembrancaDto(
    val id: String,
    val texto: String,
    val essencial: Boolean = false,
)

@Serializable
internal data class RevelacoesDto(
    val pistas: List<String> = emptyList(),
    val anotacoesPessoas: List<String> = emptyList(),
    val anotacoesLocais: List<String> = emptyList(),
    val conversas: List<String> = emptyList(),
    val lembrancas: List<String> = emptyList(),
)

@Serializable
internal data class CenaV2Dto(
    val id: String,
    val tipo: TipoCenaDto = TipoCenaDto.COMUM,
    val etapaId: String,
    val objetivoId: String? = null,
    val pontoDePausa: Boolean = false,
    val texto: String,
    val imagem: ImagemDto,
    val narracao: String? = null,
    val revelacoes: RevelacoesDto = RevelacoesDto(),
    val escolhas: List<EscolhaV2Dto> = emptyList(),
    val desfecho: DesfechoDto? = null,
)

@Serializable
internal data class EscolhaV2Dto(
    val id: String,
    val texto: String,
    val proximaCena: String,
    val revelacoes: RevelacoesDto = RevelacoesDto(),
    val dica: String? = null,
)
