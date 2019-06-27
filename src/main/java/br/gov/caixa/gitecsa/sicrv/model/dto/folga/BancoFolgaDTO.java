package br.gov.caixa.gitecsa.sicrv.model.dto.folga;

import java.util.List;

import br.gov.caixa.gitecsa.sicrv.model.Equipe;


public class BancoFolgaDTO {

  private Equipe equipe;
  private List<EmpregadoFolgaDTO> listEmpregadoFolgaDTO;
  
  public Equipe getEquipe() {
    return equipe;
  }
  public void setEquipe(Equipe equipe) {
    this.equipe = equipe;
  }
  public List<EmpregadoFolgaDTO> getListEmpregadoFolgaDTO() {
    return listEmpregadoFolgaDTO;
  }
  public void setListEmpregadoFolgaDTO(List<EmpregadoFolgaDTO> listEmpregadoFolgaDTO) {
    this.listEmpregadoFolgaDTO = listEmpregadoFolgaDTO;
  }
}