package br.gov.caixa.gitecsa.sicrv.model.dto.folga;

import java.util.Date;

public class DiaDTO {

  private String classeCss;

  private String numeroDia;

  private String saldoFolga;
  
  private Date data;
  
  private Long idFolga;
  
  private String horario;
  
  private String ausencia;

  public DiaDTO() {
  }

  public String getClasseCss() {
    return classeCss;
  }

  public void setClasseCss(String classeCss) {
    this.classeCss = classeCss;
  }

  public String getNumeroDia() {
    return numeroDia;
  }

  public void setNumeroDia(String numeroDia) {
    this.numeroDia = numeroDia;
  }

  public String getSaldoFolga() {
    return saldoFolga;
  }

  public void setSaldoFolga(String saldoFolga) {
    this.saldoFolga = saldoFolga;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("DIA:");
    sb.append(" NUMERODIA: "+ numeroDia);
    
    return sb.toString();
  }

  public Date getData() {
    return data;
  }

  public void setData(Date data) {
    this.data = data;
  }

  public Long getIdFolga() {
    return idFolga;
  }

  public void setIdFolga(Long idFolga) {
    this.idFolga = idFolga;
  }

  public String getHorario() {
    return horario;
  }

  public void setHorario(String horario) {
    this.horario = horario;
  }

  public String getAusencia() {
    return ausencia;
  }

  public void setAusencia(String ausencia) {
    this.ausencia = ausencia;
  }


}
