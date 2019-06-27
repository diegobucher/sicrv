package br.gov.caixa.gitecsa.sicrv.enumerator;

import br.gov.caixa.gitecsa.arquitetura.enumerator.EnumInterface;

public enum MotivoAusenciaEmpregadoEnum implements EnumInterface<Integer> {

	ATESTADO(2, "Atestado"),

	LICENCA_PREMIO(3, "Licença Prêmio"),

	ANIVERSARIO(4, "Aniversário"),

	DESTACAMENTO(5, "Destacamento"),

	CONVOCACAO_JURI_POPULAR(6, "Convocação Juri Popular"),

	FERIAS(7, "Férias"),

	SUBSTITUICAO(8, "Substituição"),

	LICENCA_OUTROS_MOTIVOS(9, "Licença Outros Motivos");

	public static final String NOME_ENUM = "br.gov.caixa.gitecsa.sicrv.enumerator.MotivoAusenciaEmpregadoEnum";

	private Integer valor;

	private String descricao;

	private MotivoAusenciaEmpregadoEnum(Integer valor, String descricao) {
		this.descricao = descricao;
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String toString() {
		return this.getDescricao();
	}

	public static MotivoAusenciaEmpregadoEnum valueOf(Integer valor) {
		for (MotivoAusenciaEmpregadoEnum tipo : values()) {
			if (tipo.getValor() == valor) {
				return tipo;
			}
		}

		return null;
	}

	public static MotivoAusenciaEmpregadoEnum valueOfDescricao(String valor) {
		for (MotivoAusenciaEmpregadoEnum tipo : values()) {
			if (tipo.getDescricao().equals(valor)) {
				return tipo;
			}
		}
		return null;
	}

	public Integer getValor() {
		return valor;
	}

	public void setValor(Integer valor) {
		this.valor = valor;
	}
}
