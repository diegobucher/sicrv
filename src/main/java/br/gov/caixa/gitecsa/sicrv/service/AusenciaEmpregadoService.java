package br.gov.caixa.gitecsa.sicrv.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.time.DateUtils;

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.sicrv.dao.AusenciaEmpregadoDAO;
import br.gov.caixa.gitecsa.sicrv.model.Ausencia;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;

@Stateless
public class AusenciaEmpregadoService {

	@Inject
	private AusenciaEmpregadoDAO ausenciaEmpregadoDAO;

	@Inject
	private EmpregadoService empregadoService;

	@Inject
	private EscalaService escalaService;

	public List<Ausencia> findAll() {
		return ausenciaEmpregadoDAO.findAll();
	}

	public Ausencia findById(final Long id) {
		return ausenciaEmpregadoDAO.findById(id);
	}

	public Empregado obterEmpregadoPorMatriculaUnidade(String matricula, Unidade unidade) {
		return empregadoService.obterEmpregadoPorMatriculaUnidade(matricula, unidade);
	}

	public void delete(final Ausencia ausencia) {
		ausenciaEmpregadoDAO.delete(ausencia);
	}

	public Ausencia salvar(final Ausencia ausencia) {
		if (ausencia.getId() == null) {
			escalaService.deletarEscalaFolgaExistenteEmpregadoNoPeriodo(ausencia.getEmpregado(), ausencia.getDataInicio(), ausencia.getDataFim());
			return ausenciaEmpregadoDAO.save(ausencia);
		} else {
			return ausenciaEmpregadoDAO.update(ausencia);
		}
	}

	/**
	 * Verifica se um empregado tem necessidade de ter escala gerada no periodo da ausencia informado. Caso algum empregado da mesma equipe tenha escalação então o empregado
	 * enviado no parametro entrará no algoritmo de gerar escala nas semanas que a ausência afeta.
	 */
	public void verificarNecessidadeAdicionarEscala(Date dataInicio, Date dataFim, Empregado pEmpregado) throws BusinessException, ParseException {

		Empregado empregado = empregadoService.findByIdFetch(pEmpregado.getId());
		Equipe equipe = empregado.getEquipeEmpregadoAtivo().getEquipe();
		Date dataInicial = getSegundaFeiraDaSemana(dataInicio);
		Date dataFinal = getDomingoDaSemana(dataFim);

		List<Empregado> empregadoList = new ArrayList<Empregado>();
		empregadoList.add(empregado);

		if (escalaService.existeEscalasPorEquipePorPeriodo(equipe.getId(), dataInicial, dataFinal)) {
			escalaService.gerarEscalaRevezamentoEmpregadoList(equipe, empregadoList, dataInicial, dataFinal);
		}
	}

	/**
	 * Recebe um Date e retorna a Segunda da mesma semana.
	 */
	private Date getSegundaFeiraDaSemana(Date dataInicio) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.truncate(dataInicio, Calendar.DAY_OF_MONTH));

		while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			cal.add(Calendar.DAY_OF_WEEK, -1);
		}

		return cal.getTime();
	}

	/**
	 * Recebe um Date e retorna o Domingo da mesma semana.
	 */
	private Date getDomingoDaSemana(Date dataFim) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataFim);

		while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			cal.add(Calendar.DAY_OF_WEEK, 1);
		}

		return cal.getTime();
	}

	public List<Ausencia> consultar(String matricula, Ausencia ausencia) {
		return ausenciaEmpregadoDAO.consultar(matricula, ausencia);
	}

	public Ausencia findByIdFetchAll(Long idAusenciaEmpregado) {
		return ausenciaEmpregadoDAO.findByIdFetchAll(idAusenciaEmpregado);
	}

	public List<Ausencia> validarPeriodoExistente(String matricula, Date dataInicial, Date dataFinal) {
		return ausenciaEmpregadoDAO.validarPeriodoExistente(matricula, dataInicial, dataFinal);
	}

	public List<Ausencia> buscarAusenciasNoPeriodo(Empregado empregado, Date dataInicial, Date dataFinal) {
		return ausenciaEmpregadoDAO.buscarAusenciasNoPeriodo(empregado, dataInicial, dataFinal);
	}

	public List<Ausencia> buscarAusenciasEquipeNoDia(Equipe equipe, Date dataInicial) {
		return ausenciaEmpregadoDAO.buscarAusenciasEquipeNoDia(equipe, dataInicial);
	}

	public List<Ausencia> buscarAusenciasEquipeNoPeriodo(Equipe equipe, Date dataInicio, Date dataFim) {
		return ausenciaEmpregadoDAO.buscarAusenciasEquipeNoPeriodo(equipe, dataInicio, dataFim);
	}

	public List<Ausencia> buscarAusenciasUnidadeNoPeriodo(Unidade unidade, Date dataInicio, Date dataFim) {
		return ausenciaEmpregadoDAO.buscarAusenciasUnidadeNoPeriodo(unidade, dataInicio, dataFim);
	}

}