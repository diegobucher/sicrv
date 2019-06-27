package br.gov.caixa.gitecsa.sicrv.service;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.time.DateUtils;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.sicrv.dao.AusenciaEmpregadoDAO;
import br.gov.caixa.gitecsa.sicrv.dao.CurvaPadraoDAO;
import br.gov.caixa.gitecsa.sicrv.dao.EmpregadoDAO;
import br.gov.caixa.gitecsa.sicrv.dao.EquipeDAO;
import br.gov.caixa.gitecsa.sicrv.dao.EscalaDAO;
import br.gov.caixa.gitecsa.sicrv.dao.EstacaoTrabalhoDAO;
import br.gov.caixa.gitecsa.sicrv.dao.FeriadoDAO;
import br.gov.caixa.gitecsa.sicrv.dao.FolgaDAO;
import br.gov.caixa.gitecsa.sicrv.enumerator.HoraFixaEnum;
import br.gov.caixa.gitecsa.sicrv.enumerator.PeriodicidadeEnum;
import br.gov.caixa.gitecsa.sicrv.enumerator.SituacaoFolgaEnum;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.Ausencia;
import br.gov.caixa.gitecsa.sicrv.model.CurvaPadrao;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeAtividade;
import br.gov.caixa.gitecsa.sicrv.model.EquipeEmpregado;
import br.gov.caixa.gitecsa.sicrv.model.Escala;
import br.gov.caixa.gitecsa.sicrv.model.EstacaoTrabalho;
import br.gov.caixa.gitecsa.sicrv.model.Feriado;
import br.gov.caixa.gitecsa.sicrv.model.Folga;
import br.gov.caixa.gitecsa.sicrv.model.dto.EscalaSemanaDTO;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.EventoFullCalendarDTO;

@Stateless
public class EscalaService extends AbstractService<Escala> {

	private static final long serialVersionUID = 3732615344431963065L;

	@Inject
	private EscalaDAO escalaDAO;

	@Inject
	private EquipeDAO equipeDAO;

	@Inject
	private EmpregadoDAO empregadoDAO;

	@Inject
	private FolgaDAO folgaDAO;

	@Inject
	private FeriadoDAO feriadoDAO;

	@Inject
	private AusenciaEmpregadoDAO ausenciaEmpregadoDAO;

	@Inject
	private CurvaPadraoDAO curvaPadraoDAO;

	@Inject
	private EstacaoTrabalhoDAO estacaoTrabalhoDAO;

	public void gerarEscalaRevezamentoEmpregadoList(Equipe equipe, List<Empregado> empregadoListParam, Date dataInicial, Date dataFinal) throws BusinessException,
			ParseException {

		List<Empregado> empregadoList = new ArrayList<Empregado>();

		for (Empregado empregado : empregadoListParam) {
			empregadoList.add(empregadoDAO.findByIdFetch(empregado.getId()));
		}

		List<Escala> escalaEmGeracaoList = escalaDAO.obterEscalasPorEquipeEPeriodo(equipe.getId(), dataInicial, dataFinal);

		gerarEscalaRevezamento(equipe, dataInicial, dataFinal, empregadoList, escalaEmGeracaoList);

	}

	/**
	 * Faz a geração da escala fazendo a validação da avaliação inicial
	 */
	public void gerarEscalaRevezamento(Equipe equipe, Date dataInicial, Date dataFinal) throws BusinessException, ParseException {

		List<Escala> escalaEmGeracaoList = new ArrayList<Escala>();

		List<Empregado> empregadoList = new ArrayList<Empregado>();
		for (EquipeEmpregado equipeEmpregado : equipe.getEquipeEmpregados()) {
			if (Boolean.FALSE.equals(equipeEmpregado.getSupervisor())) {
				empregadoList.add(empregadoDAO.findByIdFetch(equipeEmpregado.getEmpregado().getId()));
			}
		}

		validarAvaliacaoInicial(equipe, dataInicial, dataFinal);
		gerarEscalaRevezamento(equipe, dataInicial, dataFinal, empregadoList, escalaEmGeracaoList);

	}

	private void gerarEscalaRevezamento(Equipe equipe, Date dataInicial, Date dataFinal, List<Empregado> empregadoList, List<Escala> escalaEmGeracaoList)
			throws BusinessException, ParseException {

		// O sistema busca os dados necessários para a escala.
		// - A partir da equipe obtém as Atividades e curvas padrão cadastradas.
		// - A partir da equipe obtém os empregados associados
		// - Busca as estações de trabalho.

		/*
		 * Vai dividir o período em intervalos de semanas, SEG A DOM. E gerar a escala para cada semana
		 */
		List<EscalaSemanaDTO> escalaSemanaDTOList = gerarSemanasDTO(dataInicial, dataFinal);

		HashMap<Empregado, List<Folga>> folgaMap = criarMapaEmpregadoFolga(empregadoList, dataInicial, dataFinal);

		HashMap<Empregado, List<Ausencia>> ausenciaMap = criarMapaEmpregadoAusencia(empregadoList, dataInicial, dataFinal);

		List<Feriado> feriadoList = feriadoDAO.buscarFeriadosNoPeriodo(dataInicial, dataFinal);

		List<CurvaPadrao> curvaPadraoList = curvaPadraoDAO.buscarPorEquipe(equipe);

		// Inicia o loop na lista de semanas.
		for (EscalaSemanaDTO escalaSemanaDTO : escalaSemanaDTOList) {
			escalaEmGeracaoList.addAll(gerarEscalaSemanal(empregadoList, folgaMap, ausenciaMap, feriadoList, escalaSemanaDTO, curvaPadraoList, escalaEmGeracaoList));
		}

		escalaDAO.salvarNovasEscalas(escalaEmGeracaoList);

	}

	public void validarAvaliacaoInicial(Equipe equipe) throws BusinessException {
		validarAvaliacaoInicial(equipe, null, null);
	}

	private List<String> validarCurvaPadraoPreenchida(Atividade atividade) {

		List<String> validacaoEscalaList = new ArrayList<String>();

		Integer horaInicial = Integer.valueOf(atividade.getHorarioInicio().split(":")[0]);
		Integer horaFinal = Integer.valueOf(atividade.getHorarioFim().split(":")[0]);

		List<Integer> diaSemanaList = new ArrayList<Integer>();
		diaSemanaList.add(Calendar.MONDAY);
		diaSemanaList.add(Calendar.TUESDAY);
		diaSemanaList.add(Calendar.WEDNESDAY);
		diaSemanaList.add(Calendar.THURSDAY);
		diaSemanaList.add(Calendar.FRIDAY);
		if (PeriodicidadeEnum.REVEZAMENTO.equals(atividade.getPeriodicidade())) {
			diaSemanaList.add(Calendar.SATURDAY);
			diaSemanaList.add(Calendar.SUNDAY);
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, horaInicial);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		List<Integer> horaNecessariaPadraoList = new ArrayList<Integer>();
		do {
			horaNecessariaPadraoList.add(cal.get(Calendar.HOUR_OF_DAY));
			cal.add(Calendar.HOUR_OF_DAY, 1);
		} while (cal.get(Calendar.HOUR_OF_DAY) != horaFinal.intValue());

		String nomeDiaSemana[] = new DateFormatSymbols(new Locale("pt", "BR")).getWeekdays();

		for (Integer diaSemana : diaSemanaList) {

			List<Integer> horaNecessariaDiaList = new ArrayList<Integer>(horaNecessariaPadraoList);

			for (CurvaPadrao curvaPadrao : atividade.getCurvasPadrao()) {

				// Verifica as Curva Padrão do dia da semana
				if (curvaPadrao.getDia() != null && diaSemana.equals(curvaPadrao.getDia().getIntCalendarDayOfWeek())) {

					Calendar calCurvaPadrao = Calendar.getInstance();
					calCurvaPadrao.set(Calendar.HOUR_OF_DAY, curvaPadrao.getHoraInicial().getValor());
					calCurvaPadrao.set(Calendar.MINUTE, 0);
					calCurvaPadrao.set(Calendar.SECOND, 0);

					// TRATAMENTO DO ENUM 24 HORAS
					Integer horaFinalTratada24hr = curvaPadrao.getHoraFinal().getValor().intValue();
					if (horaFinalTratada24hr == 24) {
						horaFinalTratada24hr = 0;
					}

					do {
						if (horaNecessariaDiaList.contains(new Integer(calCurvaPadrao.get(Calendar.HOUR_OF_DAY)))) {
							horaNecessariaDiaList.remove(new Integer(calCurvaPadrao.get(Calendar.HOUR_OF_DAY)));
						}
						calCurvaPadrao.add(Calendar.HOUR_OF_DAY, 1);
					} while (calCurvaPadrao.get(Calendar.HOUR_OF_DAY) != horaFinalTratada24hr);

				}
			}

			if (!horaNecessariaDiaList.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("A Atividade \"" + atividade.getNome().trim() + "\" não possui Curva Padrão cadastrada no dia \"" + nomeDiaSemana[diaSemana]
						+ "\" e horário(s): [");

				Iterator<Integer> iterator = horaNecessariaDiaList.iterator();

				while (iterator.hasNext()) {
					Integer horaFaltando = iterator.next();
					builder.append(HoraFixaEnum.valueOf(horaFaltando).getDescricao());
					if (iterator.hasNext()) {
						builder.append(", ");
					}
				}

				builder.append("]");
				validacaoEscalaList.add(builder.toString());
			}

		}

		return validacaoEscalaList;
	}

	private void validarAvaliacaoInicial(Equipe equipeParam, Date dataInicial, Date dataFinal) throws BusinessException {

		List<String> erroList = new ArrayList<String>();

		Equipe equipe = equipeDAO.findByIdFetch(equipeParam.getId());

		if (equipe.getEquipeEmpregados() == null || equipe.getEquipeEmpregados().isEmpty()) {
			erroList.add("A equipe não possui empregados");
		} else {
			boolean existeEmpregado = false;
			for (EquipeEmpregado equipeEmpregado : equipe.getEquipeEmpregados()) {
				if (equipeEmpregado.getAtivo() && equipeEmpregado.getEmpregado().getAtivo()) {
					existeEmpregado = true;
					break;
				}
			}

			if (!existeEmpregado) {
				erroList.add("A equipe não possui empregados Ativos");
			}

		}
		if (equipe.getEquipeAtividades() == null || equipe.getEquipeAtividades().isEmpty()) {
			erroList.add("A equipe não possui Atividades");
		} else {
			for (EquipeAtividade equipeAtividade : equipe.getEquipeAtividades()) {

				List<Atividade> atividadeList = new ArrayList<Atividade>();

				// Se a atividade tem filhos
				if (equipeAtividade.getAtividade().getAtividadeList() != null && !equipeAtividade.getAtividade().getAtividadeList().isEmpty()) {
					atividadeList.addAll(equipeAtividade.getAtividade().getAtividadeList());
				} else {
					atividadeList.add(equipeAtividade.getAtividade());
				}

				for (Atividade atividade : atividadeList) {
					if (atividade.getAtivo()) {
						// Atividade atividade = equipeAtividade.getAtividade();
						Collection<CurvaPadrao> curvaPadraoList = atividade.getCurvasPadrao();

						if (curvaPadraoList == null || curvaPadraoList.isEmpty()) {
							erroList.add("A atividade: " + atividade.getNome().trim() + " não possui Curva Padrão cadastrada");
						} else {
							List<String> validacoesCurvaPadraoList = validarCurvaPadraoPreenchida(atividade);
							if (!validacoesCurvaPadraoList.isEmpty()) {
								erroList.addAll(validacoesCurvaPadraoList);
							}
						}
					}
				}
			}
		}

		if (dataInicial != null && dataFinal != null) {

			List<Escala> escalaList = obterEscalasPorEquipeEPeriodo(equipe.getId(), dataInicial, dataFinal);

			if (escalaList != null && !escalaList.isEmpty()) {
				erroList.add("Já existem empregados escalados nesse em algum momento desse periodo");
			}

			Date maiorDataEscalada = escalaDAO.getMaiorDataEscaladaPorEquipe(equipe);

			if (maiorDataEscalada != null) {

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Calendar calUltimaDataEscalada = Calendar.getInstance();
				calUltimaDataEscalada.setTime(DateUtils.truncate(maiorDataEscalada, Calendar.DAY_OF_MONTH));
				calUltimaDataEscalada.add(Calendar.DAY_OF_MONTH, 1);

				Date hojeTruncado = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

				// Caso a ultima escala gerada for posterior a Hoje testa se a Data Inicial é um dia após
				if (calUltimaDataEscalada.getTime().after(hojeTruncado) && !calUltimaDataEscalada.getTime().equals(dataInicial)) {
					erroList.add("A nova escala deve iniciar em: " + sdf.format(calUltimaDataEscalada.getTime()));
				}
			}

		}

		try {
			validarEstacoesTrabalhoDisponiveis(equipe, dataInicial, dataFinal);
		} catch (Exception e) {
			erroList.add(e.getMessage());
		}

		if (!erroList.isEmpty()) {
			throw new BusinessException(erroList);
		}

	}

	private void validarEstacoesTrabalhoDisponiveis(Equipe equipe, Date dataInicial, Date dataFinal) throws Exception {
		// Total de estações existentes
		List<EstacaoTrabalho> estacaoList = estacaoTrabalhoDAO.findAllAtivas();

		if (estacaoList == null || estacaoList.isEmpty()) {
			throw new Exception("Não existe Estação de Trabalho Ativa");
		}

		if (dataInicial != null && dataFinal != null) {
			// Buscar quantidade maxima de pessoas escaladas por horario
			List<Escala> escalaCadastradasList = escalaDAO.obterEscalasPorPeriodo(dataInicial, dataFinal);
			Map<String, Integer> dataHoraQtdEstacaoMap = new HashMap<String, Integer>();
			SimpleDateFormat formatCompleto = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			Calendar calendarDataHora = Calendar.getInstance();
			for (Escala escala : escalaCadastradasList) {
				calendarDataHora.setTime(escala.getInicio());

				while (calendarDataHora.getTime().before(escala.getFim())) {
					String dataHoraKey = formatCompleto.format(calendarDataHora.getTime());
					calendarDataHora.add(Calendar.HOUR_OF_DAY, 1);

					if (!dataHoraQtdEstacaoMap.containsKey(dataHoraKey)) {
						dataHoraQtdEstacaoMap.put(dataHoraKey, 1);
					} else {
						Integer qtdEmpregados = dataHoraQtdEstacaoMap.get(dataHoraKey);
						dataHoraQtdEstacaoMap.put(dataHoraKey, qtdEmpregados + 1);
					}

				}
			}

			// Buscar quantidade maxima de pessoas QUE PODEM escaladas por horario
			List<EquipeEmpregado> empregadosList = new ArrayList<EquipeEmpregado>(equipe.getEquipeEmpregados());
			SimpleDateFormat formatHoraMinuto = new SimpleDateFormat("HH:mm");
			calendarDataHora = Calendar.getInstance();
			Calendar calendarDiaEscalado = Calendar.getInstance();
			for (EquipeEmpregado equipeEmpregado : empregadosList) {
				String horaIni = equipeEmpregado.getHorarioInicio();
				String horaFim = equipeEmpregado.getHorarioFim();

				// if (horaFim.equals(HoraFixaEnum.VINTE_QUATRO.getDescricao())) {
				// horaFim = HoraFixaEnum.ZERO.getDescricao();
				// }

				calendarDataHora.setTime(dataInicial);

				// while que passa os dias do inicio ao fim, soma os dias
				while (calendarDataHora.getTime().before(dataFinal)) {

					// Pega o calendario do dia pelo calendar superior
					calendarDiaEscalado.setTime(calendarDataHora.getTime());
					calendarDiaEscalado.set(Calendar.HOUR_OF_DAY, Integer.valueOf(horaIni.split(":")[0]));
					calendarDiaEscalado.set(Calendar.MINUTE, Integer.valueOf(horaIni.split(":")[1]));

					String horaMinutoFmt = formatHoraMinuto.format(calendarDiaEscalado.getTime());

					// while de um dia de trabalho, soma as horas
					while (!horaMinutoFmt.equals(horaFim)) {

						calendarDiaEscalado.add(Calendar.HOUR_OF_DAY, 1);

						String dataHoraKey = formatCompleto.format(calendarDiaEscalado.getTime());

						if (!dataHoraQtdEstacaoMap.containsKey(dataHoraKey)) {
							dataHoraQtdEstacaoMap.put(dataHoraKey, 1);
						} else {
							Integer qtdEmpregados = dataHoraQtdEstacaoMap.get(dataHoraKey);
							dataHoraQtdEstacaoMap.put(dataHoraKey, qtdEmpregados + 1);
						}

						horaMinutoFmt = formatHoraMinuto.format(calendarDiaEscalado.getTime());
					}

					calendarDataHora.add(Calendar.DAY_OF_MONTH, 1);
				}

			}

			Integer picoMaximo = 0;
			List<Integer> qtdPessoasPorHora = new ArrayList<Integer>(dataHoraQtdEstacaoMap.values());
			if (qtdPessoasPorHora.size() > 0) {
				Collections.sort(qtdPessoasPorHora);
				Collections.reverse(qtdPessoasPorHora);
				picoMaximo = qtdPessoasPorHora.get(0);
			}
			if (picoMaximo > estacaoList.size()) {
				throw new Exception("A quantidade de Estações de Trabalho: " + estacaoList.size() + " não atende a necessidade: " + picoMaximo);
			}
		}
	}

	/**
	 * Cria um Map de Empregado com um List de Folgas no periodo informado.
	 */
	private HashMap<Empregado, List<Folga>> criarMapaEmpregadoFolga(List<Empregado> empregadoList, Date dataInicio, Date dataFim) {
		HashMap<Empregado, List<Folga>> folgaMap = new HashMap<Empregado, List<Folga>>();

		List<SituacaoFolgaEnum> situacaoFolgaList = new ArrayList<SituacaoFolgaEnum>();
		situacaoFolgaList.add(SituacaoFolgaEnum.AGENDADA);
		situacaoFolgaList.add(SituacaoFolgaEnum.SUGERIDA);

		for (Empregado empregado : empregadoList) {
			List<Folga> folgaList = folgaDAO.buscarFolgasNoPeriodo(empregado, dataInicio, dataFim, situacaoFolgaList);
			if (folgaList != null && !folgaList.isEmpty()) {
				folgaMap.put(empregado, folgaList);
			}
		}

		return folgaMap;
	}

	/**
	 * Cria um Map de Empregado com um List de Ausencia no periodo informado.
	 */
	private HashMap<Empregado, List<Ausencia>> criarMapaEmpregadoAusencia(List<Empregado> empregadoList, Date dataInicio, Date dataFim) {
		HashMap<Empregado, List<Ausencia>> ausenciaMap = new HashMap<Empregado, List<Ausencia>>();

		for (Empregado empregado : empregadoList) {
			List<Ausencia> ausenciaList = ausenciaEmpregadoDAO.buscarAusenciasNoPeriodo(empregado, dataInicio, dataFim);
			if (ausenciaList != null && !ausenciaList.isEmpty()) {
				ausenciaMap.put(empregado, ausenciaList);
			}
		}

		return ausenciaMap;
	}

	/**
	 * Gera a escala na semana da equipe.
	 * 
	 * @throws ParseException
	 */
	private List<Escala> gerarEscalaSemanal(List<Empregado> empregadoList, HashMap<Empregado, List<Folga>> folgaMap, HashMap<Empregado, List<Ausencia>> ausenciaMap,
			List<Feriado> feriadoList, EscalaSemanaDTO escalaSemanaDTO, List<CurvaPadrao> curvaPadraoList, List<Escala> escalaEmGeracaoList) throws ParseException {

		List<Escala> escalaEquipeSemanaGeradaList = new ArrayList<Escala>();

		// Relação de número e empregado para sorteio
		HashMap<Integer, Empregado> numeroEmpregadoMap = new HashMap<Integer, Empregado>();
		int numero = 1;
		for (Empregado empregado : empregadoList) {
			numeroEmpregadoMap.put(numero, empregado);
			numero++;
		}

		while (numeroEmpregadoMap.size() > 0) {
			Empregado empregadoSorteado = sortearEmpregado(numeroEmpregadoMap);

			// Pegar o empregado atualizado pela semana, folgas e saldo.
			empregadoSorteado = empregadoDAO.findByIdFetch(empregadoSorteado.getId());

			// Lista com os dias de descanso forçado. Nesse dia ele é obrigado a descansar.
			List<Integer> diasSemanaDescansoForcadoList = new ArrayList<Integer>();

			// REMOVER DESCANSOS REMUNERADOS
			List<SituacaoFolgaEnum> situacaoList = new ArrayList<SituacaoFolgaEnum>();
			situacaoList.add(SituacaoFolgaEnum.REPOUSO_REMUNERADO);

			List<Folga> repousoRemuneradoExistenteNaSemana = folgaDAO.buscarFolgasNoPeriodo(empregadoSorteado, escalaSemanaDTO.getDataInicio(), escalaSemanaDTO.getDataFim(),
					situacaoList);

			// REMOVER DIAS JÁ ESCALADOS DO EMPREGADO NESSA SEMANA
			List<Escala> escalaEmpregadoExistenteNaSemana = escalaDAO.obterEscalasPorEmpregadoEPeriodo(empregadoSorteado, escalaSemanaDTO.getDataInicio(),
					escalaSemanaDTO.getDataFim());

			// Dias da semana em que é possivel o empregado trabalhar. Sem Folga, Ausencia e Descanso Forcado.
			List<Integer> diasSemanaDisponiveisList = gerarDiasSemanaDisponives(empregadoSorteado, escalaSemanaDTO, ausenciaMap.get(empregadoSorteado),
					folgaMap.get(empregadoSorteado), feriadoList, escalaEmGeracaoList, diasSemanaDescansoForcadoList, repousoRemuneradoExistenteNaSemana,
					escalaEmpregadoExistenteNaSemana);

			// Gera os descansos forçados + sorteados
			List<Integer> diasDescansoList = sortearDescansosSemana(empregadoSorteado, escalaSemanaDTO, diasSemanaDisponiveisList, diasSemanaDescansoForcadoList, feriadoList,
					repousoRemuneradoExistenteNaSemana);

			List<Escala> escalaEmpregadoSemana = gerarDiasEscalaTrabalho(empregadoSorteado, escalaSemanaDTO, diasSemanaDisponiveisList, diasDescansoList, feriadoList,
					curvaPadraoList, escalaEquipeSemanaGeradaList);

			escalaEquipeSemanaGeradaList.addAll(escalaEmpregadoSemana);
		}

		return escalaEquipeSemanaGeradaList;
	}

	/**
	 * Recebendo a lista com os dias da semana disponiveis para trabalho e a lista de descansos do empregado, a partir dai gera a lista com os dias de trabalho após
	 * verificações. Trocar dia de trabalho desnecessários no final de semana trocando por descanso.
	 * 
	 * @param feriadoList
	 */
	private List<Escala> gerarDiasEscalaTrabalho(Empregado empregado, EscalaSemanaDTO escalaSemanaDTO, List<Integer> diasSemanaDisponiveisList,
			List<Integer> diasDescansoList, List<Feriado> feriadoList, List<CurvaPadrao> curvaPadraoList, List<Escala> escalasEquipeSemanaGeradasList) {

		List<Escala> escalaList = new ArrayList<Escala>();

		List<Date> datasTrabalhoEscalaList = gerarDatasEscala(empregado, escalaSemanaDTO, escalasEquipeSemanaGeradasList, feriadoList, diasSemanaDisponiveisList,
				diasDescansoList, curvaPadraoList);

		String horaInicioEquipe = empregado.getEquipeEmpregadoAtivo().getHorarioInicio();
		String horaFimEquipe = empregado.getEquipeEmpregadoAtivo().getHorarioFim();

		Calendar calendar = Calendar.getInstance();

		for (Date data : datasTrabalhoEscalaList) {
			Escala diaEscala = new Escala();
			diaEscala.setEquipeEmpregado(empregado.getEquipeEmpregadoAtivo());
			diaEscala.setEstacaoTrabalho(null);// TODO Estação Trabalho na Escala

			diaEscala.setInicio(gerarDateHoraInicioEscala(data, horaInicioEquipe));
			diaEscala.setFim(gerarDateHoraFimEscala(data, horaInicioEquipe, horaFimEquipe));

			escalaList.add(diaEscala);

			calendar.setTime(data);
			if (isFinalSemanaOuFeriado(calendar.get(Calendar.DAY_OF_WEEK), feriadoList, escalaSemanaDTO)) {
				criarFolgaAdquirida(empregado, data);
			}
		}

		for (Integer diaDescanso : diasDescansoList) {
			Date dataDescanso = escalaSemanaDTO.getDataNaSemana(diaDescanso);
			criarFolgaDescansoRemunerado(empregado, dataDescanso);
		}

		return escalaList;

	}

	public Escala criarObjetoEscala(EquipeEmpregado equipEmp, Date data) {
		String horaInicioEquipe = equipEmp.getHorarioInicio();
		String horaFimEquipe = equipEmp.getHorarioFim();

		Escala diaEscala = new Escala();
		diaEscala.setEquipeEmpregado(equipEmp);
		diaEscala.setEstacaoTrabalho(null);// TODO Estação Trabalho na Escala

		diaEscala.setInicio(gerarDateHoraInicioEscala(data, horaInicioEquipe));
		diaEscala.setFim(gerarDateHoraFimEscala(data, horaInicioEquipe, horaFimEquipe));
		return diaEscala;
	}

	private Folga criarFolgaDescansoRemunerado(Empregado empregado, Date data) {

		Folga folga = new Folga();
		folga.setEmpregado(empregado);
		folga.setData(data);
		folga.setSituacao(SituacaoFolgaEnum.REPOUSO_REMUNERADO);

		folgaDAO.save(folga);

		return folga;
	}

	/**
	 * Cria uma Folga adquirida, na data informada e atualiza o Maximo folga até essa data. DEFAULT: ATUALIZA O LIMITE - USAR Somente na Geração de Escala
	 */
	private Folga criarFolgaAdquirida(Empregado empregado, Date data) {
		Folga folga = new Folga();
		folga.setEmpregado(empregado);
		folga.setData(data);
		folga.setSituacao(SituacaoFolgaEnum.ADQUIRIDA);

		folgaDAO.save(folga);

		atualizarLimiteFolgaEmpregado(empregado, data);

		return folga;
	}

	private Folga criarFolgaAdquiridaSemAtualizarSaldo(Empregado empregado, Date dataFolga) {

		Folga folga = new Folga();
		folga.setEmpregado(empregado);
		folga.setData(dataFolga);
		folga.setSituacao(SituacaoFolgaEnum.ADQUIRIDA);

		folgaDAO.save(folga);

		return folga;
	}

	/**
	 * Calcula o saldo de folga na data informada, e testa se atingiu o limite de folgas para atualizar o campo maximo folgas do empregado.
	 */
	private void atualizarLimiteFolgaEmpregado(Empregado empregado, Date data) {
		Integer saldoFolga = folgaDAO.calcularSaldoFolga(empregado, data).intValue();
		empregadoDAO.atualizarLimiteFolgaEmpregado(empregado, saldoFolga);
	}

	private Folga criarFolgaSugerida(Empregado empregado, Date data) {

		Folga folga = new Folga();
		folga.setEmpregado(empregado);
		folga.setData(data);
		folga.setSituacao(SituacaoFolgaEnum.SUGERIDA);

		folgaDAO.save(folga);

		atualizarLimiteFolgaEmpregado(empregado, data);

		return folga;
	}

	/**
	 * Gera os Datas (Date) de trabalho na semana. Verifica e troca escalação de FDS/Feriado por dia de semana caso a escala esteja atendida.
	 */
	private List<Date> gerarDatasEscala(Empregado empregado, EscalaSemanaDTO escalaSemanaDTO, List<Escala> escalasEquipeSemanaGeradasList, List<Feriado> feriadoList,
			List<Integer> diasSemanaDisponiveisList, List<Integer> diasDescansoList, List<CurvaPadrao> curvaPadraoList) {

		// Dias de trabalho sem descanço
		List<Integer> diasTrabalhoList = new ArrayList<Integer>();

		for (Integer dia : diasSemanaDisponiveisList) {
			if (!diasDescansoList.contains(dia)) {
				diasTrabalhoList.add(dia);
			}
		}

		List<Date> datasTrabalhoEscalaList = processarTrocaEscalaFimSemanaJaAtendida(empregado, escalaSemanaDTO, escalasEquipeSemanaGeradasList, feriadoList,
				diasDescansoList, curvaPadraoList, diasTrabalhoList);

		/*
		 * Quando o empregado atinge o máximo de folgas então executa a sugestão de folgas.
		 */
		if (empregado.getMaximoFolga()) {

			final Integer qtdMinFolgaSugeridaParam = 2;
			final Integer qtdMaxFolgaSugeridaParam = 5;

			// SUGERIR FOLGAS
			Integer qtdDiasTrabalho = datasTrabalhoEscalaList.size();
			Integer qtdMinFolgaSugerida = qtdMinFolgaSugeridaParam;
			Integer qtdMaxFolgaSugerida = qtdMaxFolgaSugeridaParam;

			if (qtdDiasTrabalho < qtdMaxFolgaSugerida) {
				qtdMaxFolgaSugerida = qtdDiasTrabalho;
			}
			if (qtdMinFolgaSugerida > qtdDiasTrabalho) {
				qtdMinFolgaSugerida = qtdDiasTrabalho;
			}

			List<Integer> possibilidade = new ArrayList<Integer>();

			do {
				possibilidade.add(qtdMinFolgaSugerida);
				qtdMinFolgaSugerida++;
			} while (qtdMinFolgaSugerida <= qtdMaxFolgaSugerida);

			List<Integer> indiceDataList = new ArrayList<Integer>();
			for (int i = 0; i < datasTrabalhoEscalaList.size(); i++) {
				indiceDataList.add(i);
			}

			Integer qtdASugerir = getInteiroAleatorioNumaLista(possibilidade);

			List<Date> datasRemover = new ArrayList<Date>();

			for (int i = 0; i < qtdASugerir; i++) {
				Integer indiceSorteado = getInteiroAleatorioNumaListaRemoveSorteado(indiceDataList);

				Date dataFolgaSugerida = datasTrabalhoEscalaList.get(indiceSorteado.intValue());
				datasRemover.add(datasTrabalhoEscalaList.get(indiceSorteado));
				criarFolgaSugerida(empregado, dataFolgaSugerida);
			}

			for (Date data : datasRemover) {
				datasTrabalhoEscalaList.remove(data);
			}

		}

		return datasTrabalhoEscalaList;

	}

	/**
	 * Verifica se o empregado esta escalado para trabalhar final de semana/feriado e se estiver verifica se a escala para o horário inicial já está atendida. Caso esteja
	 * então transfere esse dia de trabalho para um descanso do meio da semana.
	 * 
	 * @param empregado
	 *            - Empregado
	 * @param escalaSemanaDTO
	 *            - Escala da semana
	 * @param escalasEquipeSemanaGeradasList
	 *            - Lista das escalas geradas na equipe na semana
	 * @param feriadoList
	 *            - Lista de feriados
	 * @param diasDescansoList
	 *            - Dias de descanso
	 * @param curvaPadraoList
	 *            - Lista de curva padrão
	 * @param diasTrabalhoList
	 *            - Dias que está escalado para trabalho
	 * @return List<Date> - Lista processada com datas alteradas.
	 */
	private List<Date> processarTrocaEscalaFimSemanaJaAtendida(Empregado empregado, EscalaSemanaDTO escalaSemanaDTO, List<Escala> escalasEquipeSemanaGeradasList,
			List<Feriado> feriadoList, List<Integer> diasDescansoList, List<CurvaPadrao> curvaPadraoList, List<Integer> diasTrabalhoList) {
		/*
		 * Regra de tirar o trabalho do FDS/Feriado para dia de semana
		 */
		List<Date> datasTrabalhoEscalaList = escalaSemanaDTO.getListaDataPorListaInt(diasTrabalhoList);

		Integer SABADO = new Integer(Calendar.SATURDAY);
		Integer DOMINGO = new Integer(Calendar.SUNDAY);

		if (diasTrabalhoList.contains(SABADO) || diasTrabalhoList.contains(DOMINGO) || (feriadoList != null && !Collections.disjoint(datasTrabalhoEscalaList, feriadoList))) {

			List<Integer> diasTrabalhoListTemp = new ArrayList<Integer>(diasTrabalhoList);

			for (Integer diaTrabalho : diasTrabalhoListTemp) {
				if (isFinalSemanaOuFeriado(diaTrabalho, feriadoList, escalaSemanaDTO)) {

					// VERIFICAR SE ESSE DIA A ESCALA JÀ ESTA ATENDIDA
					// SE ATENDIDA TROCA COM O DESCANCO escalasEquipeSemanaGeradasList
					if (isEscalaDiaFinalSemanaFeriadoAtendida(empregado, escalasEquipeSemanaGeradasList, diaTrabalho, escalaSemanaDTO, curvaPadraoList)) {

						Iterator<Integer> it = diasDescansoList.iterator();

						while (it.hasNext()) {
							Integer diaDescanso = it.next();

							if (!isFinalSemanaOuFeriado(diaDescanso, feriadoList, escalaSemanaDTO)) {
								it.remove();
								diasDescansoList.add(diaTrabalho);

								diasTrabalhoList.remove(diaTrabalho);
								diasTrabalhoList.add(diaDescanso);
								break;
							}
						}

					}
				}
			}

			// Atualiza a lista de dates pois podem ter sido alteradas
			datasTrabalhoEscalaList = escalaSemanaDTO.getListaDataPorListaInt(diasTrabalhoList);
		}
		return datasTrabalhoEscalaList;
	}

	private boolean isEscalaDiaFinalSemanaFeriadoAtendida(Empregado empregado, List<Escala> escalaSemanalGeradasList, Integer diaSemana, EscalaSemanaDTO escalaSemanaDTO,
			List<CurvaPadrao> curvaPadraoList) {

		String horarioInicio = empregado.getEquipeEmpregadoAtivo().getHorarioInicio();
		Date dataEscala = escalaSemanaDTO.getDataNaSemana(diaSemana.intValue());
		Calendar calendarDiaTrabalho = Calendar.getInstance();
		calendarDiaTrabalho.setTime(dataEscala);
		calendarDiaTrabalho.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horarioInicio.split(":")[0]));

		// TODO VERIFICAR SE É IGUAL OU BETWEEN
		CurvaPadrao curvaAnalise = null;
		Integer hora = Integer.parseInt(horarioInicio.split(":")[0]);

		// Verifica data especifica
		for (CurvaPadrao curvaPadrao : curvaPadraoList) {
			Integer horaFinal = curvaPadrao.getHoraFinal().getValor();
			if (horaFinal == 0) {
				horaFinal = 24;
			}
			if (curvaPadrao.getDataEspecifica() != null && curvaPadrao.getDataEspecifica().equals(dataEscala) && hora >= curvaPadrao.getHoraInicial().getValor()
					&& hora < horaFinal) {

				curvaAnalise = curvaPadrao;
			}
		}

		// Verifica dia de semana
		if (curvaAnalise == null) {
			for (CurvaPadrao curvaPadrao : curvaPadraoList) {
				Integer horaFinal = curvaPadrao.getHoraFinal().getValor();
				if (horaFinal == 0) {
					horaFinal = 24;
				}
				if (curvaPadrao.getDia() != null && curvaPadrao.getDia().getIntCalendarDayOfWeek().equals(diaSemana) && hora >= curvaPadrao.getHoraInicial().getValor()
						&& hora < horaFinal) {
					curvaAnalise = curvaPadrao;
					break;
				}
			}

		}

		if (curvaAnalise != null) {

			Integer qtdHoras = curvaAnalise.getQuantidadeHoras();

			Double qtdChamadosPorPessoaPorHora = 60 / curvaAnalise.getTempoAtendimento().doubleValue();
			Double mediaChamadosPorHora = curvaAnalise.getQuantidadeChamados() / qtdHoras.doubleValue();

			Double qtdPessoasNecessarias = mediaChamadosPorHora / qtdChamadosPorPessoaPorHora;

			int qtdPessoasNecessariasArredondado = (int) Math.ceil(qtdPessoasNecessarias);

			int contEscalados = 0;
			for (Escala escala : escalaSemanalGeradasList) {
				if (!escala.getInicio().after(calendarDiaTrabalho.getTime()) && escala.getFim().after(calendarDiaTrabalho.getTime())) {
					contEscalados++;
				}
			}

			if (contEscalados >= qtdPessoasNecessariasArredondado) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	private boolean isFinalSemanaOuFeriado(Integer diaTrabalho, List<Feriado> feriadoList, EscalaSemanaDTO escalaSemanaDTO) {

		Integer SABADO = new Integer(Calendar.SATURDAY);
		Integer DOMINGO = new Integer(Calendar.SUNDAY);

		List<String> feriadoStringList = new ArrayList<String>();
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

		if (feriadoList != null) {
			for (Feriado feriado : feriadoList) {
				feriadoStringList.add(fmt.format(feriado.getData()));
			}
		}

		String dataNaSemana = fmt.format(escalaSemanaDTO.getDataNaSemana(diaTrabalho.intValue()));

		if (diaTrabalho.equals(SABADO) || diaTrabalho.equals(DOMINGO) || feriadoStringList.contains(dataNaSemana)) {
			return true;
		}

		return false;
	}

	private boolean isFinalSemanaOuFeriado(Date data, List<Feriado> feriadoList) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(data);

		Integer SABADO = new Integer(Calendar.SATURDAY);
		Integer DOMINGO = new Integer(Calendar.SUNDAY);

		List<String> feriadoStringList = new ArrayList<String>();
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

		if (feriadoList != null) {
			for (Feriado feriado : feriadoList) {
				feriadoStringList.add(fmt.format(feriado.getData()));
			}
		}

		String dataNaSemana = fmt.format(data);

		if (cal.get(Calendar.DAY_OF_WEEK) == SABADO || cal.get(Calendar.DAY_OF_WEEK) == DOMINGO || feriadoStringList.contains(dataNaSemana)) {
			return true;
		}

		return false;
	}

	/**
	 * Recebe uma data e uma hora/minuto Inicial e retorna essa data com hora/minuto.
	 */
	private Date gerarDateHoraInicioEscala(Date data, String horaMinutoIni) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);

		String hh = horaMinutoIni.split(":")[0];
		String mm = horaMinutoIni.split(":")[1];

		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hh));
		cal.set(Calendar.MINUTE, Integer.parseInt(mm));
		cal.set(Calendar.SECOND, 0);

		return cal.getTime();
	}

	/**
	 * Recebe uma data e uma hora/minuto Inicial e Final e retorna essa data com hora/minuto Final da Escala.
	 */
	private Date gerarDateHoraFimEscala(Date data, String horaMinutoIni, String horaMinutoFim) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(gerarDateHoraInicioEscala(data, horaMinutoIni));

		int hhIni = Integer.parseInt(horaMinutoIni.split(":")[0]);
		int hhFim = Integer.parseInt(horaMinutoFim.split(":")[0]);

		if (hhFim >= hhIni) {
			cal.set(Calendar.HOUR_OF_DAY, hhFim);
			cal.set(Calendar.MINUTE, Integer.parseInt(horaMinutoFim.split(":")[1]));
		} else {
			cal.add(Calendar.DAY_OF_WEEK, 1);
			cal.set(Calendar.HOUR_OF_DAY, hhFim);
			cal.set(Calendar.MINUTE, Integer.parseInt(horaMinutoFim.split(":")[1]));
		}

		return cal.getTime();
	}

	/**
	 * Busca os dias da semana disponíveis para trabalho do empregado. Verifica ausências.
	 * 
	 * @throws ParseException
	 */
	private List<Integer> gerarDiasSemanaDisponives(Empregado empregado, EscalaSemanaDTO escalaSemanaDTO, List<Ausencia> ausenciaList, List<Folga> folgaList,
			List<Feriado> feriadoList, List<Escala> escalaEmGeracaoList, List<Integer> diasSemanaDescansoForcadoList, List<Folga> repousoRemuneradoExistenteNaSemana,
			List<Escala> escalaEmpregadoExistenteNaSemana) throws ParseException {

		List<Integer> diasSemanaDisponiveisList = new ArrayList<Integer>();

		Calendar cal = Calendar.getInstance();
		cal.setTime(escalaSemanaDTO.getDataInicio());

		do {

			// TODO EM ANDAMENTO
			boolean diaSemanaIndisponivel = false;
			for (Folga folga : repousoRemuneradoExistenteNaSemana) {
				if (DateUtils.truncate(cal.getTime(), Calendar.DATE).equals(DateUtils.truncate(folga.getData(), Calendar.DATE))) {
					diaSemanaIndisponivel = true;
					break;
				}
			}

			if (diaSemanaIndisponivel) {
				cal.add(Calendar.DATE, 1);
				continue;
			}

			for (Escala escala : escalaEmpregadoExistenteNaSemana) {
				if (DateUtils.truncate(cal.getTime(), Calendar.DATE).equals(DateUtils.truncate(escala.getInicio(), Calendar.DATE))) {
					diaSemanaIndisponivel = true;
					break;
				}
			}

			if (diaSemanaIndisponivel) {
				cal.add(Calendar.DATE, 1);
				continue;
			}

			// TODO EM ANDAMENTO

			if (isDataDescansoForcado(empregado, cal.getTime(), feriadoList, escalaEmGeracaoList)) {
				diasSemanaDescansoForcadoList.add(cal.get(Calendar.DAY_OF_WEEK));
			} else {

				if (isDataDisponivelTrabalho(empregado, ausenciaList, folgaList, cal.getTime(), escalaEmGeracaoList)) {
					diasSemanaDisponiveisList.add(cal.get(Calendar.DAY_OF_WEEK));
				}
			}

			cal.add(Calendar.DATE, 1);
		} while (!cal.getTime().after(escalaSemanaDTO.getDataFim()));

		return diasSemanaDisponiveisList;
	}

	/**
	 * Recebe um empregado e a lista de dias na semana disponíveis para trabalho e lista de descanso forçados e sorteia 2 descansos.
	 * 
	 * @param feriadoList
	 */
	private List<Integer> sortearDescansosSemana(Empregado empregado, EscalaSemanaDTO escalaSemanaDTO, List<Integer> diasSemanaDisponiveisList,
			List<Integer> diasSemanaDescansoForcadoList, List<Feriado> feriadoList, List<Folga> repousoRemuneradoExistenteNaSemana) {

		Integer qtdDescansoNaSemana = 2;
		qtdDescansoNaSemana = qtdDescansoNaSemana + calcularQtdFeriadosMeioSemana(escalaSemanaDTO, feriadoList);
		// Caso já tenha descanços na semana
		if (repousoRemuneradoExistenteNaSemana != null) {
			qtdDescansoNaSemana = qtdDescansoNaSemana - repousoRemuneradoExistenteNaSemana.size();
		}

		List<Integer> diasSemanaList = new ArrayList<Integer>(diasSemanaDisponiveisList);
		List<Integer> diasDescansoList = new ArrayList<Integer>();

		diasDescansoList.addAll(diasSemanaDescansoForcadoList);

		while (diasDescansoList.size() < qtdDescansoNaSemana && diasSemanaList.size() > 0) {
			Integer diaFolgaSorteada = getInteiroAleatorioNumaListaRemoveSorteado(diasSemanaList);
			diasDescansoList.add(diaFolgaSorteada);
		}

		return diasDescansoList;
	}

	private Integer calcularQtdFeriadosMeioSemana(EscalaSemanaDTO escalaSemanaDTO, List<Feriado> feriadoList) {

		int contador = 0;

		for (Feriado feriado : feriadoList) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(feriado.getData());
			// Verificar se o feriado está na semana dto e ser no meio de semana
			if (!feriado.getData().before(escalaSemanaDTO.getDataInicio()) && !feriado.getData().after(escalaSemanaDTO.getDataFim())
					&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				contador++;
			}
		}

		return contador;
	}

	/**
	 * Verifica se a data informada é disponivel para trabalho. FOLGA/AUSENCIA
	 * 
	 * @throws ParseException
	 */
	private Boolean isDataDisponivelTrabalho(Empregado empregado, List<Ausencia> ausenciaList, List<Folga> folgaList, Date data, List<Escala> escalaEmGeracaoList)
			throws ParseException {

		boolean dataValida = true;

		if (isDataComFolga(folgaList, data) || isDataComAusencia(ausenciaList, data)) {
			return false;
		}

		return dataValida;
	}

	/**
	 * Verifica se a data informada é disponivel para trabalho. FOLGA/AUSENCIA
	 * 
	 * @throws ParseException
	 */
	private Boolean isDataDescansoForcado(Empregado empregado, Date data, List<Feriado> feriadoList, List<Escala> escalaEmGeracaoList) throws ParseException {

		boolean descansoForcado = false;

		if (isDataFeriadoFimSemanaComFolgaEstourada(data, feriadoList, empregado) || isTrabalhadoMaximoDomingoSeguidos(empregado, data, escalaEmGeracaoList)) {
			return true;
		}

		return descansoForcado;
	}

	private boolean isTrabalhadoMaximoDomingoSeguidos(Empregado empregado, Date data, List<Escala> escalaEmGeracaoList) throws ParseException {

		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {

			List<Date> tresDomingosAnterioresList = getTresDomingosAnteriores(data);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			int qtdDomingoTrabalho = 0;
			// Buscar na escala em geração
			for (Escala escala : escalaEmGeracaoList) {
				String escalaDtInicio = sdf.format(escala.getInicio());
				Date dtInicioFmt = sdf.parse(escalaDtInicio);
				if (escala.getEquipeEmpregado().getEmpregado().equals(empregado) && tresDomingosAnterioresList.contains(dtInicioFmt)) {
					qtdDomingoTrabalho++;
				}
			}

			if (qtdDomingoTrabalho == 3) {
				return true;
			} else {
				// Buscar nas escalas persistidas
				// Verificar na memoria e caso necessário na base, em escalas anteriores.
				List<Escala> escalaCadastradasList = escalaDAO.obterEscalasPorEmpregadoEDatas(empregado, tresDomingosAnterioresList);

				if (qtdDomingoTrabalho + escalaCadastradasList.size() == 3) {
					return true;
				}

			}

		}

		return false;
	}

	private List<Date> getTresDomingosAnteriores(Date data) {

		List<Date> domingoList = new ArrayList<Date>();

		Calendar cal = Calendar.getInstance();
		cal.setTime(data);

		int qtdDomingosAnteriores = 0;

		while (qtdDomingosAnteriores < 3) {
			cal.add(Calendar.DAY_OF_MONTH, -1);

			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				domingoList.add(cal.getTime());
				qtdDomingosAnteriores++;
			}
		}

		return domingoList;
	}

	/**
	 * Caso seja feriado ou final de semana testa se o limite folga foi atingido.
	 */
	private boolean isDataFeriadoFimSemanaComFolgaEstourada(Date data, List<Feriado> feriadoList, Empregado empregado) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(data);

		List<String> feriadoStringList = new ArrayList<String>();
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		if (feriadoList != null) {
			for (Feriado feriado : feriadoList) {
				feriadoStringList.add(fmt.format(feriado.getData()));
			}
		}

		if (empregado.getMaximoFolga()
				&& (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || feriadoStringList.contains(fmt.format(data)))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Verifica numa List de folga se contem a data informada.
	 */
	private Boolean isDataComFolga(List<Folga> folgaList, Date data) {

		if (folgaList != null && !folgaList.isEmpty()) {
			SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
			String dataFormatada = fmt.format(data);
			for (Folga folga : folgaList) {
				if (dataFormatada.equals(fmt.format(folga.getData()))) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Verifica numa List de Ausencia se contem a data informada.
	 */
	private Boolean isDataComAusencia(List<Ausencia> ausenciaList, Date data) throws ParseException {

		if (ausenciaList != null && !ausenciaList.isEmpty()) {
			SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

			Date dataFormatada = fmt.parse(fmt.format(data));

			for (Ausencia ausencia : ausenciaList) {

				Date dataIniFormatada = fmt.parse(fmt.format(ausencia.getDataInicio()));
				Date dataFimFormatada = fmt.parse(fmt.format(ausencia.getDataFim()));

				if (dataFormatada.equals(dataIniFormatada) || dataFormatada.equals(dataFimFormatada)) {
					return true;
				}

				if (dataFormatada.before(dataFimFormatada) && dataFormatada.after(dataIniFormatada)) {
					return true;
				}
			}

		}

		return false;
	}

	/**
	 * Recebe um Map de empregado e sorteia um e o remove do Map.
	 */
	private static Empregado sortearEmpregado(HashMap<Integer, Empregado> numeroEmpregadoMap) {

		Integer numeroSorteado = getInteiroAleatorioNumaLista(new ArrayList<Integer>(numeroEmpregadoMap.keySet()));

		Empregado empregado = numeroEmpregadoMap.get(numeroSorteado);
		numeroEmpregadoMap.remove(numeroSorteado);
		return empregado;
	}

	public static Integer getInteiroAleatorioNumaLista(List<Integer> lista) {
		Integer rnd = new Random().nextInt(lista.size());
		return lista.get(rnd);
	}

	public static Integer getInteiroAleatorioNumaListaRemoveSorteado(List<Integer> lista) {
		Integer rnd = new Random().nextInt(lista.size());
		Integer sorteado = lista.get(rnd);
		lista.remove(sorteado);
		return sorteado;
	}

	/**
	 * Recebe uma data inicio e fim, e quebra em semanas EscalaSemanaDTO.
	 */
	public List<EscalaSemanaDTO> gerarSemanasDTO(Date pDataInicial, Date pDataFinal) {

		List<EscalaSemanaDTO> escalaSemanaDTOList = new ArrayList<EscalaSemanaDTO>();
		EscalaSemanaDTO escalaSemanaDTO = null;
		Date dataInicioSemana = pDataInicial;
		Date dataFimSemana = null;
		Calendar calendar = Calendar.getInstance();

		do {
			calendar.setTime(dataInicioSemana);
			calendar.add(Calendar.DATE, 6);
			dataFimSemana = calendar.getTime();
			escalaSemanaDTO = new EscalaSemanaDTO(dataInicioSemana, dataFimSemana);
			escalaSemanaDTOList.add(escalaSemanaDTO);

			calendar.add(Calendar.DATE, 1);
			dataInicioSemana = calendar.getTime();
		} while (dataInicioSemana.before(pDataFinal));

		return escalaSemanaDTOList;
	}

	public List<Escala> obterEscalasPorEquipeEPeriodo(Long idEquipe, Date dataInicial, Date dataFinal) {
		return escalaDAO.obterEscalasPorEquipeEPeriodo(idEquipe, dataInicial, dataFinal);
	}

	public Object[] obterDatasPeriodoEscalasFuturasPorEquipe(Long idEquipe) {
		return escalaDAO.obterDatasPeriodoEscalasFuturasPorEquipe(idEquipe);
	}

	public Object[] obterDatasPeriodoEscalasFuturasPorEquipeInclusoHoje(Long idEquipe) {
		return escalaDAO.obterDatasPeriodoEscalasFuturasPorEquipeInclusoHoje(idEquipe);
	}

	public Boolean existeEscalasFuturasPorEquipe(Long idEquipe, Date dataInicial) {
		return escalaDAO.existeEscalasFuturasPorEquipe(idEquipe, dataInicial);
	}

	public Boolean existeEscalasFuturasPorEquipe(Long idEquipe) {
		Calendar calAmanha = Calendar.getInstance();
		calAmanha = DateUtils.truncate(calAmanha, Calendar.DAY_OF_MONTH);
		calAmanha.add(Calendar.DAY_OF_MONTH, 1);

		return escalaDAO.existeEscalasFuturasPorEquipe(idEquipe, calAmanha.getTime());
	}

	public Boolean existeEscalasFuturasPorEmpregado(Long idEmpregado, Date dataInicial) {
		return escalaDAO.existeEscalasFuturasPorEmpregado(idEmpregado, dataInicial);
	}

	public Boolean existeEscalasFuturasPorEmpregadoPorPeriodo(Long idEmpregado, Date dataInicial, Date dataFinal) {
		return escalaDAO.existeEscalasFuturasPorEmpregadoPorPeriodo(idEmpregado, dataInicial, dataFinal);
	}

	public Boolean existeEscalasFuturasPorEmpregado(Long idEmpregado) {
		Calendar calAmanha = Calendar.getInstance();
		calAmanha = DateUtils.truncate(calAmanha, Calendar.DAY_OF_MONTH);
		calAmanha.add(Calendar.DAY_OF_MONTH, 1);

		return escalaDAO.existeEscalasFuturasPorEmpregado(idEmpregado, calAmanha.getTime());
	}

	public void deletarEscalaExistente(Equipe equipe, Date dataInicial) {
		folgaDAO.deletarFolgasEquipeEscalaGeradaApartirData(equipe, dataInicial);
		escalaDAO.deletarEscalasEquipeApartirData(equipe, dataInicial);
	}

	public void deletarEscalaFolgaExistenteEmpregado(Empregado empregado, Date dataInicial) {
		folgaDAO.deletarFolgasEmpregadoEscalaGeradaApartirData(empregado, dataInicial);
		escalaDAO.deletarEscalasEmpregadoApartirData(empregado, dataInicial);
	}

	public void deletarEscalaFolgaExistenteEmpregadoNoPeriodo(Empregado empregado, Date dataInicial, Date dataFinal) {
		folgaDAO.deletarFolgasEmpregadoEscalaGeradaNoPeriodo(empregado, dataInicial, dataFinal);
		escalaDAO.deletarEscalasEmpregadoNoPeriodo(empregado, dataInicial, dataFinal);
	}

	public void atualizarEscalasEmpregadoApartirAmanha(EquipeEmpregado equipeEmpregado) throws RequiredException, BusinessException, Exception {

		Calendar amanha = Calendar.getInstance();
		amanha = DateUtils.truncate(amanha, Calendar.DAY_OF_MONTH);
		amanha.add(Calendar.DAY_OF_MONTH, 1);

		List<Escala> escalasFuturaList = escalaDAO.obterEscalasFuturasPorEmpregado(equipeEmpregado.getEmpregado().getId(), amanha.getTime());

		for (Escala escala : escalasFuturaList) {
			Calendar calInicio = Calendar.getInstance();
			calInicio.setTime(escala.getInicio());

			Integer horaInicio = equipeEmpregado.getHorarioInicioEnum().getValor();

			calInicio.set(Calendar.HOUR_OF_DAY, horaInicio);

			Calendar calFim = Calendar.getInstance();
			calFim.setTime(escala.getFim());

			Integer horaFim = equipeEmpregado.getHorarioFimEnum().getValor();
			// if (horaFim == HoraFixaEnum.VINTE_QUATRO.getValor()) {
			// horaFim = HoraFixaEnum.ZERO.getValor();
			// }

			// Se hora fim < hora inicio então mudou o dia, e caso a escala no banco não a hora fim não esteja no mesmo dia
			if (horaFim < horaInicio) {
				if (calInicio.get(Calendar.DAY_OF_WEEK) == calFim.get(Calendar.DAY_OF_WEEK)) {
					calFim.add(Calendar.DAY_OF_MONTH, 1);
				}
			} else {
				if (calInicio.get(Calendar.DAY_OF_WEEK) != calFim.get(Calendar.DAY_OF_WEEK)) {
					calFim.add(Calendar.DAY_OF_MONTH, -1);
				}

			}

			calFim.set(Calendar.HOUR_OF_DAY, equipeEmpregado.getHorarioFimEnum().getValor());

			escala.setInicio(calInicio.getTime());
			escala.setFim(calFim.getTime());

			update(escala);
		}

	}

	public List<Escala> obterEscalasPorEmpregadoEPeriodo(Empregado empregado, Date dataInicial, Date dataFinal) {
		return escalaDAO.obterEscalasPorEmpregadoEPeriodo(empregado, dataInicial, dataFinal);
	}

	public Escala obterEscalaPorEmpregadoNoDia(Empregado empregado, Date data) {
		return escalaDAO.obterEscalaPorEmpregadoNoDia(empregado, data);
	}

	@Override
	public List<Escala> consultar(Escala entidade) throws Exception {

		return null;
	}

	@Override
	protected void validaCamposObrigatorios(Escala entity) {

	}

	@Override
	protected void validaRegras(Escala entity) {

	}

	@Override
	protected void validaRegrasExcluir(Escala entity) {

	}

	@Override
	protected BaseDAO<Escala> getDAO() {
		return escalaDAO;
	}

	/**
	 * Recebe um Date e retorna a Segunda da mesma semana.
	 */
	public Date getSegundaFeiraDaSemana(Date dataInicio) {
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
	public Date getDomingoDaSemana(Date dataFim) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataFim);

		while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			cal.add(Calendar.DAY_OF_WEEK, 1);
		}

		return cal.getTime();
	}

	public void validarMudancaEscala(EventoFullCalendarDTO evento, Date dataInicial, Date dataFim) throws ParseException, BusinessException {

		Escala escala = findByIdFetch(Long.parseLong(evento.getId()));

		SimpleDateFormat sdf = new SimpleDateFormat(EventoFullCalendarDTO.DATE_TIME_PATTERN);

		Date novoInicio = sdf.parse(evento.getStart());
		novoInicio = DateUtils.truncate(novoInicio, Calendar.DAY_OF_MONTH);

		// Teste para não permitir a mudança para fora do intervalo buscado
		if (novoInicio.before(getSegundaFeiraDaSemana(dataInicial)) || novoInicio.after(getDomingoDaSemana(dataFim))) {
			throw new BusinessException("Não é possível mover o Empregado para fora do intervalo em edição.");
		}

		// Teste de para não permitir mudar a escala para outra semana
		Date dataEscalado = DateUtils.truncate(escala.getInicio(), Calendar.DAY_OF_MONTH);

		Calendar inicioDaSemana = Calendar.getInstance();
		inicioDaSemana.setTime(dataEscalado);
		while (inicioDaSemana.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			inicioDaSemana.add(Calendar.DAY_OF_WEEK, -1);
		}

		Calendar fimDaSemana = Calendar.getInstance();
		fimDaSemana.setTime(dataEscalado);
		while (fimDaSemana.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			fimDaSemana.add(Calendar.DAY_OF_WEEK, 1);
		}

		if (novoInicio.before(inicioDaSemana.getTime()) || novoInicio.after(fimDaSemana.getTime())) {
			throw new BusinessException("Não é possível mover o Empregado para outra semana.");
		}

		Escala escalaCadastrada = escalaDAO.obterEscalaPorEmpregadoNoDia(escala.getEquipeEmpregado().getEmpregado(), novoInicio);

		// TODO VALIDAR JÁ ESCALADO PARA O DIA
		if (escalaCadastrada != null && !escalaCadastrada.getId().equals(escala.getId())) {
			throw new BusinessException("O Empregado já está escalado para o dia de destino.");
		}

		List<SituacaoFolgaEnum> situacaoList = new ArrayList<SituacaoFolgaEnum>();
		situacaoList.add(SituacaoFolgaEnum.AGENDADA);
		situacaoList.add(SituacaoFolgaEnum.SUGERIDA);

		List<Folga> folgaList = folgaDAO.buscarFolgasNoPeriodo(escala.getEquipeEmpregado().getEmpregado(), novoInicio, novoInicio, situacaoList);

		// TODO VALIDAR FOLGA NO NOVO DIA
		if (folgaList != null && !folgaList.isEmpty()) {
			throw new BusinessException("O Empregado tem uma Folga " + folgaList.iterator().next().getSituacao().getDescricao() + " para o dia de destino.");
		}

		List<Ausencia> ausenciaList = ausenciaEmpregadoDAO.buscarAusenciasNoPeriodo(escala.getEquipeEmpregado().getEmpregado(), novoInicio, novoInicio);

		// TODO VALIDAR AUSENCIA NO NOVO DIA
		if (ausenciaList != null && !ausenciaList.isEmpty()) {
			throw new BusinessException("O Empregado tem uma Ausência " + ausenciaList.iterator().next().getMotivo().getDescricao() + " para o dia de destino.");
		}

		Empregado empregado = escala.getEquipeEmpregado().getEmpregado();

		List<Feriado> feriadoList = feriadoDAO.buscarFeriadosNoPeriodo(novoInicio, novoInicio);

		// SE ORIGEM == DESTINO (FDS/FERIADO) Não valida a flag de folga máxima
		if (!(isFinalSemanaOuFeriado(novoInicio, feriadoList) && isFinalSemanaOuFeriado(dataEscalado, feriadoList))) {
			if (isDataFeriadoFimSemanaComFolgaEstourada(novoInicio, feriadoList, empregado)) {
				throw new BusinessException("O Empregado com o limite de folgas atingido não pode trabalhar em Feriado/Final de semana.");
			}
		}

		if (isTrabalhadoMaximoDomingoSeguidos(empregado, novoInicio, new ArrayList<Escala>())) {
			throw new BusinessException("O Empregado não pode trabalhar mais que 3 domingos seguidos.");
		}

	}

	public void alterarDiaEscala(EventoFullCalendarDTO evento, Date dataInicial, Date dataFim) throws RequiredException, Exception {

		validarMudancaEscala(evento, dataInicial, dataFim);

		SimpleDateFormat sdf = new SimpleDateFormat(EventoFullCalendarDTO.DATE_TIME_PATTERN);
		Date novoInicioEscala = sdf.parse(evento.getStart());
		Date novoInicioTruncado = DateUtils.truncate(novoInicioEscala, Calendar.DAY_OF_MONTH);
		Date novoFimEscala = sdf.parse(evento.getEnd());

		// Altera inicio e fim da escala existente para o dia de destino
		Escala escalaExistenteDia = findByIdFetch(Long.parseLong(evento.getId()));

		Date antigoInicioEscalaTruncado = DateUtils.truncate(escalaExistenteDia.getInicio(), Calendar.DAY_OF_MONTH);
		escalaExistenteDia.setInicio(novoInicioEscala);
		escalaExistenteDia.setFim(novoFimEscala);
		update(escalaExistenteDia);

		// Verifica se o novo inicio tem repouso remunerado cadastrado, se tiver troca a data pela data antiga
		List<SituacaoFolgaEnum> situacaoRepousoList = new ArrayList<SituacaoFolgaEnum>();
		situacaoRepousoList.add(SituacaoFolgaEnum.REPOUSO_REMUNERADO);
		List<Folga> folgaRepousoRemuneradoList = folgaDAO.buscarFolgasNoPeriodo(escalaExistenteDia.getEquipeEmpregado().getEmpregado(), novoInicioTruncado,
				novoInicioTruncado, situacaoRepousoList);

		if (folgaRepousoRemuneradoList != null && !folgaRepousoRemuneradoList.isEmpty()) {
			Folga descandoRemunerado = folgaRepousoRemuneradoList.iterator().next();
			descandoRemunerado.setData(antigoInicioEscalaTruncado);
			folgaDAO.update(descandoRemunerado);
		}

		// SE O DIA ANTERIOR TIVER FOLGA ADQUIRIDA ENTAO A REMOVE
		List<SituacaoFolgaEnum> situacaoAdquiridaList = new ArrayList<SituacaoFolgaEnum>();
		situacaoAdquiridaList.add(SituacaoFolgaEnum.ADQUIRIDA);
		List<Folga> folgaAdquiridaList = folgaDAO.buscarFolgasNoPeriodo(escalaExistenteDia.getEquipeEmpregado().getEmpregado(), antigoInicioEscalaTruncado,
				antigoInicioEscalaTruncado, situacaoAdquiridaList);

		if (folgaAdquiridaList != null && !folgaAdquiridaList.isEmpty()) {
			for (Folga folga : folgaAdquiridaList) {
				folgaDAO.delete(folga);
				atualizarFlagMaximoFolgas(escalaExistenteDia.getEquipeEmpregado().getEmpregado());
			}
		}

		// SE O NOVO DIA É FERIADO/FDS GERA FOLGA ADQUIRIDA
		List<Feriado> feriadoList = feriadoDAO.buscarFeriadosNoPeriodo(novoInicioTruncado, novoInicioTruncado);

		if (isFinalSemanaOuFeriado(novoInicioTruncado, feriadoList)) {
			// Busca folgas num periodo de 1 ano pra frente
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, 1);
			criarFolgaAdquiridaSemAtualizarSaldo(escalaExistenteDia.getEquipeEmpregado().getEmpregado(), novoInicioTruncado);
			atualizarFlagMaximoFolgas(escalaExistenteDia.getEquipeEmpregado().getEmpregado());
		}

	}

	/**
	 * Verifica o saldo de folgas na projeção maxima e atualiza a flag.
	 */
	private void atualizarFlagMaximoFolgas(Empregado empregado) {

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 5);

		// Pegar o saldo de folgas
		Long qtdSaldoFolga = folgaDAO.calcularSaldoFolga(empregado, cal.getTime());

		if (qtdSaldoFolga >= FolgaService.PARAM_QTD_MAX_FOLGA_ACUMULADA) {
			empregado.setMaximoFolga(true);
		} else {
			empregado.setMaximoFolga(false);
		}

		empregadoDAO.update(empregado);
	}

	public Escala findByIdFetch(Long idEscala) {
		return escalaDAO.findByIdFetch(idEscala);
	}

	public Boolean existeEscalasPorEquipePorPeriodo(Long idEquipe, Date dataInicial, Date dataFinal) {
		return escalaDAO.existeEscalasPorEquipePorPeriodo(idEquipe, dataInicial, dataFinal);
	}

	public List<Escala> obterEscalasPorEquipeEData(Long idEquipe, Date data) {
		return escalaDAO.obterEscalasPorEquipeEData(idEquipe, data);
	}

	public List<Date> buscarDatasFolgasAPartir(Equipe equipe, Date dataInicio, Date dataFim) {
		return escalaDAO.buscarDatasFolgasAPartir(equipe, dataInicio, dataFim);
	}

}
