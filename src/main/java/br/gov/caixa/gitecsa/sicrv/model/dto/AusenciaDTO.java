package br.gov.caixa.gitecsa.sicrv.model.dto;

import br.gov.caixa.gitecsa.sicrv.model.Ausencia;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;

public class AusenciaDTO {

  private Empregado empregado;
  private Ausencia ausencia;

  public Empregado getEmpregado() {
    return empregado;
  }

  public void setEmpregado(Empregado empregado) {
    this.empregado = empregado;
  }

  public Ausencia getAusencia() {
    return ausencia;
  }

  public void setAusencia(Ausencia ausencia) {
    this.ausencia = ausencia;
  }

}
