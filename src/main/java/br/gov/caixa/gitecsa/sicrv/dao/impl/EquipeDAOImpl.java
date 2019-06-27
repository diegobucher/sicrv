package br.gov.caixa.gitecsa.sicrv.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.sicrv.dao.EquipeDAO;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;

public class EquipeDAOImpl extends BaseDAOImpl<Equipe> implements EquipeDAO {

  @SuppressWarnings("unchecked")
  public List<Equipe> consultar(Equipe equipe) {

    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT equipe ");
    hql.append(" FROM Equipe equipe ");
    hql.append(" LEFT JOIN FETCH equipe.unidade unidade ");
    hql.append(" WHERE equipe.ativo = :ativo ");
    if (!Util.isNullOuVazio(equipe.getId())) {
      hql.append(" AND equipe.id = :idEquipe ");
    }
    if (equipe.getUnidade() != null && !Util.isNullOuVazio(equipe.getUnidade().getId())) {
      hql.append(" AND unidade.id = :idUnidade ");
    }
    if (!Util.isNullOuVazio(equipe.getNome())) {
      hql.append(" AND trim(upper(equipe.nome)) like :nome ");
    }
    if (equipe.getPeriodicidade() != null) {
      hql.append(" AND equipe.periodicidade = :periodicidade ");
    }

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("ativo", Boolean.TRUE);

    if (!Util.isNullOuVazio(equipe.getId())) {
      query.setParameter("idEquipe", equipe.getId());
    }
    if (equipe.getUnidade() != null && !Util.isNullOuVazio(equipe.getUnidade().getId())) {
      query.setParameter("idUnidade", equipe.getUnidade().getId());
    }
    if (!Util.isNullOuVazio(equipe.getNome())) {
      query.setParameter("nome", "%" + equipe.getNome().toUpperCase().trim() + "%");
    }
    if (equipe.getPeriodicidade() != null) {
      query.setParameter("periodicidade", equipe.getPeriodicidade());
    }
    return query.getResultList();
  }

  public Equipe findByIdFetch(Long idEquipe) {
    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT equipe ");
    hql.append(" FROM Equipe equipe ");
    hql.append(" LEFT JOIN FETCH equipe.equipeAtividades atividades ");
    hql.append(" LEFT JOIN FETCH equipe.equipeEmpregados equipesEmp ");
    hql.append(" LEFT JOIN FETCH equipesEmp.empregado empregado ");
    hql.append(" LEFT JOIN FETCH atividades.atividade atividade ");
    hql.append(" WHERE equipe.id = :idEquipe ");
    hql.append(" AND (equipesEmp.ativo = true OR equipesEmp.ativo IS NULL )");
    hql.append(" AND (atividade.ativo = true OR atividade.ativo IS NULL )");

    TypedQuery<Equipe> query = getEntityManager().createQuery(hql.toString(), Equipe.class);
    query.setParameter("idEquipe", idEquipe);
    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public List<Equipe> obterEquipePorUnidade(Unidade unidade) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT distinct equipe ");
    hql.append(" FROM Equipe equipe ");
    hql.append(" LEFT JOIN FETCH equipe.unidade unidade ");
    hql.append(" LEFT JOIN FETCH equipe.equipeEmpregados equipes ");
    hql.append(" LEFT JOIN FETCH equipes.empregado empregado ");
    hql.append(" WHERE unidade.id = :idUnidade ");
    hql.append(" AND equipe.ativo = :ativo ");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idUnidade", unidade.getId());
    query.setParameter("ativo", Boolean.TRUE);

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<Equipe>();
    }
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public List<Equipe> consultarEquipePorEmpregado(Empregado empregado) {
    
    StringBuilder hql = new StringBuilder();
    
    hql.append(" SELECT equipe ");
    hql.append(" FROM Equipe equipe ");
    hql.append(" LEFT JOIN FETCH equipe.unidade unidade ");
    hql.append(" LEFT JOIN FETCH equipe.empregado empregado ");
    hql.append(" WHERE empregado.id = :idEmpregado ");
    hql.append(" AND equipe.ativo = :ativo ");
    
    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idEmpregado", empregado.getId());
    query.setParameter("ativo", Boolean.TRUE);
    
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<Equipe>();
    }
  }

  @Override
  public List<Equipe> obterEquipesAtivas() {
    
    StringBuilder hql = new StringBuilder();
    
    hql.append(" SELECT DISTINCT equipe ");
    hql.append(" FROM Equipe equipe ");
    hql.append(" LEFT JOIN FETCH equipe.equipeEmpregados equipesEmp ");
    hql.append(" LEFT JOIN FETCH equipesEmp.empregado empregado ");
    hql.append(" WHERE 1 = 1 ");
    hql.append(" AND (equipesEmp.ativo = true OR equipesEmp.ativo IS NULL )");
    hql.append(" AND equipe.ativo = true ");

    TypedQuery<Equipe> query = getEntityManager().createQuery(hql.toString(), Equipe.class);
    
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<Equipe>();
    }
  }

  @Override
  public List<Equipe> obterEquipesAtivasPorUnidade(Integer cgc) {
    
    StringBuilder hql = new StringBuilder();
    
    hql.append(" SELECT DISTINCT equipe ");
    hql.append(" FROM Equipe equipe ");
    hql.append(" LEFT JOIN FETCH equipe.equipeEmpregados equipesEmp ");
    hql.append(" LEFT JOIN FETCH equipesEmp.empregado empregado ");
    hql.append(" LEFT JOIN equipe.unidade unidade ");
    hql.append(" WHERE 1 = 1 ");
    hql.append(" AND (equipesEmp.ativo = true OR equipesEmp.ativo IS NULL )");
    hql.append(" AND equipe.ativo = true ");
    hql.append(" AND unidade.cgc = :cgcUnidade ");

    TypedQuery<Equipe> query = getEntityManager().createQuery(hql.toString(), Equipe.class);
    
    query.setParameter("cgcUnidade", cgc);
    
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<Equipe>();
    }
  }  
}
