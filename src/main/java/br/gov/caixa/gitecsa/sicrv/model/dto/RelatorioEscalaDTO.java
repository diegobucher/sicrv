package br.gov.caixa.gitecsa.sicrv.model.dto;

public class RelatorioEscalaDTO {
  
  private Long idEmpregado;
  private String nome;
  private String segunda;
  private String terca;
  private String quarta;
  private String quinta;
  private String sexta;
  private String sabado;
  private String domingo;
  private String periodoPorExtenso;
  
  public String getNome() {
    return nome;
  }
  public void setNome(String nome) {
    this.nome = nome;
  }
  public String getSegunda() {
    return segunda;
  }
  public void setSegunda(String segunda) {
    this.segunda = segunda;
  }
  public String getTerca() {
    return terca;
  }
  public void setTerca(String terca) {
    this.terca = terca;
  }
  public String getQuarta() {
    return quarta;
  }
  public void setQuarta(String quarta) {
    this.quarta = quarta;
  }
  public String getQuinta() {
    return quinta;
  }
  public void setQuinta(String quinta) {
    this.quinta = quinta;
  }
  public String getSexta() {
    return sexta;
  }
  public void setSexta(String sexta) {
    this.sexta = sexta;
  }
  public String getSabado() {
    return sabado;
  }
  public void setSabado(String sabado) {
    this.sabado = sabado;
  }
  public String getDomingo() {
    return domingo;
  }
  public void setDomingo(String domingo) {
    this.domingo = domingo;
  }
  public Long getIdEmpregado() {
    return idEmpregado;
  }
  public void setIdEmpregado(Long idEmpregado) {
    this.idEmpregado = idEmpregado;
  }
  public String getPeriodoPorExtenso() {
    return periodoPorExtenso;
  }
  public void setPeriodoPorExtenso(String periodoPorExtenso) {
    this.periodoPorExtenso = periodoPorExtenso;
  }
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((idEmpregado == null) ? 0 : idEmpregado.hashCode());
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof RelatorioEscalaDTO)) {
      return false;
    }
    RelatorioEscalaDTO other = (RelatorioEscalaDTO) obj;
    if (idEmpregado == null) {
      if (other.idEmpregado != null) {
        return false;
      }
    } else if (!idEmpregado.equals(other.idEmpregado)) {
      return false;
    }
    return true;
  }
}