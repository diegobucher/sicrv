package br.gov.caixa.gitecsa.sicrv.model.dto;

import java.util.Calendar;

public enum DiaSemanaCalendarEnum {

  SEG(Calendar.MONDAY),
  TER(Calendar.TUESDAY),
  QUA(Calendar.WEDNESDAY),
  QUI(Calendar.THURSDAY),
  SEX(Calendar.FRIDAY),
  SAB(Calendar.SATURDAY),
  DOM(Calendar.SUNDAY);

  private int valor;

  private DiaSemanaCalendarEnum(int inteiro) {
    this.valor = inteiro;
  }

  public static DiaSemanaCalendarEnum valueOf(int valor) {
    for (DiaSemanaCalendarEnum dia : values()) {
      if (dia.getValor() == valor) {
        return dia;
      }
    }
    return null;
  }

  public int getValor() {
    return valor;
  }
}
