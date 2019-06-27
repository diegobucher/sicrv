package br.gov.caixa.gitecsa.sicrv.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.sicrv.dao.EmpregadoDAO;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;

@Stateless
public class EmpregadoService extends AbstractService<Empregado> {

  @Inject
  private EmpregadoDAO empregadoDAO;

  private static final long serialVersionUID = 8549855484094535815L;

  public Empregado obterEmpregadoPorMatricula(String matricula) {
    return empregadoDAO.obterEmpregadoPorMatricula(matricula);
  }

  public Empregado obterEmpregadoPorMatriculaUnidade(String matricula, Unidade unidade) {
    return empregadoDAO.obterEmpregadoPorMatriculaUnidade(matricula, unidade);
  }

  public List<Empregado> obterEmpregadosPorUnidade(Unidade unidade) {
    return empregadoDAO.obterEmpregadosPorUnidade(unidade);
  }

  @Override
  public List<Empregado> consultar(Empregado entidade) throws Exception {
    return null;
  }

  @Override
  protected void validaCamposObrigatorios(Empregado entity) {

  }

  @Override
  protected void validaRegras(Empregado entity) {

  }

  @Override
  protected void validaRegrasExcluir(Empregado entity) {

  }

  @Override
  protected BaseDAO<Empregado> getDAO() {
    return empregadoDAO;
  }

  public Empregado findByIdFetch(Long id) {
    return empregadoDAO.findByIdFetch(id);
  }
  
  public Boolean isEmpregadoSupervisorEmEquipeAtiva(Empregado empregado) {
    return empregadoDAO.isEmpregadoSupervisorEmEquipeAtiva(empregado);
  }

  public List<Empregado> obterListaEmpregadosAtivosPorEquipe(Equipe equipe) {
    return empregadoDAO.obterListaEmpregadosAtivosPorEquipe(equipe);
  }
}
