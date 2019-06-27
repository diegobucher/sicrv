package br.gov.caixa.gitecsa.sicrv.enumerator;

import java.util.Calendar;

import br.gov.caixa.gitecsa.arquitetura.enumerator.EnumInterface;

public enum DiaSemanaEnum implements EnumInterface<Integer> {

  DOMINGO(1, "Domingo"),
  SEGUNDA(2, "Segunda"),
  TERCA(3, "Terça"),
  QUARTA(4, "Quarta"),
  QUINTA(5, "Quinta"),
  SEXTA(6, "Sexta"),
  SABADO(7, "Sábado");

  public static final String NOME_ENUM = "br.gov.caixa.gitecsa.sicrv.enumerator.DiaSemanaEnum";

  private Integer valor;

  private String descricao;

  private DiaSemanaEnum(int valor, String descricao) {
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

  public static DiaSemanaEnum valueOf(Integer valor) {
    for (DiaSemanaEnum tipo : values()) {
      if (tipo.getValor() == valor) {
        return tipo;
      }
    }
    return null;
  }

  public static DiaSemanaEnum valueOfDescricao(String valor) {
    for (DiaSemanaEnum tipo : values()) {
      if (tipo.getDescricao().equals(valor)) {
        return tipo;
      }
    }
    return null;
  }

  /**
   * Converte o Enum para o Int do Calendar, DAY OF WEEK
   */
  public Integer getIntCalendarDayOfWeek() {

    switch (this) {
    case SEGUNDA:
      return Calendar.MONDAY;
    case TERCA:
      return Calendar.TUESDAY;
    case QUARTA:
      return Calendar.WEDNESDAY;
    case QUINTA:
      return Calendar.THURSDAY;
    case SEXTA:
      return Calendar.FRIDAY;
    case SABADO:
      return Calendar.SATURDAY;
    case DOMINGO:
      return Calendar.SUNDAY;
    default:
      return null;
    }
  }

}
