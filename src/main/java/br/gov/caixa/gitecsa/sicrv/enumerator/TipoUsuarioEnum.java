package br.gov.caixa.gitecsa.sicrv.enumerator;

import br.gov.caixa.gitecsa.arquitetura.enumerator.EnumInterface;

public enum TipoUsuarioEnum implements EnumInterface<Integer> {

  ADMINISTRADOR(1, "ADMINISTRADOR"),
  SUPERVISOR(2, "SUPERVISOR"),
  GERAL(3, "GERAL");

  public static final String NOME_ENUM = "br.gov.caixa.gitecsa.sicrv.enumerator.TipoUsuarioEnum";

  private Integer valor;

  private String descricao;

  private TipoUsuarioEnum(int valor, String descricao) {
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

  public static TipoUsuarioEnum valueOf(Integer valor) {
    for (TipoUsuarioEnum tipo : values()) {
      if (tipo.getValor() == valor) {
        return tipo;
      }
    }

    return null;
  }

  public static TipoUsuarioEnum valueOfDescricao(String valor) {
    for (TipoUsuarioEnum tipo : values()) {
      if (tipo.getDescricao().equals(valor)) {
        return tipo;
      }
    }

    return null;
  }

}
