package br.gov.caixa.gitecsa.sicrv.enumerator;

import br.gov.caixa.gitecsa.arquitetura.enumerator.EnumInterface;

public enum SituacaoFolgaEnum implements EnumInterface<Integer> {

  ADQUIRIDA(0, "Adquirida"),
  AGENDADA(1, "Agendada"),
  SUGERIDA(2, "Sugerida"),
  REPOUSO_REMUNERADO(3, "Repouso Remunerado");

  public static final String NOME_ENUM = "br.gov.caixa.gitecsa.sicrv.enumerator.SituacaoFolgaEnum";

  private Integer valor;

  private String descricao;

  private SituacaoFolgaEnum(Integer valor, String descricao) {
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

  public static SituacaoFolgaEnum valueOf(Integer valor) {
    for (SituacaoFolgaEnum tipo : values()) {
      if (tipo.getValor() == valor) {
        return tipo;
      }
    }

    return null;
  }

  public static SituacaoFolgaEnum valueOfDescricao(String valor) {
    for (SituacaoFolgaEnum tipo : values()) {
      if (tipo.getDescricao().equals(valor)) {
        return tipo;
      }
    }

    return null;
  }

}
