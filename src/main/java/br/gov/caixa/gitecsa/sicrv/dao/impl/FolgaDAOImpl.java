package br.gov.caixa.gitecsa.sicrv.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.sicrv.dao.FolgaDAO;
import br.gov.caixa.gitecsa.sicrv.enumerator.SituacaoFolgaEnum;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.Folga;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;
import br.gov.caixa.gitecsa.sicrv.service.FolgaService;

public class FolgaDAOImpl extends BaseDAOImpl<Folga> implements FolgaDAO {

  @SuppressWarnings("unchecked")
  public List<Folga> buscarFolgasNoPeriodo(Empregado empregado, Date dataInicio, Date dataFim,
      List<SituacaoFolgaEnum> situacaoList) {

    StringBuilder hql = new StringBuilder();
    hql.append(" FROM Folga folga");
    hql.append(" INNER JOIN FETCH folga.empregado ");
    hql.append(" WHERE folga.empregado.id = :idEmpregado ");
    hql.append(" AND folga.data BETWEEN :dataIni AND :dataFim ");
    hql.append(" AND folga.situacao IN :situacaoList ");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idEmpregado", empregado.getId());
    query.setParameter("dataIni", dataInicio, TemporalType.DATE);
    query.setParameter("dataFim", dataFim, TemporalType.DATE);
    query.setParameter("situacaoList", situacaoList);

    return query.getResultList();
  }

  @Override
  public List<Folga> buscarFolgasAPartir(Empregado empregado, Date dataInicio, List<SituacaoFolgaEnum> situacaoList) {

    StringBuilder hql = new StringBuilder();
    hql.append(" FROM Folga folga");
    hql.append(" WHERE folga.empregado.id = :idEmpregado ");
    hql.append(" AND folga.data >= :dataIni ");
    hql.append(" AND folga.situacao IN :situacaoList");

    TypedQuery<Folga> query = getEntityManager().createQuery(hql.toString(), Folga.class);
    query.setParameter("idEmpregado", empregado.getId());
    query.setParameter("dataIni", dataInicio, TemporalType.DATE);
    query.setParameter("situacaoList", situacaoList);

    return query.getResultList();
  }

  @Override
  public Long calcularSaldoFolga(Empregado empregado, Date dataLimite) {
    return calcularFolgaAdquirida(empregado, dataLimite) - calcularFolgaUtilizadas(empregado, dataLimite);
  }

  @Override
  public Boolean isEmpregadoComFolgaMaximaAcumulada(Empregado empregado, Date dataLimite) {
    Long saldoFolga = calcularSaldoFolga(empregado, dataLimite);

    if (saldoFolga > FolgaService.PARAM_QTD_MAX_FOLGA_ACUMULADA) {
      return true;
    } else {
      return false;
    }

  }

  /**
   * Calcula a quantidade de Folgas do tipo Adquirida até a data de hoje.
   */
  private Long calcularFolgaAdquirida(Empregado empregado, Date dataLimite) {
    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT count(folga)");
    hql.append(" FROM Folga folga ");
    hql.append(" WHERE folga.data < :dataLimite ");
    hql.append(" AND folga.situacao = :situacao");
    hql.append(" AND folga.empregado.id = :idEmpregado");

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("dataLimite", dataLimite, TemporalType.DATE);
    query.setParameter("situacao", SituacaoFolgaEnum.ADQUIRIDA);
    query.setParameter("idEmpregado", empregado.getId());

    return (Long) query.getSingleResult();
  }

  private Long calcularFolgaUtilizadas(Empregado empregado, Date dataLimite) {
    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT count(folga)");
    hql.append(" FROM Folga folga ");
    hql.append(" WHERE folga.data < :dataLimite ");
    hql.append(" AND folga.situacao IN  :situacaoList");
    hql.append(" AND folga.empregado.id = :idEmpregado");

    List<SituacaoFolgaEnum> situacaoList = new ArrayList<SituacaoFolgaEnum>();
    situacaoList.add(SituacaoFolgaEnum.AGENDADA);
    situacaoList.add(SituacaoFolgaEnum.SUGERIDA);

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("dataLimite", dataLimite, TemporalType.DATE);
    query.setParameter("situacaoList", situacaoList);
    query.setParameter("idEmpregado", empregado.getId());

    return (Long) query.getSingleResult();
  }

  @Override
  public void deletarFolgasEquipeEscalaGeradaApartirData(Equipe equipe, Date dataInicio) {

    StringBuilder hql = new StringBuilder();

    hql.append(" DELETE ");
    hql.append(" FROM Folga folga");
    hql.append(" WHERE folga IN (");
    hql.append("  SELECT folga FROM Folga folga ");
    hql.append("  INNER JOIN folga.empregado emp ");
    hql.append("  INNER JOIN emp.equipesEmpregado equipesEmp ");
    hql.append("  INNER JOIN equipesEmp.equipe equipe ");
    hql.append("  WHERE equipe.id = :idEquipe ");
    hql.append("  AND Date(folga.data) >= :dataInicial ");
    hql.append("  AND folga.situacao IN (:situacaoList) ");
    hql.append(" )");

    List<SituacaoFolgaEnum> situacaoFolgaEnumList = new ArrayList<SituacaoFolgaEnum>();
    situacaoFolgaEnumList.add(SituacaoFolgaEnum.ADQUIRIDA);
    situacaoFolgaEnumList.add(SituacaoFolgaEnum.SUGERIDA);
    situacaoFolgaEnumList.add(SituacaoFolgaEnum.REPOUSO_REMUNERADO);

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idEquipe", equipe.getId());
    query.setParameter("dataInicial", dataInicio);
    query.setParameter("situacaoList", situacaoFolgaEnumList);

    query.executeUpdate();
  }

  @Override
  public void deletarFolgasEmpregadoEscalaGeradaApartirData(Empregado empregado, Date dataInicio) {

    StringBuilder hql = new StringBuilder();

    hql.append(" DELETE ");
    hql.append(" FROM Folga folga");
    hql.append(" WHERE folga IN (");
    hql.append("  SELECT folga FROM Folga folga ");
    hql.append("  INNER JOIN folga.empregado emp ");
    hql.append("  WHERE emp.id = :idEmpregado ");
    hql.append("  AND Date(folga.data) >= :dataInicial ");
    hql.append("  AND folga.situacao IN (:situacaoList) ");
    hql.append(" )");

    List<SituacaoFolgaEnum> situacaoFolgaEnumList = new ArrayList<SituacaoFolgaEnum>();
    situacaoFolgaEnumList.add(SituacaoFolgaEnum.ADQUIRIDA);
    situacaoFolgaEnumList.add(SituacaoFolgaEnum.SUGERIDA);
    situacaoFolgaEnumList.add(SituacaoFolgaEnum.REPOUSO_REMUNERADO);

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idEmpregado", empregado.getId());
    query.setParameter("dataInicial", dataInicio, TemporalType.DATE);
    query.setParameter("situacaoList", situacaoFolgaEnumList);

    query.executeUpdate();

  }

  @Override
  public void deletarFolgasEmpregadoEscalaGeradaNoPeriodo(Empregado empregado, Date dataInicio, Date dataFim) {

    StringBuilder hql = new StringBuilder();

    hql.append(" DELETE ");
    hql.append(" FROM Folga folga");
    hql.append(" WHERE folga IN (");
    hql.append("  SELECT folga FROM Folga folga ");
    hql.append("  INNER JOIN folga.empregado emp ");
    hql.append("  WHERE emp.id = :idEmpregado ");
    hql.append("  AND Date(folga.data) BETWEEN :dataInicial AND :dataFinal ");
    hql.append("  AND folga.situacao IN (:situacaoList) ");
    hql.append(" )");

    List<SituacaoFolgaEnum> situacaoFolgaEnumList = new ArrayList<SituacaoFolgaEnum>();
    situacaoFolgaEnumList.add(SituacaoFolgaEnum.ADQUIRIDA);
    situacaoFolgaEnumList.add(SituacaoFolgaEnum.SUGERIDA);
    situacaoFolgaEnumList.add(SituacaoFolgaEnum.REPOUSO_REMUNERADO);

    Query query = getEntityManager().createQuery(hql.toString());
    query.setParameter("idEmpregado", empregado.getId());
    query.setParameter("dataInicial", dataInicio, TemporalType.DATE);
    query.setParameter("dataFinal", dataFim, TemporalType.DATE);
    query.setParameter("situacaoList", situacaoFolgaEnumList);

    query.executeUpdate();

  }

  @Override
  public List<Date> buscarDatasFolgasAPartir(Equipe equipe, Date dataInicio, Date dataFim, List<SituacaoFolgaEnum> situacaoList) {

    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT DISTINCT folga.data");
    hql.append(" FROM Folga folga");
    hql.append(" WHERE folga.empregado.id IN ( ");
    hql.append("  SELECT DISTINCT emp.id FROM Equipe equipe  ");
    hql.append("     INNER JOIN equipe.equipeEmpregados equipEmp INNER JOIN equipEmp.empregado emp  ");
    hql.append("  WHERE equipe.id = :idEquipe AND equipEmp.ativo = TRUE AND emp.ativo = TRUE ");
    hql.append("  ) ");
    hql.append(" AND folga.data BETWEEN :dataIni AND :dataFim");
    hql.append(" AND folga.situacao IN :situacaoList");

    TypedQuery<Date> query = getEntityManager().createQuery(hql.toString(), Date.class);
    query.setParameter("idEquipe", equipe.getId());
    query.setParameter("dataIni", dataInicio, TemporalType.DATE);
    query.setParameter("dataFim", dataFim, TemporalType.DATE);
    query.setParameter("situacaoList", situacaoList);

    return query.getResultList();
  }
  
  @Override
  public List<Folga> buscarFolgasEquipeNoPeriodo(Equipe equipe, Date dataInicio, Date dataFim, List<SituacaoFolgaEnum> situacaoList) {
    
    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT DISTINCT folga");
    hql.append(" FROM Folga folga");
    hql.append(" INNER JOIN FETCH folga.empregado emp");
    hql.append(" INNER JOIN FETCH emp.equipesEmpregado equiEmp");
    hql.append(" INNER JOIN FETCH equiEmp.equipe ");
    hql.append(" WHERE folga.empregado.id IN ( ");
    hql.append("  SELECT DISTINCT emp.id FROM Equipe equipe  ");
    hql.append("     INNER JOIN equipe.equipeEmpregados equipEmp INNER JOIN equipEmp.empregado emp  ");
    hql.append("  WHERE equipe.id = :idEquipe AND equipEmp.ativo = TRUE AND emp.ativo = TRUE ");
    hql.append("  ) ");
    hql.append(" AND folga.data BETWEEN :dataIni AND :dataFim");
    hql.append(" AND folga.situacao IN :situacaoList");
    
    TypedQuery<Folga> query = getEntityManager().createQuery(hql.toString(), Folga.class);
    query.setParameter("idEquipe", equipe.getId());
    query.setParameter("dataIni", dataInicio, TemporalType.DATE);
    query.setParameter("dataFim", dataFim, TemporalType.DATE);
    query.setParameter("situacaoList", situacaoList);
    
    return query.getResultList();
  }

  @Override
  public List<Folga> buscarFolgasUnidadeNoPeriodo(Unidade unidade, Date dataInicio, Date dataFim,
      List<SituacaoFolgaEnum> situacaoList) {
    
    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT DISTINCT folga");
    hql.append(" FROM Folga folga");
    hql.append(" INNER JOIN FETCH folga.empregado emp");
    hql.append(" INNER JOIN FETCH emp.equipesEmpregado equiEmp");
    hql.append(" INNER JOIN FETCH equiEmp.equipe ");
    hql.append(" WHERE folga.empregado.id IN ( ");
    hql.append("  SELECT DISTINCT emp.id FROM Equipe equipe  ");
    hql.append("     INNER JOIN equipe.equipeEmpregados equipEmp");
    hql.append("     INNER JOIN equipEmp.empregado emp  ");
    hql.append("     INNER JOIN equipEmp.equipe equip  ");
    hql.append("     INNER JOIN equip.unidade unidade ");
    hql.append("  WHERE unidade.id = :idUnidade AND equipEmp.ativo = TRUE AND emp.ativo = TRUE AND equipe.ativo = TRUE");
    hql.append("  ) ");
    hql.append(" AND folga.data BETWEEN :dataIni AND :dataFim");
    hql.append(" AND folga.situacao IN :situacaoList");
    
    TypedQuery<Folga> query = getEntityManager().createQuery(hql.toString(), Folga.class);
    query.setParameter("idUnidade", unidade.getId());
    query.setParameter("dataIni", dataInicio, TemporalType.DATE);
    query.setParameter("dataFim", dataFim, TemporalType.DATE);
    query.setParameter("situacaoList", situacaoList);
    
    return query.getResultList();
  }

}
