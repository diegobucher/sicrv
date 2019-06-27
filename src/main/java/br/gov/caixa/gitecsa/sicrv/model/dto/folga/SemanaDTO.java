package br.gov.caixa.gitecsa.sicrv.model.dto.folga;

import java.util.ArrayList;
import java.util.List;

public class SemanaDTO {
  
  private List<DiaDTO> diaDTOList;

  public SemanaDTO() {
    diaDTOList = new ArrayList<DiaDTO>();
  }

  public List<DiaDTO> getDiaDTOList() {
    return diaDTOList;
  }

  public void setDiaDTOList(List<DiaDTO> diaDTOList) {
    this.diaDTOList = diaDTOList;
  }
  
  @Override
  public String toString() {
    return diaDTOList.toString();
  }

}
