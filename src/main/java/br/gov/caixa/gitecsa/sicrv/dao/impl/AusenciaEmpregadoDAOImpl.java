package br.gov.caixa.gitecsa.sicrv.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.sicrv.dao.AusenciaEmpregadoDAO;
import br.gov.caixa.gitecsa.sicrv.model.Ausencia;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;

public class AusenciaEmpregadoDAOImpl extends BaseDAOImpl<Ausencia> implements AusenciaEmpregadoDAO {

  public List<Ausencia> consultar(String matricula, Ausencia ausencia) {

    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT ausencia ");
    hql.append(" FROM Ausencia ausencia ");
    hql.append(" INNER JOIN FETCH ausencia.empregado emp ");
    hql.append(" WHERE 1 = 1 ");
    if (!Util.isNullOuVazio(matricula)) {
      hql.append(" and emp.matricula = :matricula ");
    }

    // -------- Inicio das validações de data
    if (ausencia.getDataInicio() != null && ausencia.getDataFim() == null) {
      hql.append(" and (ausencia.dataInicio <= :dataInicio and ausencia.dataFim >= :dataInicio) ");
    }
    if (ausencia.getDataFim() != null && ausencia.getDataInicio() == null) {
      hql.append(" and (ausencia.dataInicio <= :dataFim and ausencia.dataFim >= :dataFim) ");
    }
    if (ausencia.getDataInicio() != null && ausencia.getDataFim() != null) {
      hql.append(" and ( ");
      hql.append(" (ausencia.dataInicio >= :dataInicio and ausencia.dataFim <= :dataFim) ");
      hql.append(" OR (ausencia.dataInicio >= :dataInicio and ausencia.dataFim <= :dataFim) ");
      hql.append(" OR (ausencia.dataInicio <= :dataInicio and ausencia.dataFim >= :dataFim) ");
      hql.append(" OR (ausencia.dataInicio <= :dataInicio and ausencia.dataFim >= :dataInicio) ");
      hql.append(" OR (ausencia.dataInicio <= :dataFim and ausencia.dataFim >= :dataFim) ");
      hql.append(" ) ");
    }
    // -------- Final das validações de data

    if (ausencia.getMotivo() != null) {
      hql.append(" and ausencia.motivo = :motivo ");
    }

    hql.append(" ORDER BY emp.nome, ausencia.dataInicio asc ");

    TypedQuery<Ausencia> query = getEntityManager().createQuery(hql.toString(), Ausencia.class);

    if (!Util.isNullOuVazio(matricula)) {
      query.setParameter("matricula", matricula);
    }
    if (ausencia.getDataInicio() != null) {
      query.setParameter("dataInicio", ausencia.getDataInicio());
    }
    if (ausencia.getDataFim() != null) {
      query.setParameter("dataFim", ausencia.getDataFim());
    }
    if (ausencia.getMotivo() != null) {
      query.setParameter("motivo", ausencia.getMotivo());
    }
    return query.getResultList();
  }

  @Override
  public Ausencia findByIdFetchAll(Long idAusenciaEmpregado) {
    StringBuilder hql = new StringBuilder();
    Ausencia ausencia = null;

    hql.append(" FROM Ausencia a");
    hql.append(" INNER JOIN FETCH a.empregado e");
    hql.append(" WHERE a.id = :idAusenciaEmpregado ");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idAusenciaEmpregado", idAusenciaEmpregado);

    try {
      ausencia = (Ausencia) query.getSingleResult();
    } catch (NoResultException e) {

    }
    return ausencia;
  }

  @Override
  public List<Ausencia> validarPeriodoExistente(String matricula, Date dataInicial, Date dataFinal) {

    StringBuilder hql = new StringBuilder();

    hql.append(" FROM Ausencia a");
    hql.append(" INNER JOIN FETCH a.empregado e");
    hql.append(" where e.matricula = :matricula ");
    hql.append(" and ( ");
    hql.append(" (a.dataInicio >= :dataInicial and a.dataFim <= :dataFinal) ");
    hql.append(" OR (a.dataInicio >= :dataInicial and a.dataFim <= :dataFinal) ");
    hql.append(" OR (a.dataInicio <= :dataInicial and a.dataFim >= :dataFinal) ");
    hql.append(" OR (a.dataInicio <= :dataInicial and a.dataFim >= :dataInicial) ");
    hql.append(" OR (a.dataInicio <= :dataFinal and a.dataFim >= :dataFinal) ");
    hql.append(" ) ");

    TypedQuery<Ausencia> query = getEntityManager().createQuery(hql.toString(), Ausencia.class);

    query.setParameter("matricula", matricula);
    query.setParameter("dataInicial", dataInicial);
    query.setParameter("dataFinal", dataFinal);

    return query.getResultList();
  }

  @Override
  public List<Ausencia> buscarAusenciasNoPeriodo(Empregado empregado, Date dataInicial, Date dataFinal) {

    StringBuilder hql = new StringBuilder();

    hql.append(" FROM Ausencia a");
    hql.append(" INNER JOIN FETCH a.empregado e");
    hql.append(" where e.id = :idEmpregado ");
    hql.append(" and a.dataInicio <= :dataFinal and a.dataFim >= :dataInicial ");

    TypedQuery<Ausencia> query = getEntityManager().createQuery(hql.toString(), Ausencia.class);

    query.setParameter("idEmpregado", empregado.getId());
    query.setParameter("dataInicial", dataInicial);
    query.setParameter("dataFinal", dataFinal);

    return query.getResultList();
  }

  @Override
  public List<Ausencia> buscarAusenciasEquipeNoDia(Equipe equipe, Date dataInicial) {
    
    StringBuilder hql = new StringBuilder();
    
    hql.append(" SELECT a");
    hql.append(" FROM Ausencia a");
    hql.append(" INNER JOIN FETCH a.empregado e");
    hql.append(" INNER JOIN e.equipesEmpregado equipesEmp");
    hql.append(" INNER JOIN equipesEmp.equipe equipe");
    hql.append(" WHERE equipe.id = :idEquipe ");
    hql.append(" AND equipesEmp.ativo = TRUE ");
    hql.append(" AND a.dataInicio <= :dataInicial and a.dataFim >= :dataInicial ");
    
    TypedQuery<Ausencia> query = getEntityManager().createQuery(hql.toString(), Ausencia.class);
    
    query.setParameter("idEquipe", equipe.getId());
    query.setParameter("dataInicial", dataInicial);
    
    return query.getResultList();
  }
  
  @Override
  public List<Ausencia> buscarAusenciasEquipeNoPeriodo(Equipe equipe, Date dataInicio, Date dataFim) {

    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT ausencia ");
    hql.append(" FROM Ausencia ausencia ");
    hql.append(" INNER JOIN FETCH ausencia.empregado ");
    hql.append(" WHERE ausencia.empregado.id IN ( ");
    hql.append("  SELECT DISTINCT emp.id FROM Equipe equipe  ");
    hql.append("     INNER JOIN equipe.equipeEmpregados equipEmp INNER JOIN equipEmp.empregado emp  ");
    hql.append("  WHERE equipe.id = :idEquipe AND equipEmp.ativo = TRUE AND emp.ativo = TRUE ");
    hql.append("  ) ");
    hql.append(" AND ausencia.dataInicio <= :dataFim AND ausencia.dataFim >= :dataIni  ");

    TypedQuery<Ausencia> query = getEntityManager().createQuery(hql.toString(), Ausencia.class);
    query.setParameter("idEquipe", equipe.getId());
    query.setParameter("dataIni", dataInicio, TemporalType.DATE);
    query.setParameter("dataFim", dataFim, TemporalType.DATE);

    return query.getResultList();
  }
  
  @Override
  public List<Ausencia> buscarAusenciasUnidadeNoPeriodo(Unidade unidade, Date dataInicio, Date dataFim) {
    
    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT ausencia ");
    hql.append(" FROM Ausencia ausencia ");
    hql.append(" INNER JOIN FETCH ausencia.empregado empregado");
    hql.append(" WHERE ausencia.empregado.id IN ( ");
    hql.append("  SELECT DISTINCT emp.id FROM Equipe equipe  ");
    hql.append("     INNER JOIN equipe.equipeEmpregados equipEmp");
    hql.append("     INNER JOIN equipEmp.empregado emp  ");
    hql.append("     INNER JOIN equipEmp.equipe equip  ");
    hql.append("     INNER JOIN equip.unidade unidade ");
    hql.append("  WHERE unidade.id = :idUnidade AND equipEmp.ativo = TRUE AND emp.ativo = TRUE AND equipe.ativo = TRUE");
    hql.append("  ) ");
    hql.append(" AND ausencia.dataInicio <= :dataFim AND ausencia.dataFim >= :dataIni  ");
    
    TypedQuery<Ausencia> query = getEntityManager().createQuery(hql.toString(), Ausencia.class);
    query.setParameter("idUnidade", unidade.getId());
    query.setParameter("dataIni", dataInicio, TemporalType.DATE);
    query.setParameter("dataFim", dataFim, TemporalType.DATE);
    
    return query.getResultList();
  }
}
