package br.gov.caixa.gitecsa.sicrv.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.sicrv.dao.FeriadoDAO;
import br.gov.caixa.gitecsa.sicrv.model.Feriado;

public class FeriadoDAOImpl extends BaseDAOImpl<Feriado> implements FeriadoDAO {

  @SuppressWarnings("unchecked")
  public List<Feriado> buscarFeriadosNoPeriodo(Date dataInicio, Date dataFim) {

    StringBuilder hql = new StringBuilder();
    hql.append(" FROM Feriado feriado");
    hql.append(" WHERE Date(feriado.data) BETWEEN :dataIni AND :dataFim ");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("dataIni", dataInicio, TemporalType.DATE);
    query.setParameter("dataFim", dataFim, TemporalType.DATE);

    return query.getResultList();
  }

  @Override
  public Feriado buscarFeriadoNaData(Date data) {
    StringBuilder hql = new StringBuilder();
    hql.append(" FROM Feriado feriado");
    hql.append(" WHERE Date(feriado.data) = :data");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("data", data, TemporalType.DATE);

    try {
      return (Feriado) query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }
}
