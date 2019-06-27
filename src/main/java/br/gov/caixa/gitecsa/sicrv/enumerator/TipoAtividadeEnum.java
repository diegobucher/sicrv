package br.gov.caixa.gitecsa.sicrv.enumerator;

import br.gov.caixa.gitecsa.arquitetura.enumerator.EnumInterface;

public enum TipoAtividadeEnum implements EnumInterface<Integer> {

  ATIVIDADE(0, "Atividade"),
  SUBATIVIDADE(1, "Subatividade");

  public static final String NOME_ENUM = "br.gov.caixa.gitecsa.sicrv.enumerator.TipoAtividadeEnum";

  private Integer valor;

  private String descricao;

  private TipoAtividadeEnum(int valor, String descricao) {
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

  public static TipoAtividadeEnum valueOf(Integer valor) {
    for (TipoAtividadeEnum tipo : values()) {
      if (tipo.getValor() == valor) {
        return tipo;
      }
    }

    return null;
  }

  public static TipoAtividadeEnum valueOfDescricao(String valor) {
    for (TipoAtividadeEnum tipo : values()) {
      if (tipo.getDescricao().equals(valor)) {
        return tipo;
      }
    }

    return null;
  }

}
