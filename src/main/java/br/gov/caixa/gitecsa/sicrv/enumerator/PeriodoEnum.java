package br.gov.caixa.gitecsa.sicrv.enumerator;

import br.gov.caixa.gitecsa.arquitetura.enumerator.EnumInterface;

public enum PeriodoEnum implements EnumInterface<Integer> {

  DIA_DA_SEMANA(0, "Dia da semana"),
  DATA_ESPECIFICA(1, "Data específica");

  public static final String NOME_ENUM = "br.gov.caixa.gitecsa.sicrv.enumerator.PeriodoEnum";

  private Integer valor;

  private String descricao;

  private PeriodoEnum(Integer valor, String descricao) {
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

  public static PeriodoEnum valueOf(Integer valor) {
    for (PeriodoEnum tipo : values()) {
      if (tipo.getValor() == valor) {
        return tipo;
      }
    }

    return null;
  }

  public static PeriodoEnum valueOfDescricao(String valor) {
    for (PeriodoEnum tipo : values()) {
      if (tipo.getDescricao().equals(valor)) {
        return tipo;
      }
    }

    return null;
  }

}
