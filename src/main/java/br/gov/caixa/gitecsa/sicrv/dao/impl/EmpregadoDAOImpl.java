package br.gov.caixa.gitecsa.sicrv.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.sicrv.dao.EmpregadoDAO;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;
import br.gov.caixa.gitecsa.sicrv.service.FolgaService;

public class EmpregadoDAOImpl extends BaseDAOImpl<Empregado> implements EmpregadoDAO {
  
  @Override
  public Empregado obterEmpregadoPorMatricula(String matricula) {
    StringBuilder hql = new StringBuilder();

    hql.append(" FROM Empregado emp");
    hql.append(" WHERE upper(emp.matricula) = upper(:matricula) ");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("matricula", matricula);

    try {
      return (Empregado) query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  @Override
  public Empregado obterEmpregadoPorMatriculaUnidade(String matricula, Unidade unidade) {

    StringBuilder hql = new StringBuilder();
    Empregado empregado = null;

    hql.append(" SELECT DISTINCT emp ");
    hql.append(" FROM Empregado emp");
    hql.append(" INNER JOIN FETCH emp.equipesEmpregado equipesEmpregado");
    hql.append(" INNER JOIN FETCH equipesEmpregado.equipe equipe ");
    hql.append(" INNER JOIN FETCH equipe.unidade unidade ");
    hql.append(" WHERE lower(emp.matricula) = lower(:matricula) ");
    hql.append(" AND equipe.ativo = :ativo ");
    hql.append(" AND unidade.id = :idUnidade ");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("matricula", matricula);
    query.setParameter("ativo", Boolean.TRUE);
    query.setParameter("idUnidade", unidade.getId());

    try {
      empregado = (Empregado) query.getSingleResult();
    } catch (NoResultException e) {

    }
    return empregado;
  }

  @Override
  public Empregado findByIdFetch(Long id) {

    StringBuilder hql = new StringBuilder();
    Empregado empregado = null;

    hql.append(" SELECT emp ");
    hql.append(" FROM Empregado emp");
    hql.append(" INNER JOIN FETCH emp.equipesEmpregado equipesEmpregado");
    hql.append(" INNER JOIN FETCH equipesEmpregado.equipe equipe ");
    hql.append(" INNER JOIN FETCH equipe.unidade unidade ");
    hql.append(" WHERE emp.id = :id");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("id", id);

    try {
      empregado = (Empregado) query.getSingleResult();
    } catch (NoResultException e) {
    }

    return empregado;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Empregado> obterEmpregadosPorUnidade(Unidade unidade) {

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT emp ");
    hql.append(" FROM Empregado emp");
    hql.append(" INNER JOIN FETCH emp.equipesEmpregado equipesEmpregado");
    hql.append(" INNER JOIN FETCH equipesEmpregado.equipe equipe ");
    hql.append(" INNER JOIN FETCH equipe.unidade unidade ");
    hql.append(" WHERE 1=1 ");
    hql.append(" AND equipe.ativo = :ativo ");
    hql.append(" AND equipesEmpregado.ativo = :ativo ");
    hql.append(" AND unidade.id = :idUnidade ");
    hql.append(" ORDER BY emp.nome asc ");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("ativo", Boolean.TRUE);
    query.setParameter("idUnidade", unidade.getId());

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<Empregado>();
    }
  }

  @Override
  public Empregado atualizarLimiteFolgaEmpregado(Empregado empregado, Integer saldoFolga) {
    
    if(saldoFolga >= FolgaService.PARAM_QTD_MAX_FOLGA_ACUMULADA ){
      empregado.setMaximoFolga(true);
    } else if (saldoFolga <= FolgaService.PARAM_QTD_MIN_VOLTA_ESCALAR) {
      empregado.setMaximoFolga(false);
    }
    
    return update(empregado);
  }
  
  @Override
  public Boolean isEmpregadoSupervisorEmEquipeAtiva(Empregado empregado) {
    
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT count(equipeEmpregado) ");
    hql.append(" FROM EquipeEmpregado equipeEmpregado");
    hql.append(" INNER JOIN equipeEmpregado.empregado emp");
    hql.append(" WHERE emp.id = :idEmpregado ");
    hql.append(" AND equipeEmpregado.supervisor = true");
    hql.append(" AND equipeEmpregado.ativo = true");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idEmpregado", empregado.getId());

    Long contador = (Long) query.getSingleResult();

    if (contador > 0) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public List<Empregado> obterListaEmpregadosAtivosPorEquipe(Equipe equipe) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT emp ");                                   
    hql.append(" FROM Empregado emp ");                                   
    hql.append(" INNER JOIN FETCH emp.equipesEmpregado equipesEmpregado ");                                   
    hql.append(" INNER JOIN FETCH equipesEmpregado.equipe equipe ");                                   
    hql.append(" WHERE 1=1 ");                                   
    hql.append(" AND emp.ativo = true ");                                   
    hql.append(" AND equipe.id = :idEquipe ");                                   
    hql.append(" AND equipe.ativo = true ");                                   
    hql.append(" AND equipesEmpregado.ativo = true ");                                   
    hql.append(" ORDER BY emp.nome asc ");                                   

    TypedQuery<Empregado> query = getEntityManager().createQuery(hql.toString(), Empregado.class);
    
    query.setParameter("idEquipe", equipe.getId());

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<Empregado>();
    }
  }

}
