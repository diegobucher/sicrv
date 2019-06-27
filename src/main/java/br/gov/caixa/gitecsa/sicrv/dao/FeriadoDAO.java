package br.gov.caixa.gitecsa.sicrv.dao;

import java.util.Date;
import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.sicrv.model.Feriado;

public interface FeriadoDAO extends BaseDAO<Feriado> {
  
  public List<Feriado> buscarFeriadosNoPeriodo(Date dataInicio, Date dataFim);
  
  public Feriado buscarFeriadoNaData(Date data);
	
}
