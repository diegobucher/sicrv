package br.gov.caixa.gitecsa.sicrv.dao;

import java.util.Date;
import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.sicrv.model.Ausencia;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;

public interface AusenciaEmpregadoDAO extends BaseDAO<Ausencia> {

	public List<Ausencia> consultar(String matricula, Ausencia ausencia);

	public Ausencia findByIdFetchAll(Long idAusenciaEmpregado);
	
	public List<Ausencia> validarPeriodoExistente(String matricula, Date dataInicial, Date dataFinal);

	public List<Ausencia> buscarAusenciasNoPeriodo(Empregado empregado, Date dataInicial, Date dataFinal);

	public List<Ausencia> buscarAusenciasEquipeNoDia(Equipe equipe, Date dataInicial);
	
  public List<Ausencia> buscarAusenciasEquipeNoPeriodo(Equipe equipe, Date dataInicio, Date dataFim);

  public List<Ausencia> buscarAusenciasUnidadeNoPeriodo(Unidade unidade, Date dataInicio, Date dataFim);
}
