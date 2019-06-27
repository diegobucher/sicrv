package br.gov.caixa.gitecsa.sicrv.enumerator;

import br.gov.caixa.gitecsa.arquitetura.enumerator.EnumInterface;


public enum HoraFixaEnum implements EnumInterface<Integer>{
	
	ZERO(0,"00:00"), 
	UM(1,"01:00"), 
	DOIS(2,"02:00"), 
	TRES(3,"03:00"), 
	QUATRO(4,"04:00"), 
	CINCO(5,"05:00"), 
	SEIS(6,"06:00"), 
	SETE(7,"07:00"), 
	OITO(8,"08:00"), 
	NOVE(9,"09:00"), 
	DEZ(10,"10:00"), 
	ONZE(11,"11:00"), 
	DOZE(12,"12:00"), 
	TREZE(13,"13:00"), 
	QUATORZE(14,"14:00"), 
	QUINZE(15,"15:00"), 
	DESESSEIS(16,"16:00"), 
	DESESSETE(17,"17:00"), 
	DESOITO(18,"18:00"), 
	DESENOVE(19,"19:00"), 
	VINTE(20,"20:00"), 
	VINTE_UM(21,"21:00"), 
	VINTE_DOIS(22,"22:00"), 
	VINTE_TRES(23,"23:00")
//	,VINTE_QUATRO(24,"24:00")
	;
	

	public static final String NOME_ENUM = "br.gov.caixa.gitecsa.sicrv.enumerator.HoraFixaEnum";
	
	private Integer valor;

	private String descricao;

	private HoraFixaEnum(Integer valor, String descricao) {
		this.descricao = descricao;
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getValor() {
		return valor;
	}

	public void setValor(Integer valor) {
		this.valor = valor;
	}

	public String toString() {
		return this.getDescricao();
	}

	public static HoraFixaEnum valueOf(Integer valor) {
		for (HoraFixaEnum tipo : values()) {
			if (tipo.getValor() == valor) {
				return tipo;
			}
		}
		return null;
	}

	public static HoraFixaEnum valueOfDescricao(String valor) {
		for (HoraFixaEnum tipo : values()) {
			if (tipo.getDescricao().equals(valor)) {
				return tipo;
			}
		}
		return null;
	}	
}
