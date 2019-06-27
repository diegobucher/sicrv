package br.gov.caixa.gitecsa.sicrv.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.sicrv.dao.CurvaPadraoDAO;
import br.gov.caixa.gitecsa.sicrv.enumerator.PeriodoEnum;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.CurvaPadrao;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;

public class CurvaPadraoDAOImpl extends BaseDAOImpl<CurvaPadrao> implements CurvaPadraoDAO {

  @Override
  public List<CurvaPadrao> buscarPorAtividadeSubAtividade(Atividade atividade) {

    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT curvaPadrao ");
    hql.append(" FROM CurvaPadrao curvaPadrao ");
    hql.append(" INNER JOIN FETCH curvaPadrao.atividade a");
    hql.append(" WHERE curvaPadrao.ic_ativo = true ");
    hql.append(" and curvaPadrao.atividade = :atividade ");

    TypedQuery<CurvaPadrao> query = getEntityManager().createQuery(hql.toString(), CurvaPadrao.class);

    query.setParameter("atividade", atividade);

    return query.getResultList();
  }

  @Override
  public List<CurvaPadrao> buscarPorEquipe(Equipe equipe) {

    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT distinct curvaPadrao ");
    hql.append(" FROM CurvaPadrao curvaPadrao   ");
    hql.append(" INNER JOIN FETCH curvaPadrao.atividade atividade  ");
    hql.append(" LEFT JOIN FETCH atividade.atividadePai atividadepai  ");
    hql.append(" LEFT JOIN atividade.equipeAtividades equipAtiv  ");
    hql.append(" LEFT JOIN atividadepai.equipeAtividades equipAtivPai  ");
    hql.append(" LEFT JOIN equipAtiv.equipe equipe  ");
    hql.append(" LEFT JOIN equipAtivPai.equipe equipePai  ");
    hql.append(" WHERE curvaPadrao.ic_ativo = true   ");
    hql.append(" and (equipe.id = :idEquipe or equipePai.id = :idEquipe) ");

    TypedQuery<CurvaPadrao> query = getEntityManager().createQuery(hql.toString(), CurvaPadrao.class);

    query.setParameter("idEquipe", equipe.getId());

    return query.getResultList();
  }

  @Override
  public CurvaPadrao findByIdFetchAll(Long idCurvaPadrao) {

    StringBuilder hql = new StringBuilder();
    CurvaPadrao curvaPadrao = null;

    hql.append(" FROM CurvaPadrao c");
    hql.append(" INNER JOIN FETCH c.atividade a");
    hql.append(" WHERE c.ic_ativo = true ");
    hql.append(" and c.id = :idCurvaPadrao ");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idCurvaPadrao", idCurvaPadrao);

    try {
      curvaPadrao = (CurvaPadrao) query.getSingleResult();
    } catch (NoResultException e) {

    }
    return curvaPadrao;
  }

  @Override
  public List<CurvaPadrao> listaGarantirUnicidade(CurvaPadrao curva) {
    List<CurvaPadrao> resultado;
    StringBuilder hql = new StringBuilder();

    hql.append(" FROM CurvaPadrao c");
    hql.append(" INNER JOIN FETCH c.atividade a");
    hql.append(" WHERE c.ic_ativo = true");
    hql.append(" and c.atividade = :atividade");
    hql.append(" and c.periodo = :periodo ");
    if (PeriodoEnum.DIA_DA_SEMANA.equals(curva.getPeriodo())) {
      hql.append(" and c.dia = :dia ");
    } else {
      hql.append(" and c.dataEspecifica = :data ");
    }
    hql.append(" and (c.horaInicial = :inicio and c.horaFinal = :fim) ");

    TypedQuery<CurvaPadrao> query = getEntityManager().createQuery(hql.toString(), CurvaPadrao.class);

    query.setParameter("atividade", curva.getAtividade());
    query.setParameter("periodo", curva.getPeriodo());
    if (PeriodoEnum.DIA_DA_SEMANA.equals(curva.getPeriodo())) {
      query.setParameter("dia", curva.getDia());
    } else {
      query.setParameter("data", curva.getDataEspecifica());
    }
    query.setParameter("inicio", curva.getHoraInicial());
    query.setParameter("fim", curva.getHoraFinal());

    try {
      resultado = query.getResultList();
      return resultado;
    } catch (NoResultException e) {
      return null;
    }
  }
}