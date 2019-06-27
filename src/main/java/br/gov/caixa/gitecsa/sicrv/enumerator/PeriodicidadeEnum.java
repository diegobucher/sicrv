package br.gov.caixa.gitecsa.sicrv.enumerator;

import br.gov.caixa.gitecsa.arquitetura.enumerator.EnumInterface;

public enum PeriodicidadeEnum implements EnumInterface<Integer> {

  SEGASEXTA(0, "Fixa"),
  REVEZAMENTO(1, "Monitoramento");

  public static final String NOME_ENUM = "br.gov.caixa.gitecsa.sicrv.enumerator.PeriodicidadeEnum";

  private Integer valor;

  private String descricao;

  private PeriodicidadeEnum(Integer valor, String descricao) {
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

  public static PeriodicidadeEnum valueOf(Integer valor) {
    for (PeriodicidadeEnum tipo : values()) {
      if (tipo.getValor() == valor) {
        return tipo;
      }
    }

    return null;
  }

  public static PeriodicidadeEnum valueOfDescricao(String valor) {
    for (PeriodicidadeEnum tipo : values()) {
      if (tipo.getDescricao().equals(valor)) {
        return tipo;
      }
    }

    return null;
  }

}
