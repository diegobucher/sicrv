package br.gov.caixa.gitecsa.sicrv.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.sicrv.dao.AtividadeDAO;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeAtividade;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;

public class AtividadeDAOImpl extends BaseDAOImpl<Atividade> implements AtividadeDAO {

  public List<Atividade> findAtividadesPai(Unidade unidade) {

    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT atividade ");
    hql.append(" FROM Atividade atividade ");
    hql.append(" WHERE atividade.ativo = true");
    hql.append(" AND atividade.atividadePai is null");
    hql.append(" AND atividade.unidade.id = :idUnidade");

    TypedQuery<Atividade> query = getEntityManager().createQuery(hql.toString(), Atividade.class);
    query.setParameter("idUnidade", unidade.getId());
    
    return query.getResultList();
  }
  
  public List<Atividade> findAtividadesPaiSemCurvaPadrao(Unidade unidade) {

    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT atividade ");
    hql.append(" FROM Atividade atividade ");
    hql.append(" WHERE atividade.ativo = true");
    hql.append(" AND atividade.atividadePai is null");
    hql.append(" AND atividade.unidade.id = :idUnidade");
    hql.append(" AND atividade.curvasPadrao IS EMPTY");

    TypedQuery<Atividade> query = getEntityManager().createQuery(hql.toString(), Atividade.class);
    query.setParameter("idUnidade", unidade.getId());
    
    return query.getResultList();
  }

  public List<Atividade> consultar(Atividade atividade) {

    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT DISTINCT atividade ");
    hql.append(" FROM Atividade atividade ");
    hql.append(" LEFT JOIN FETCH atividade.atividadeList atividadeFilhas");
    hql.append(" INNER JOIN atividade.unidade unidade ");
    hql.append(" WHERE atividade.atividadePai is null ");
    hql.append(" AND atividade.ativo = true ");
    hql.append(" AND (atividadeFilhas.ativo IS NULL OR atividadeFilhas.ativo = true )");
    hql.append(" AND unidade.id = :idUnidade ");

    if (StringUtils.isNotBlank(atividade.getNome())) {
      hql.append(" AND upper(atividade.nome) like :nome ");
    }
    
    if (!Util.isNullOuVazio(atividade.getPeriodicidade())) {
    	hql.append(" AND atividade.periodicidade = :periodicidade ");
	}
    
    hql.append(" ORDER BY atividade.nome ");

    TypedQuery<Atividade> query = getEntityManager().createQuery(hql.toString(), Atividade.class);

    query.setParameter("idUnidade", atividade.getUnidade().getId());
    if (StringUtils.isNotBlank(atividade.getNome())) {
      query.setParameter("nome", "%" + atividade.getNome().toUpperCase() + "%");
    }
    if (!Util.isNullOuVazio(atividade.getPeriodicidade())) {
    	query.setParameter("periodicidade", atividade.getPeriodicidade());
    }
    
    return query.getResultList();
  }

  public Atividade findAtividadeByNomeUnidade(String nomeAtividade, Unidade unidade) {

    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT atividade ");
    hql.append(" FROM Atividade atividade ");
    hql.append(" LEFT JOIN FETCH atividade.atividadeList ");
    if (unidade != null && unidade.getId() != null) {
    	hql.append(" LEFT JOIN FETCH atividade.unidade unidade ");
	}
    hql.append(" WHERE atividade.ativo = true");
    hql.append(" AND trim(upper(atividade.nome)) like :nome ");
    if (unidade != null && unidade.getId() != null) {
    	hql.append(" AND unidade.id = :idUnidade ");
    }
    TypedQuery<Atividade> query = getEntityManager().createQuery(hql.toString(), Atividade.class);

    query.setParameter("nome", "" + nomeAtividade.toUpperCase().trim() + "");
    if (unidade != null && unidade.getId() != null) {
    	query.setParameter("idUnidade", unidade.getId());
    }
    
    
    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }
  

  public List<Atividade> findAtividadesFilhas(Long idPai) {

    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT atividade ");
    hql.append(" FROM Atividade atividade ");
    hql.append(" WHERE atividade.ativo = true");
    hql.append(" AND atividade.atividadePai.id = :idPai ");

    TypedQuery<Atividade> query = getEntityManager().createQuery(hql.toString(), Atividade.class);

    query.setParameter("idPai", idPai);

    return query.getResultList();
  }

  public Atividade findByIdFetchAll(Long id) {

    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT atividade ");
    hql.append(" FROM Atividade atividade ");
    hql.append(" LEFT JOIN FETCH atividade.atividadeList ");
    hql.append(" LEFT JOIN FETCH atividade.atividadePai ");
    hql.append(" WHERE atividade.id = :id ");

    TypedQuery<Atividade> query = getEntityManager().createQuery(hql.toString(), Atividade.class);

    query.setParameter("id", id);

    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  @Override
  public List<EquipeAtividade> buscarEquipeAtividade(Atividade atividade) {
	  StringBuilder hql = new StringBuilder();
	  hql.append(" SELECT equipeAtividade ");
	  hql.append(" FROM EquipeAtividade equipeAtividade ");
	  hql.append(" INNER JOIN FETCH equipeAtividade.atividade atividade ");
	  hql.append(" INNER JOIN FETCH equipeAtividade.equipe equipe ");
	  hql.append(" WHERE atividade.id = :idAtividade ");
    	
	  TypedQuery<EquipeAtividade> query = getEntityManager().createQuery(hql.toString(), EquipeAtividade.class);
    	
	  query.setParameter("idAtividade", atividade.getId());
    	
      return query.getResultList();
  }
  
  @Override
  public List<EquipeAtividade> findEquipeAtividadeByEquipe(Equipe equipe) {
      
      StringBuilder hql = new StringBuilder();
      hql.append(" SELECT DISTINCT equipeAtividade ");
      hql.append(" FROM EquipeAtividade equipeAtividade ");
      hql.append(" INNER JOIN FETCH equipeAtividade.atividade atividade ");
      hql.append(" LEFT JOIN FETCH atividade.atividadePai atividadePai ");
      hql.append(" LEFT JOIN FETCH atividade.atividadeList atividadeList ");
      hql.append(" WHERE equipeAtividade.equipe.id = :idEquipe ");
        
      TypedQuery<EquipeAtividade> query = getEntityManager().createQuery(hql.toString(), EquipeAtividade.class);
        
      query.setParameter("idEquipe", equipe.getId());
        
      return query.getResultList();
  }

}
