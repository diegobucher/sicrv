package br.gov.caixa.gitecsa.sicrv.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.Escala;

public interface EscalaDAO extends BaseDAO<Escala> {

    public void salvarNovasEscalas(Collection<Escala> escalaList);

    public List<Escala> obterEscalasPorEquipeEPeriodo(Long idEquipe, Date dataInicial, Date dataFinal);

    public List<Escala> obterEscalasPorEmpregadoEDatas(Empregado empregado, List<Date> dataList);

    public List<Escala> obterEscalasPorPeriodo(Date dataInicial, Date dataFinal);

    public Object[] obterDatasPeriodoEscalasFuturasPorEquipe(Long idEquipe);

    public List<Escala> obterEscalasFuturasPorEquipe(Long idEquipe, Date dataInicial);

    public Boolean existeEscalasFuturasPorEquipe(Long idEquipe, Date dataInicial);

    public Boolean existeEscalasFuturasPorEmpregado(Long idEmpregado, Date dataInicial);

    public Boolean existeEscalasFuturasPorEmpregadoPorPeriodo(Long idEmpregado, Date dataInicial, Date dataFinal);

    public List<Escala> obterEscalasPorEmpregadoEPeriodo(Empregado empregado, Date dataInicial, Date dataFinal);

    public void deletarEscalasEquipeApartirData(Equipe equipe, Date dataInicio);

    public void deletarEscalasEmpregadoApartirData(Empregado empregado, Date dataInicio);

    public List<Escala> obterEscalasFuturasPorEmpregado(Long idEmpregado, Date dataInicial);

    public void deletarEscalasEmpregadoNoPeriodo(Empregado empregado, Date dataInicio, Date dataFim);

    public Escala findByIdFetch(Long idEscala);

    public Escala obterEscalaPorEmpregadoNoDia(Empregado empregado, Date data);

    public Boolean existeEscalasPorEquipePorPeriodo(Long idEquipe, Date dataInicial, Date dataFinal);

    public List<Escala> obterEscalasPorEquipeEData(Long idEquipe, Date data);

    public List<Date> buscarDatasFolgasAPartir(Equipe equipe, Date dataInicio, Date dataFim);

    public Date getMaiorDataEscaladaPorEquipe(Equipe equipe);

    public Object[] obterDatasPeriodoEscalasFuturasPorEquipeInclusoHoje(Long idEquipe);

}
