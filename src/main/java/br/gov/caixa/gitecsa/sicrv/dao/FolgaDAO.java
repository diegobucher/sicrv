package br.gov.caixa.gitecsa.sicrv.dao;

import java.util.Date;
import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.sicrv.enumerator.SituacaoFolgaEnum;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.Folga;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;

public interface FolgaDAO extends BaseDAO<Folga> {
	
  public List<Folga> buscarFolgasNoPeriodo(Empregado empregado, Date dataInicio, Date dataFim, List<SituacaoFolgaEnum> situacaoList);

  public List<Folga> buscarFolgasAPartir(Empregado empregado, Date dataInicio,  List<SituacaoFolgaEnum> situacaoList);

  public Long calcularSaldoFolga(Empregado empregado, Date dataLimite);

  public Boolean isEmpregadoComFolgaMaximaAcumulada(Empregado empregado, Date dataLimite);

  public void deletarFolgasEquipeEscalaGeradaApartirData(Equipe equipe, Date dataInicio);

  public void deletarFolgasEmpregadoEscalaGeradaApartirData(Empregado empregado, Date dataInicio);

  public void deletarFolgasEmpregadoEscalaGeradaNoPeriodo(Empregado empregado, Date dataInicio, Date dataFim);

  public List<Date> buscarDatasFolgasAPartir(Equipe equipe, Date dataInicio, Date dataFim, List<SituacaoFolgaEnum> situacaoList);

  public List<Folga> buscarFolgasEquipeNoPeriodo(Equipe equipe, Date dataInicio, Date dataFim, List<SituacaoFolgaEnum> situacaoList);

  public List<Folga> buscarFolgasUnidadeNoPeriodo(Unidade unidade, Date dataInicio, Date dataFim,
      List<SituacaoFolgaEnum> situacaoList);

}
