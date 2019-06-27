package br.gov.caixa.gitecsa.sicrv.dao;

import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;

public interface EmpregadoDAO extends BaseDAO<Empregado> {

  public Empregado obterEmpregadoPorMatricula(String matricula);

  public Empregado obterEmpregadoPorMatriculaUnidade(String matricula, Unidade unidade);

  public List<Empregado> obterEmpregadosPorUnidade(Unidade unidade);

  public Empregado findByIdFetch(Long id);

  public Boolean isEmpregadoSupervisorEmEquipeAtiva(Empregado empregado);

  public Empregado atualizarLimiteFolgaEmpregado(Empregado empregado, Integer saldoFolga);

  public List<Empregado> obterListaEmpregadosAtivosPorEquipe(Equipe equipe);

}
