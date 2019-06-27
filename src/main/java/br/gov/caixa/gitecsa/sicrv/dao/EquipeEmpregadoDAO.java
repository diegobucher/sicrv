package br.gov.caixa.gitecsa.sicrv.dao;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeEmpregado;

public interface EquipeEmpregadoDAO extends BaseDAO<EquipeEmpregado> {

  EquipeEmpregado obterEmpregadoOutrasEquipes(Long idEmpregado);

  void inativarEquipeEmpregadosPorEquipe(Equipe equipe);

}
