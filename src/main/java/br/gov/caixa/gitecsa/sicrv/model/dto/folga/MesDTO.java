package br.gov.caixa.gitecsa.sicrv.model.dto.folga;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MesDTO {

  private Integer mesInt;

  private Integer anoInt;

  private String saldoFolgas;

  private String projecao;

  private List<SemanaDTO> semanaDTOList;

  public MesDTO() {
    semanaDTOList = new ArrayList<SemanaDTO>();
  }

  public List<SemanaDTO> getSemanaDTOList() {
    return semanaDTOList;
  }

  public void setSemanaDTOList(List<SemanaDTO> semanaDTOList) {
    this.semanaDTOList = semanaDTOList;
  }

  public String getSaldoFolgas() {
    return saldoFolgas;
  }

  public void setSaldoFolgas(String saldoFolgas) {
    this.saldoFolgas = saldoFolgas;
  }

  public String getProjecao() {
    return projecao;
  }

  public void setProjecao(String projecao) {
    this.projecao = projecao;
  }

  public Integer getMesInt() {
    return mesInt;
  }

  public void setMesInt(Integer mesInt) {
    this.mesInt = mesInt;
  }

  public Integer getAnoInt() {
    return anoInt;
  }

  public void setAnoInt(Integer anoInt) {
    this.anoInt = anoInt;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("\n MES: " + anoInt + "/" + mesInt + "\n");
    sb.append(semanaDTOList.toString());
    return sb.toString();
  }

  public String getNomeMes() {
    String nomeMes[] = new DateFormatSymbols(new Locale("pt", "BR")).getMonths();

    if (mesInt != null) {
      return nomeMes[mesInt];
    } else {
      return "";
    }
  }

}
