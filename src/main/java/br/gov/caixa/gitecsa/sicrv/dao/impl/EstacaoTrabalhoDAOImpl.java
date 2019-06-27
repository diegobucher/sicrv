package br.gov.caixa.gitecsa.sicrv.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.sicrv.dao.EstacaoTrabalhoDAO;
import br.gov.caixa.gitecsa.sicrv.model.EstacaoTrabalho;

public class EstacaoTrabalhoDAOImpl extends BaseDAOImpl<EstacaoTrabalho> implements EstacaoTrabalhoDAO {

  @Override
  public EstacaoTrabalho buscarPorNome(String nome) {
    StringBuilder hql = new StringBuilder();
    hql.append(" FROM EstacaoTrabalho estacao ");
    hql.append(" WHERE trim(upper(estacao.nome)) like :nome");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("nome", nome.trim().toUpperCase());
    query.setMaxResults(1);

    try {
      return (EstacaoTrabalho) query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  @Override
  public List<EstacaoTrabalho> findAllAtivas() {
    StringBuilder hql = new StringBuilder();
    hql.append(" FROM EstacaoTrabalho estacao ");
    hql.append(" WHERE estacao.ativa = true");

    TypedQuery<EstacaoTrabalho> query = getEntityManager().createQuery(hql.toString(), EstacaoTrabalho.class);

    return query.getResultList();
  }
}
