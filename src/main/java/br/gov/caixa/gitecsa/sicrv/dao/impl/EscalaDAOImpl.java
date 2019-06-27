package br.gov.caixa.gitecsa.sicrv.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.time.DateUtils;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.sicrv.dao.EscalaDAO;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.Escala;

public class EscalaDAOImpl extends BaseDAOImpl<Escala> implements EscalaDAO {

  @Override
  public void salvarNovasEscalas(Collection<Escala> escalaList) {

    int contador = 1;

    for (Escala escala : escalaList) {
      
      if(escala.getId() == null){
        this.getEntityManager().persist(escala);
        
        // a cada 100 objetos, faz a sincronizacao e limpa o cache
        if (contador % 100 == 0) {
          this.getEntityManager().flush();
          this.getEntityManager().clear();
        }
        contador++;
      }

    }

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Escala> obterEscalasPorEquipeEPeriodo(Long idEquipe, Date dataInicial, Date dataFinal) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT esc ");
    hql.append(" FROM Escala esc");
    hql.append(" INNER JOIN FETCH esc.equipeEmpregado equipeEmpregado");
    hql.append(" INNER JOIN FETCH equipeEmpregado.equipe equipe ");
    hql.append(" INNER JOIN FETCH equipeEmpregado.empregado empregado ");
    hql.append(" WHERE equipe.id = :idEquipe ");
    hql.append(" AND Date(esc.inicio) BETWEEN  :dataInicial AND :dataFinal ");

    hql.append(" ORDER BY esc.id asc ");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idEquipe", idEquipe);
    query.setParameter("dataInicial", dataInicial);
    query.setParameter("dataFinal", dataFinal);

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<Escala>();
    }
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public List<Escala> obterEscalasPorEquipeEData(Long idEquipe, Date data) {
    StringBuilder hql = new StringBuilder();
    
    hql.append(" SELECT esc ");
    hql.append(" FROM Escala esc");
    hql.append(" INNER JOIN FETCH esc.equipeEmpregado equipeEmpregado");
    hql.append(" INNER JOIN FETCH equipeEmpregado.equipe equipe ");
    hql.append(" INNER JOIN FETCH equipeEmpregado.empregado empregado ");
    hql.append(" WHERE equipe.id = :idEquipe ");
    hql.append(" AND Date(esc.inicio) =  :data ");
    hql.append(" ORDER BY esc.id asc ");
    
    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idEquipe", idEquipe);
    query.setParameter("data", data);
    
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<Escala>();
    }
  }

  @Override
  public List<Escala> obterEscalasPorEmpregadoEDatas(Empregado empregado, List<Date> dataList) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT esc ");
    hql.append(" FROM Escala esc");
    hql.append(" INNER JOIN esc.equipeEmpregado equipeEmpregado");
    hql.append(" INNER JOIN equipeEmpregado.empregado empregado ");
    hql.append(" WHERE empregado.id = :idEmpregado ");
    hql.append(" AND Date(esc.inicio) in :dataList ");

    hql.append(" ORDER BY esc.id asc ");

    TypedQuery<Escala> query = getEntityManager().createQuery(hql.toString(), Escala.class);
    query.setParameter("idEmpregado", empregado.getId());
    query.setParameter("dataList", dataList);

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<Escala>();
    }
  }

  @Override
  public List<Escala> obterEscalasPorPeriodo(Date dataInicial, Date dataFinal) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT esc ");
    hql.append(" FROM Escala esc");
    hql.append(" WHERE Date(esc.inicio) BETWEEN  :dataInicial AND :dataFinal ");
    hql.append(" ORDER BY esc.id ASC ");

    TypedQuery<Escala> query = getEntityManager().createQuery(hql.toString(), Escala.class);
    query.setParameter("dataInicial", dataInicial, TemporalType.DATE);
    query.setParameter("dataFinal", dataFinal, TemporalType.DATE);

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<Escala>();
    }

  }

  @Override
  public List<Escala> obterEscalasPorEmpregadoEPeriodo(Empregado empregado, Date dataInicial, Date dataFinal) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT esc ");
    hql.append(" FROM Escala esc");
    hql.append(" INNER JOIN esc.equipeEmpregado equipeEmp");
    hql.append(" INNER JOIN equipeEmp.empregado emp");
    hql.append(" WHERE Date(esc.inicio) BETWEEN  :dataInicial AND :dataFinal ");
    hql.append(" AND emp.id = :idEmpregado");
    hql.append(" ORDER BY esc.id ASC ");

    TypedQuery<Escala> query = getEntityManager().createQuery(hql.toString(), Escala.class);
    query.setParameter("dataInicial", dataInicial, TemporalType.DATE);
    query.setParameter("dataFinal", dataFinal, TemporalType.DATE);
    query.setParameter("idEmpregado", empregado.getId());

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<Escala>();
    }

  }

  @Override
  public Object[] obterDatasPeriodoEscalasFuturasPorEquipe(Long idEquipe) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT MIN(esc.inicio), MAX(esc.inicio) ");
    hql.append(" FROM Escala esc");
    hql.append(" INNER JOIN esc.equipeEmpregado equipeEmpregado");
    hql.append(" INNER JOIN equipeEmpregado.equipe equipe ");
    hql.append(" WHERE equipe.id = :idEquipe ");
    hql.append(" AND Date(esc.inicio)> current_date ");

    Query query = getEntityManager().createQuery(hql.toString());

    query.setParameter("idEquipe", idEquipe);

    return (Object[]) query.getSingleResult();
  }
  
  @Override
  public Object[] obterDatasPeriodoEscalasFuturasPorEquipeInclusoHoje(Long idEquipe) {
      StringBuilder hql = new StringBuilder();
      
      hql.append(" SELECT MIN(esc.inicio), MAX(esc.inicio) ");
      hql.append(" FROM Escala esc");
      hql.append(" INNER JOIN esc.equipeEmpregado equipeEmpregado");
      hql.append(" INNER JOIN equipeEmpregado.equipe equipe ");
      hql.append(" WHERE equipe.id = :idEquipe ");
      hql.append(" AND Date(esc.inicio)>= current_date ");
      
      Query query = getEntityManager().createQuery(hql.toString());
      
      query.setParameter("idEquipe", idEquipe);
      
      return (Object[]) query.getSingleResult();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Escala> obterEscalasFuturasPorEquipe(Long idEquipe, Date dataInicial) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT esc ");
    hql.append(" FROM Escala esc");
    hql.append(" INNER JOIN FETCH esc.equipeEmpregado equipeEmpregado");
    hql.append(" INNER JOIN FETCH equipeEmpregado.equipe equipe ");
    hql.append(" INNER JOIN FETCH equipeEmpregado.empregado empregado ");
    hql.append(" WHERE equipe.id = :idEquipe ");
    hql.append(" AND Date(esc.inicio) >= :dataInicial");

    hql.append(" ORDER BY esc.id asc ");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idEquipe", idEquipe);
    query.setParameter("dataInicial", dataInicial);

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<Escala>();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Escala> obterEscalasFuturasPorEmpregado(Long idEmpregado, Date dataInicial) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT esc ");
    hql.append(" FROM Escala esc");
    hql.append(" INNER JOIN FETCH esc.equipeEmpregado equipeEmpregado");
    hql.append(" INNER JOIN FETCH equipeEmpregado.equipe equipe ");
    hql.append(" INNER JOIN FETCH equipeEmpregado.empregado empregado ");
    hql.append(" WHERE empregado.id = :idEmpregado ");
    hql.append(" AND Date(esc.inicio) >= :dataInicial");

    hql.append(" ORDER BY esc.id asc ");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idEmpregado", idEmpregado);
    query.setParameter("dataInicial", dataInicial);

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<Escala>();
    }
  }

  @Override
  public Boolean existeEscalasFuturasPorEquipe(Long idEquipe, Date dataInicial) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT count(esc) ");
    hql.append(" FROM Escala esc");
    hql.append(" INNER JOIN esc.equipeEmpregado equipeEmpregado");
    hql.append(" INNER JOIN equipeEmpregado.equipe equipe ");
    hql.append(" INNER JOIN equipeEmpregado.empregado empregado ");
    hql.append(" WHERE equipe.id = :idEquipe ");
    hql.append(" AND Date(esc.inicio) >= :dataInicial");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idEquipe", idEquipe);
    query.setParameter("dataInicial", dataInicial);

    Long contador = (Long) query.getSingleResult();

    if (contador > 0) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void deletarEscalasEquipeApartirData(Equipe equipe, Date dataInicio) {

    StringBuilder hql = new StringBuilder();

    hql.append(" DELETE ");
    hql.append(" FROM Escala escala ");
    hql.append(" WHERE escala IN ( ");
    hql.append("    SELECT escala FROM Escala escala ");
    hql.append("    INNER JOIN escala.equipeEmpregado equipeEmp ");
    hql.append("    INNER JOIN equipeEmp.equipe equipe ");
    hql.append("    WHERE equipe.id = :idEquipe ");
    hql.append("    AND Date(escala.inicio) >= :dataInicial ");
    hql.append(" )");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idEquipe", equipe.getId());
    query.setParameter("dataInicial", dataInicio);

    query.executeUpdate();
  }

  @Override
  public Boolean existeEscalasFuturasPorEmpregado(Long idEmpregado, Date dataInicial) {

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT count(esc) ");
    hql.append(" FROM Escala esc");
    hql.append(" INNER JOIN esc.equipeEmpregado equipeEmpregado");
    hql.append(" INNER JOIN equipeEmpregado.equipe equipe ");
    hql.append(" INNER JOIN equipeEmpregado.empregado empregado ");
    hql.append(" WHERE empregado.id = :idEmpregado ");
    hql.append(" AND Date(esc.inicio) >= :dataInicial");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idEmpregado", idEmpregado);
    query.setParameter("dataInicial", dataInicial, TemporalType.DATE);

    Long contador = (Long) query.getSingleResult();

    if (contador > 0) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void deletarEscalasEmpregadoApartirData(Empregado empregado, Date dataInicio) {
    StringBuilder hql = new StringBuilder();

    hql.append(" DELETE ");
    hql.append(" FROM Escala escala ");
    hql.append(" WHERE escala IN ( ");
    hql.append("    SELECT escala FROM Escala escala ");
    hql.append("    INNER JOIN escala.equipeEmpregado equipeEmp ");
    hql.append("    INNER JOIN equipeEmp.equipe equipe ");
    hql.append("    INNER JOIN equipeEmp.empregado empregado ");
    hql.append("    WHERE empregado.id = :idEmpregado ");
    hql.append("    AND Date(escala.inicio) >= :dataInicial ");
    hql.append(" )");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idEmpregado", empregado.getId());
    query.setParameter("dataInicial", dataInicio);

    query.executeUpdate();
  }

  @Override
  public void deletarEscalasEmpregadoNoPeriodo(Empregado empregado, Date dataInicio, Date dataFim) {
    StringBuilder hql = new StringBuilder();

    hql.append(" DELETE ");
    hql.append(" FROM Escala escala ");
    hql.append(" WHERE escala IN ( ");
    hql.append("    SELECT escala FROM Escala escala ");
    hql.append("    INNER JOIN escala.equipeEmpregado equipeEmp ");
    hql.append("    INNER JOIN equipeEmp.equipe equipe ");
    hql.append("    INNER JOIN equipeEmp.empregado empregado ");
    hql.append("    WHERE empregado.id = :idEmpregado ");
    hql.append("    AND Date(escala.inicio) BETWEEN :dataInicial AND :dataFim");
    hql.append(" )");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idEmpregado", empregado.getId());
    query.setParameter("dataInicial", dataInicio, TemporalType.DATE);
    query.setParameter("dataFim", dataFim, TemporalType.DATE);

    query.executeUpdate();
  }

  @Override
  public Boolean existeEscalasFuturasPorEmpregadoPorPeriodo(Long idEmpregado, Date dataInicial, Date dataFinal) {

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT count(esc) ");
    hql.append(" FROM Escala esc");
    hql.append(" INNER JOIN esc.equipeEmpregado equipeEmpregado");
    hql.append(" INNER JOIN equipeEmpregado.equipe equipe ");
    hql.append(" INNER JOIN equipeEmpregado.empregado empregado ");
    hql.append(" WHERE empregado.id = :idEmpregado ");
    hql.append(" AND Date(esc.inicio) BETWEEN :dataInicial AND :dataFinal");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idEmpregado", idEmpregado);
    query.setParameter("dataInicial", dataInicial, TemporalType.DATE);
    query.setParameter("dataFinal", dataFinal, TemporalType.DATE);

    Long contador = (Long) query.getSingleResult();

    if (contador > 0) {
      return true;
    } else {
      return false;
    }
  }
  
  @Override
  public Boolean existeEscalasPorEquipePorPeriodo(Long idEquipe, Date dataInicial, Date dataFinal) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT count(esc) ");
    hql.append(" FROM Escala esc");
    hql.append(" INNER JOIN esc.equipeEmpregado equipeEmpregado");
    hql.append(" INNER JOIN equipeEmpregado.equipe equipe ");
    hql.append(" INNER JOIN equipeEmpregado.empregado empregado ");
    hql.append(" WHERE equipe.id = :idEquipe ");
    hql.append(" AND Date(esc.inicio) BETWEEN :dataInicial AND :dataFinal");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idEquipe", idEquipe);
    query.setParameter("dataInicial", dataInicial, TemporalType.DATE);
    query.setParameter("dataFinal", dataFinal, TemporalType.DATE);

    Long contador = (Long) query.getSingleResult();

    if (contador > 0) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public Escala findByIdFetch(Long idEscala) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT esc ");
    hql.append(" FROM Escala esc");
    hql.append(" INNER JOIN FETCH esc.equipeEmpregado equipeEmpregado");
    hql.append(" INNER JOIN FETCH equipeEmpregado.empregado empregado ");
    hql.append(" WHERE esc.id = :id");

    TypedQuery<Escala> query = getEntityManager().createQuery(hql.toString(), Escala.class);
    query.setParameter("id", idEscala);

    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }
  
  @Override
  public Escala obterEscalaPorEmpregadoNoDia(Empregado empregado, Date data) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT esc ");
    hql.append(" FROM Escala esc");
    hql.append(" INNER JOIN esc.equipeEmpregado equipeEmp");
    hql.append(" INNER JOIN equipeEmp.empregado emp");
    hql.append(" WHERE Date(esc.inicio) = :data");
    hql.append(" AND emp.id = :idEmpregado");

    TypedQuery<Escala> query = getEntityManager().createQuery(hql.toString(), Escala.class);
    query.setParameter("data", data, TemporalType.DATE);
    query.setParameter("idEmpregado", empregado.getId());

    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }

  }
  
  @Override
  public List<Date> buscarDatasFolgasAPartir(Equipe equipe, Date dataInicio, Date dataFim) {
    
    List<Date> datasTruncadasList = new ArrayList<Date>();
    
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT esc.inicio ");
    hql.append(" FROM Escala esc");
    hql.append(" INNER JOIN esc.equipeEmpregado equipeEmpregado");
    hql.append(" INNER JOIN equipeEmpregado.equipe equipe ");
    hql.append(" INNER JOIN equipeEmpregado.empregado empregado ");
    hql.append(" WHERE equipe.id = :idEquipe ");
    hql.append(" AND Date(esc.inicio) BETWEEN :dataIni AND :dataFim ");
    
    TypedQuery<Date> query = getEntityManager().createQuery(hql.toString(), Date.class);
    query.setParameter("idEquipe", equipe.getId());
    query.setParameter("dataIni", dataInicio, TemporalType.DATE);
    query.setParameter("dataFim", dataFim, TemporalType.DATE);
    
    List<Date> dataList = query.getResultList();
    
    if(dataList != null){
      for (Date data : dataList) {
        Date dataTruncada = DateUtils.truncate(data, Calendar.DAY_OF_MONTH);
        if(!datasTruncadasList.contains(dataTruncada)){
          datasTruncadasList.add(dataTruncada);
        }
      }
    }
    
    return datasTruncadasList;
  }
  
  @Override
  public Date getMaiorDataEscaladaPorEquipe(Equipe equipe) {
    
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT MAX(esc.inicio) ");
    hql.append(" FROM Escala esc");
    hql.append(" INNER JOIN esc.equipeEmpregado equipeEmpregado");
    hql.append(" INNER JOIN equipeEmpregado.equipe equipe ");
    hql.append(" WHERE equipe.id = :idEquipe ");
    
    TypedQuery<Date> query = getEntityManager().createQuery(hql.toString(), Date.class);
    query.setParameter("idEquipe", equipe.getId());
    
    return query.getSingleResult();
  }
  

}
