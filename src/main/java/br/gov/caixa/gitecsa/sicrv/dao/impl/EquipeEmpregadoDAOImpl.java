package br.gov.caixa.gitecsa.sicrv.dao.impl;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.sicrv.dao.EquipeEmpregadoDAO;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeEmpregado;

public class EquipeEmpregadoDAOImpl extends BaseDAOImpl<EquipeEmpregado> implements EquipeEmpregadoDAO {

  @Override
  public EquipeEmpregado obterEmpregadoOutrasEquipes(Long idEmpregado) {

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT equipeEmpregado ");
    hql.append(" FROM EquipeEmpregado equipeEmpregado ");
    hql.append(" INNER JOIN FETCH equipeEmpregado.empregado empregado ");
    hql.append(" INNER JOIN FETCH equipeEmpregado.equipe equipe ");
    hql.append(" WHERE 1=1 ");
    hql.append(" AND equipe.ativo = true ");
    hql.append(" AND empregado.ativo = true ");
    hql.append(" AND equipeEmpregado.ativo = true ");
    hql.append(" AND empregado.id = :idEmpregado ");

    Query query = getEntityManager().createQuery(hql.toString());

    query.setParameter("idEmpregado", idEmpregado);

    try {
      return (EquipeEmpregado) query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  @Override
  public void inativarEquipeEmpregadosPorEquipe(Equipe equipe) {
    StringBuilder hql = new StringBuilder();

    hql.append(" UPDATE EquipeEmpregado equipeEmpregado ");
    hql.append(" SET equipeEmpregado.ativo = false ");
    hql.append(" WHERE equipeEmpregado.equipe.id = :idEquipe ");

    Query query = getEntityManager().createQuery(hql.toString());

    query.setParameter("idEquipe", equipe.getId());

    query.executeUpdate();
  }

}
