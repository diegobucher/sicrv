package br.gov.caixa.gitecsa.sicrv.service;

import java.util.List;

import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.sicrv.dao.EquipeEmpregadoDAO;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeEmpregado;

public class EquipeEmpregadoService  extends AbstractService<EquipeEmpregado> {

  private static final long serialVersionUID = -65341386934427430L;
  
  @Inject
  private EquipeEmpregadoDAO equipeEmpregadoDAO;

  @Override
  public List<EquipeEmpregado> consultar(EquipeEmpregado entidade) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void validaCamposObrigatorios(EquipeEmpregado entity) {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void validaRegras(EquipeEmpregado entity) {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void validaRegrasExcluir(EquipeEmpregado entity) {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected BaseDAO<EquipeEmpregado> getDAO() {
    return equipeEmpregadoDAO;
  }

  public EquipeEmpregado obterEmpregadoOutrasEquipes(Long idEmpregado){
    return equipeEmpregadoDAO.obterEmpregadoOutrasEquipes(idEmpregado);
  }
  
  public void inativarEquipeEmpregadosPorEquipe(Equipe equipe) {
    equipeEmpregadoDAO.inativarEquipeEmpregadosPorEquipe(equipe);
  }

}
