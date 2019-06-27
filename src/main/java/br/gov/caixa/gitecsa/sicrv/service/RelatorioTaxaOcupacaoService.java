package br.gov.caixa.gitecsa.sicrv.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.time.DateUtils;

import br.gov.caixa.gitecsa.sicrv.comparator.AtividadeNomeComparator;
import br.gov.caixa.gitecsa.sicrv.dao.AtividadeDAO;
import br.gov.caixa.gitecsa.sicrv.dao.CurvaPadraoDAO;
import br.gov.caixa.gitecsa.sicrv.dao.EquipeDAO;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.CurvaPadrao;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeAtividade;
import br.gov.caixa.gitecsa.sicrv.model.Escala;
import br.gov.caixa.gitecsa.sicrv.model.dto.RelatorioTaxaOcupacaoDTO;
import br.gov.caixa.gitecsa.sicrv.model.dto.RelatorioTaxaOcupacaoDetalheHoraDTO;
import br.gov.caixa.gitecsa.sicrv.model.dto.TaxaOcupacaoSemanaDTO;

@Stateless
public class RelatorioTaxaOcupacaoService {

	@Inject
	private EscalaService escalaService;

	@Inject
	private CurvaPadraoDAO curvaPadraoDAO;

	@Inject
	private EquipeDAO equipeDAO;

	@Inject
	private AtividadeDAO atividadeDAO;

	public RelatorioTaxaOcupacaoDTO getRelatorioTaxaOcupacao(Long idEquipe, Date dataInicial, Date dataFinal) {
		RelatorioTaxaOcupacaoDTO relatorio = new RelatorioTaxaOcupacaoDTO();

		Calendar calDataFinal = Calendar.getInstance();
		calDataFinal.setTime(dataFinal);

		Equipe equipe = equipeDAO.findByIdFetch(idEquipe);

		List<Atividade> atividadeList = new ArrayList<Atividade>();
		Set<EquipeAtividade> equipeAtividades = equipe.getEquipeAtividades();
		for (EquipeAtividade equipeAtividade : equipeAtividades) {
			if (equipeAtividade.getAtividade().getAtivo()
					&& (equipeAtividade.getAtividade().getAtividadeList() == null || equipeAtividade.getAtividade().getAtividadeList().isEmpty())) {
				atividadeList.add(equipeAtividade.getAtividade());
			} else {
				for (Atividade atividadeFilha : equipeAtividade.getAtividade().getAtividadeList()) {
					if (atividadeFilha.getAtivo()) {
						atividadeList.add(atividadeFilha);
					}
				}
			}
		}
		Collections.sort(atividadeList, new AtividadeNomeComparator());

		List<Escala> escalaPeriodoList = escalaService.obterEscalasPorEquipeEPeriodo(equipe.getId(), dataInicial, dataFinal);

		Calendar dataEmAnalise = Calendar.getInstance();

		Integer qtdHorasComCurvaPadrao = 0;
		Integer qtdHorasCobertas = 0;

		List<CurvaPadrao> curvaPadraoEquipeList = curvaPadraoDAO.buscarPorEquipe(equipe);

		for (Atividade atividade : atividadeList) {

			List<CurvaPadrao> curvaPadraoAtividadeList = buscarPorAtividade(curvaPadraoEquipeList, atividade);

			dataEmAnalise.setTime(dataInicial);

			relatorio.getNomeAtividadesList().add(atividade.getNome());
			if (curvaPadraoAtividadeList != null && !curvaPadraoAtividadeList.isEmpty()) {
				relatorio.getCurvaPadraoDataSet().add(100f);
			} else {
				relatorio.getCurvaPadraoDataSet().add(0f);
			}

			qtdHorasCobertas = 0;
			qtdHorasComCurvaPadrao = 0;

			// Vai processar dia a dia
			while (dataEmAnalise.get(Calendar.DAY_OF_YEAR) <= calDataFinal.get(Calendar.DAY_OF_YEAR)) {

				Calendar horaEmAnalise = Calendar.getInstance();
				horaEmAnalise.setTime(dataEmAnalise.getTime());
				DateUtils.truncate(horaEmAnalise, Calendar.DATE);

				// Processa as horas do dia
				while (dataEmAnalise.get(Calendar.DAY_OF_MONTH) == horaEmAnalise.get(Calendar.DAY_OF_MONTH)) {

					// Pegar curva padrão dessa data
					CurvaPadrao curvaEmAnalise = buscarCurvaPadrao(horaEmAnalise, curvaPadraoAtividadeList);

					if (curvaEmAnalise != null) {
						// Calcular quantos empregados estão escalados nesse dia/hora
						Integer quantidadeEscalados = 0; // TODO
						qtdHorasComCurvaPadrao++;

						Integer qtdHoras = curvaEmAnalise.getQuantidadeHoras();
						Double qtdChamadosPorPessoaPorHora = 60 / curvaEmAnalise.getTempoAtendimento().doubleValue();
						Double mediaChamadosPorHora = curvaEmAnalise.getQuantidadeChamados() / qtdHoras.doubleValue();
						Double qtdPessoasNecessarias = mediaChamadosPorHora / qtdChamadosPorPessoaPorHora;
						int qtdPessoasNecessariasArredondado = (int) Math.ceil(qtdPessoasNecessarias);

						// int contEscalados = 0;
						for (Escala escala : escalaPeriodoList) {
							if (!escala.getInicio().after(horaEmAnalise.getTime()) && escala.getFim().after(horaEmAnalise.getTime())) {
								quantidadeEscalados++;
							}
						}

						if (quantidadeEscalados >= qtdPessoasNecessariasArredondado) {
							qtdHorasCobertas++;
						}
					}
					horaEmAnalise.add(Calendar.HOUR, 1);
				}

				dataEmAnalise.add(Calendar.DAY_OF_MONTH, 1);
			}

			Double porcentagemCobertura = 0d;
			if (qtdHorasComCurvaPadrao != 0) {
				porcentagemCobertura = Math.ceil((qtdHorasCobertas * 100) / (float) (qtdHorasComCurvaPadrao));
			}
			relatorio.getOcupacaoDataSet().add(porcentagemCobertura.floatValue());
		}

		return relatorio;
	}

	private List<CurvaPadrao> buscarPorAtividade(List<CurvaPadrao> curvaPadraoEquipeList, Atividade atividade) {

		List<CurvaPadrao> curvaAtividadeList = new ArrayList<CurvaPadrao>();

		for (CurvaPadrao curvaPadrao : curvaPadraoEquipeList) {
			if (curvaPadrao.getAtividade().equals(atividade)) {
				curvaAtividadeList.add(curvaPadrao);
			}
		}

		return curvaAtividadeList;
	}

	private CurvaPadrao buscarCurvaPadrao(Calendar horaEmAnalise, List<CurvaPadrao> curvaPadraoList) {

		Calendar calDataEmAnalise = DateUtils.truncate(horaEmAnalise, Calendar.DATE);

		CurvaPadrao curvaAnalise = null;
		Integer hora = horaEmAnalise.get(Calendar.HOUR_OF_DAY);

		// Verifica data especifica
		for (CurvaPadrao curvaPadrao : curvaPadraoList) {
			Integer horaFinal = curvaPadrao.getHoraFinal().getValor();
			if (horaFinal == 0) {
				horaFinal = 24;
			}
			if (curvaPadrao.getDataEspecifica() != null && curvaPadrao.getDataEspecifica().equals(calDataEmAnalise.getTime())
					&& hora >= curvaPadrao.getHoraInicial().getValor() && hora < horaFinal) {

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
				if (curvaPadrao.getDia() != null && curvaPadrao.getDia().getIntCalendarDayOfWeek().equals(calDataEmAnalise.get(Calendar.DAY_OF_WEEK))
						&& hora >= curvaPadrao.getHoraInicial().getValor() && hora < horaFinal) {
					curvaAnalise = curvaPadrao;
					break;
				}
			}

		}

		return curvaAnalise;
	}

	public List<TaxaOcupacaoSemanaDTO> buscarTaxaOcupacaoSemanal(Date pDataInicial, Date pDataFinal, Long idEquipe, Long idAtividade) {

		Date dataIni = DateUtils.truncate(pDataInicial, Calendar.DATE);
		Date dataFim = DateUtils.truncate(pDataFinal, Calendar.DATE);

		Equipe equipe = equipeDAO.findByIdFetch(idEquipe);
		Atividade atividade = atividadeDAO.findById(idAtividade);

		Calendar diaAnteriorInicio = Calendar.getInstance();
		diaAnteriorInicio.setTime(dataIni);
		diaAnteriorInicio.add(Calendar.DAY_OF_MONTH, -1);

		List<Escala> escalaPeriodoList = escalaService.obterEscalasPorEquipeEPeriodo(equipe.getId(), diaAnteriorInicio.getTime(), dataFim);
		List<CurvaPadrao> curvaPadraoEquipeList = curvaPadraoDAO.buscarPorAtividadeSubAtividade(atividade);

		List<TaxaOcupacaoSemanaDTO> taxaOcupacaoSemanalList = gerarTaxaOcupacaoSemanaDTO(dataIni, dataFim);

		try {
			for (TaxaOcupacaoSemanaDTO taxaOcupacaoSemanaDTO : taxaOcupacaoSemanalList) {
				Calendar calendarHorario = DateUtils.truncate(Calendar.getInstance(), Calendar.DATE);

				// Processa as horas do dia
				do {
					// A cada hora faz o calculo pra os dias da semana.
					RelatorioTaxaOcupacaoDetalheHoraDTO horaDetalhe = gerarDetalhamentoOcupacaoHora(escalaPeriodoList, curvaPadraoEquipeList, calendarHorario,
							taxaOcupacaoSemanaDTO);
					taxaOcupacaoSemanaDTO.getDetalhamentoSemanaList().add(horaDetalhe);

					calendarHorario.add(Calendar.HOUR_OF_DAY, 1);
				} while (calendarHorario.get(Calendar.HOUR_OF_DAY) != 0);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return taxaOcupacaoSemanalList;
	}

	private RelatorioTaxaOcupacaoDetalheHoraDTO gerarDetalhamentoOcupacaoHora(List<Escala> escalaPeriodoList, List<CurvaPadrao> curvaPadraoList, Calendar calendarHorario,
			TaxaOcupacaoSemanaDTO taxaOcupacaoSemanaDTO) {

		RelatorioTaxaOcupacaoDetalheHoraDTO horaDetalhe = new RelatorioTaxaOcupacaoDetalheHoraDTO();
		horaDetalhe.setHoraInicial(calendarHorario.get(Calendar.HOUR_OF_DAY));
		// A cada hora faz o calculo pra os dias da semana.

		Date inicioSemana = DateUtils.truncate(taxaOcupacaoSemanaDTO.getDataInicio(), Calendar.DATE);
		Calendar fimSemana = Calendar.getInstance();
		fimSemana.setTime(DateUtils.truncate(taxaOcupacaoSemanaDTO.getDataFim(), Calendar.DATE));
		// Seta o horario em processamento para a condição do while nao ter horarios distintos
		fimSemana.set(Calendar.HOUR_OF_DAY, calendarHorario.get(Calendar.HOUR_OF_DAY));

		// Inicia da data inicial da semana
		Calendar horaEmAnalise = Calendar.getInstance();
		horaEmAnalise.setTime(inicioSemana);
		// Seta o horario em processamento
		horaEmAnalise.set(Calendar.HOUR_OF_DAY, calendarHorario.get(Calendar.HOUR_OF_DAY));

		do {

			// Pegar curva padrão dessa data
			CurvaPadrao curvaEmAnalise = buscarCurvaPadrao(horaEmAnalise, curvaPadraoList);

			if (curvaEmAnalise != null) {
				// Calcular quantos empregados estão escalados nesse dia/hora
				Integer quantidadeEscalados = 0;

				Integer qtdHoras = curvaEmAnalise.getQuantidadeHoras();
				Double qtdChamadosPorPessoaPorHora = 60 / curvaEmAnalise.getTempoAtendimento().doubleValue();
				Double mediaChamadosPorHora = curvaEmAnalise.getQuantidadeChamados() / qtdHoras.doubleValue();
				Double qtdPessoasNecessarias = mediaChamadosPorHora / qtdChamadosPorPessoaPorHora;
				int qtdPessoasNecessariasArredondado = (int) Math.ceil(qtdPessoasNecessarias);

				for (Escala escala : escalaPeriodoList) {
					if (!escala.getInicio().after(horaEmAnalise.getTime()) && escala.getFim().after(horaEmAnalise.getTime())) {
						quantidadeEscalados++;
					}
				}

				switch (horaEmAnalise.get(Calendar.DAY_OF_WEEK)) {
				case Calendar.MONDAY:
					horaDetalhe.setSegundaOcupacao(quantidadeEscalados);
					horaDetalhe.setSegundaCurvaEsperada(qtdPessoasNecessariasArredondado);
					break;
				case Calendar.TUESDAY:
					horaDetalhe.setTercaOcupacao(quantidadeEscalados);
					horaDetalhe.setTercaCurvaEsperada(qtdPessoasNecessariasArredondado);
					break;
				case Calendar.WEDNESDAY:
					horaDetalhe.setQuartaOcupacao(quantidadeEscalados);
					horaDetalhe.setQuartaCurvaEsperada(qtdPessoasNecessariasArredondado);
					break;
				case Calendar.THURSDAY:
					horaDetalhe.setQuintaOcupacao(quantidadeEscalados);
					horaDetalhe.setQuintaCurvaEsperada(qtdPessoasNecessariasArredondado);
					break;
				case Calendar.FRIDAY:
					horaDetalhe.setSextaOcupacao(quantidadeEscalados);
					horaDetalhe.setSextaCurvaEsperada(qtdPessoasNecessariasArredondado);
					break;
				case Calendar.SATURDAY:
					horaDetalhe.setSabadoOcupacao(quantidadeEscalados);
					horaDetalhe.setSabadoCurvaEsperada(qtdPessoasNecessariasArredondado);
					break;
				case Calendar.SUNDAY:
					horaDetalhe.setDomingoOcupacao(quantidadeEscalados);
					horaDetalhe.setDomingoCurvaEsperada(qtdPessoasNecessariasArredondado);
					break;
				default:
					break;
				}
			}

			horaEmAnalise.add(Calendar.DAY_OF_WEEK, 1);
		} while (!horaEmAnalise.after(fimSemana));

		return horaDetalhe;
	}

	/**
	 * Recebe uma data inicio e fim, e quebra em semanas.
	 */
	public List<TaxaOcupacaoSemanaDTO> gerarTaxaOcupacaoSemanaDTO(Date pDataInicial, Date pDataFinal) {

		List<TaxaOcupacaoSemanaDTO> taxaOcupacaoSemanalList = new ArrayList<TaxaOcupacaoSemanaDTO>();

		Date dataInicioSemana = pDataInicial;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataInicioSemana);

		TaxaOcupacaoSemanaDTO taxaOcupacaoSemanaDTO = new TaxaOcupacaoSemanaDTO();
		taxaOcupacaoSemanalList.add(taxaOcupacaoSemanaDTO);
		taxaOcupacaoSemanaDTO.setDataInicio(dataInicioSemana);
		taxaOcupacaoSemanaDTO.setDataFim(calendar.getTime());
		calendar.add(Calendar.DAY_OF_WEEK, 1);
		calendar = DateUtils.truncate(calendar, Calendar.DATE);

		while (!calendar.getTime().after(pDataFinal)) {

			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
				taxaOcupacaoSemanaDTO = new TaxaOcupacaoSemanaDTO();
				taxaOcupacaoSemanaDTO.setDataInicio(calendar.getTime());
				taxaOcupacaoSemanaDTO.setDataFim(calendar.getTime());
				taxaOcupacaoSemanalList.add(taxaOcupacaoSemanaDTO);
			} else {
				taxaOcupacaoSemanaDTO.setDataFim(calendar.getTime());
			}

			calendar.add(Calendar.DAY_OF_WEEK, 1);
			calendar = DateUtils.truncate(calendar, Calendar.DATE);
		}

		return taxaOcupacaoSemanalList;
	}

	public List<Atividade> findEquipeAtividadeByEquipe(Equipe equipe) {
		List<EquipeAtividade> equipeAtividadeList = atividadeDAO.findEquipeAtividadeByEquipe(equipe);
		List<Atividade> atividadeList = new ArrayList<Atividade>();

		for (EquipeAtividade equipeAtividade : equipeAtividadeList) {
			Atividade atividade = equipeAtividade.getAtividade();
			// Não pode ser pai
			if (atividade.getAtivo() && atividade.getAtividadeList() == null || atividade.getAtividadeList().isEmpty()) {
				atividadeList.add(atividade);
			} else {
				for (Atividade atividadeFilha : atividade.getAtividadeList()) {
					if (atividadeFilha.getAtivo()) {
						atividadeList.add(atividadeFilha);
					}
				}
			}
		}

		Collections.sort(atividadeList, new AtividadeNomeComparator());
		return atividadeList;
	}

}
