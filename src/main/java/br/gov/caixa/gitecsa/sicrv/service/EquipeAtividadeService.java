package br.gov.caixa.gitecsa.sicrv.service;

import java.util.List;

import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.sicrv.dao.EquipeAtividadeDAO;
import br.gov.caixa.gitecsa.sicrv.model.EquipeAtividade;

public class EquipeAtividadeService  extends AbstractService<EquipeAtividade> {

  private static final long serialVersionUID = -65341386934427430L;
  
  @Inject
  private EquipeAtividadeDAO equipeAtividadeDAO;

  @Override
  public List<EquipeAtividade> consultar(EquipeAtividade entidade) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void validaCamposObrigatorios(EquipeAtividade entity) {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void validaRegras(EquipeAtividade entity) {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void validaRegrasExcluir(EquipeAtividade entity) {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected BaseDAO<EquipeAtividade> getDAO() {
    return equipeAtividadeDAO;
  }

}
