package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang.time.DateUtils;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.StreamedContent;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.util.DateUtil;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.JasperReportUtils;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.sicrv.comparator.AnoMesComparator;
import br.gov.caixa.gitecsa.sicrv.enumerator.SituacaoFolgaEnum;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.Escala;
import br.gov.caixa.gitecsa.sicrv.model.dto.RelatorioEscalaDTO;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.DiaDTO;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.MesDTO;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.SemanaDTO;
import br.gov.caixa.gitecsa.sicrv.service.EmpregadoService;
import br.gov.caixa.gitecsa.sicrv.service.EquipeService;
import br.gov.caixa.gitecsa.sicrv.service.EscalaService;
import br.gov.caixa.gitecsa.sicrv.util.Constantes;

@Named
@ViewScoped
public class RelatorioEscalaController extends BaseController implements Serializable {

	private static final long serialVersionUID = -4438337556151771799L;

	private Boolean exibirGerarRelatorio;

	private Date dataInicio;

	private Date dataFim;

	private List<MesDTO> mesDTOList;

	@Inject
	private EmpregadoService empregadoService;

	@Inject
	private EscalaService escalaService;

	@Inject
	private EquipeService equipeService;

	private Equipe equipe;

	private Date diaDetalhe;

	private List<Empregado> empregadosEscaladosList;

	private HashMap<Empregado, String> empregadoHorarioMapa;

	private List<Equipe> listEquipe;

	// @Inject
	// private transient Logger logger;

	@PostConstruct
	private void init() {
		Empregado empregado = empregadoService.obterEmpregadoPorMatricula((String) JSFUtil.getSessionMapValue("loggedMatricula"));
		this.exibirGerarRelatorio = Boolean.FALSE;
		empregado = empregadoService.findByIdFetch(empregado.getId());

		listEquipe = new ArrayList<Equipe>();

		if (empregado.getEquipeEmpregadoAtivo() != null) {
			equipe = empregado.getEquipeEmpregadoAtivo().getEquipe();
			listEquipe.add(equipe);
		} else {
			facesMessager.addMessageError("O empregado não está ativo em uma equipe.");
		}

	}

	public void detalharDia() throws ParseException {

		String diaParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("dia");

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		this.diaDetalhe = sdf.parse(diaParam);

		Equipe equipe = equipeService.findByIdFetch(this.equipe.getId());

		empregadoHorarioMapa = new HashMap<Empregado, String>();

		List<Escala> escalaList = escalaService.obterEscalasPorEquipeEData(equipe.getId(), diaDetalhe);

		empregadosEscaladosList = new ArrayList<Empregado>();
		for (Escala escala : escalaList) {
			empregadosEscaladosList.add(escala.getEquipeEmpregado().getEmpregado());
			empregadoHorarioMapa.put(escala.getEquipeEmpregado().getEmpregado(), escala.getHorarioEscalaExibidoNoturnoIncluso(":"));
		}

	}

	public String buscarEscalas() {
		this.exibirGerarRelatorio = Boolean.FALSE;
		this.mesDTOList = new ArrayList<MesDTO>();

		if (validarCampos()) {
			carregarDadosCalendarioEscala();
		}
		return null;
	}

	private void carregarDadosCalendarioEscala() {
		this.mesDTOList = new ArrayList<MesDTO>();

		List<SituacaoFolgaEnum> situacaoFolgaList = new ArrayList<SituacaoFolgaEnum>();
		situacaoFolgaList.add(SituacaoFolgaEnum.AGENDADA);
		situacaoFolgaList.add(SituacaoFolgaEnum.SUGERIDA);

		List<Date> listaDatasComEscala = escalaService.buscarDatasFolgasAPartir(equipe, dataInicio, dataFim);

		// Pegar os meses entre as datas
		Calendar calInicio = Calendar.getInstance();
		Calendar calFim = Calendar.getInstance();

		calInicio.setTime(DateUtils.truncate(dataInicio, Calendar.DATE));
		calFim.setTime(DateUtils.truncate(dataFim, Calendar.DATE));

		// GERAR OS MESES ENTRE AS DATAS DA TELA
		Set<String> anoMesSet = new HashSet<String>();

		Calendar calMeses = Calendar.getInstance();
		calMeses.setTime(calInicio.getTime());
		do {
			anoMesSet.add(calMeses.get(Calendar.YEAR) + "/" + calMeses.get(Calendar.MONTH));
			calMeses.add(Calendar.MONTH, 1);
		} while (calMeses.getTime().before(calFim.getTime()));
		anoMesSet.add(calFim.get(Calendar.YEAR) + "/" + calFim.get(Calendar.MONTH));

		List<String> anoMesList = new ArrayList<String>(anoMesSet);
		Collections.sort(anoMesList, new AnoMesComparator());

		// EM CADA MES VAI GERAR AS SEMANAS
		for (String string : anoMesList) {
			MesDTO mesDTO = new MesDTO();
			Integer ano = Integer.parseInt(string.split("/")[0]);
			Integer mes = Integer.parseInt(string.split("/")[1]);

			mesDTO.setAnoInt(ano);
			mesDTO.setMesInt(mes);

			Calendar calPercorreMesDTO = Calendar.getInstance();
			calPercorreMesDTO.set(ano, mes, calPercorreMesDTO.getActualMinimum(Calendar.DAY_OF_MONTH));
			calPercorreMesDTO = DateUtils.truncate(calPercorreMesDTO, Calendar.DATE);

			Calendar calInicioMesDTO = Calendar.getInstance();
			calInicioMesDTO.setTime(calPercorreMesDTO.getTime());

			Calendar calFimMesDTO = Calendar.getInstance();
			calFimMesDTO.setTime(calPercorreMesDTO.getTime());
			calFimMesDTO.set(Calendar.DAY_OF_MONTH, calFimMesDTO.getActualMaximum(Calendar.DAY_OF_MONTH));

			// AGORA TEM QUE VOLTAR ATÉ ACHAR A PRIMEIRA SEGUNDA FEIRA ANTERIOR
			while (calPercorreMesDTO.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				calPercorreMesDTO.add(Calendar.DAY_OF_MONTH, -1);
			}

			SemanaDTO semanaDTO = new SemanaDTO();

			while (calPercorreMesDTO.getTime().before(calFimMesDTO.getTime()) || calPercorreMesDTO.getTime().equals(calFimMesDTO.getTime())) {

				if (calPercorreMesDTO.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
					semanaDTO = new SemanaDTO();
					mesDTO.getSemanaDTOList().add(semanaDTO);
				}

				// Dias Anteriores a dataInicio
				DiaDTO diaDTO = new DiaDTO();
				if (!calPercorreMesDTO.before(calInicioMesDTO) && !calPercorreMesDTO.before(calInicio) && !calPercorreMesDTO.after(calFim)) {
					diaDTO.setNumeroDia(String.valueOf(calPercorreMesDTO.get(Calendar.DAY_OF_MONTH)));
					diaDTO.setData(calPercorreMesDTO.getTime());

					diaDTO.setClasseCss("diaUtilTrabalhado");
					if (listaDatasComEscala.contains(DateUtils.truncate(calPercorreMesDTO.getTime(), Calendar.DATE))) {
						diaDTO.setClasseCss("colorEscala");
						this.exibirGerarRelatorio = Boolean.TRUE;
					}

				}

				semanaDTO.getDiaDTOList().add(diaDTO);
				calPercorreMesDTO.add(Calendar.DAY_OF_MONTH, 1);
				calPercorreMesDTO.set(Calendar.HOUR_OF_DAY, 0);
			}

			if (calPercorreMesDTO.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {

				// APOS PREENCHER OS DIAS NORMAIS, CONTINUA ATE ACHAR UM DOMINGO
				while (calPercorreMesDTO.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
					DiaDTO diaDTO = new DiaDTO();
					semanaDTO.getDiaDTOList().add(diaDTO);
					calPercorreMesDTO.add(Calendar.DAY_OF_MONTH, 1);
				}
			}

			this.mesDTOList.add(mesDTO);
		}

	}

	private boolean validarCampos() {

		if (Util.isNullOuVazio(dataInicio)) {
			facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("geral.label.data.inicio")));
			return false;
		}

		if (Util.isNullOuVazio(dataFim)) {
			facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("geral.label.data.fim")));
			return false;
		}

		if (!dataFim.after(dataInicio)) {
			facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.validation.data.incio.menorIgual.data.fim"));
			return false;
		}

		return true;
	}

	/**
	 * Geração do Relatório em PDF paisagem.
	 */
	public StreamedContent downloadPdf() throws JRException {
		HashMap<String, Object> params = new HashMap<String, Object>();

		params.put("LOGO_CAIXA", String.format("%slogocaixa2.png", Constantes.REPORT_IMG_DIR));
		params.put("PARAM_EQUIPE", equipe.getNome());
		params.put("PARAM_UNIDADE", "CECAC - CN SEGURANCA EM CARTAO DE CREDITO");
		params.put("PARAM_DATA_INICIO", dataInicio);
		params.put("PARAM_DATA_FIM", dataFim);

		Calendar primeiroDia = Calendar.getInstance();
		primeiroDia.setTime(this.dataInicio);
		Calendar ultimoDia = Calendar.getInstance();
		ultimoDia.setTime(this.dataFim);
		List<RelatorioEscalaDTO> listaRelEscala = new ArrayList<RelatorioEscalaDTO>();

		List<PeriodoDTO> listaPeriodo = listaSemanasFracionadasPorPeriodo(primeiroDia, ultimoDia);
		for (PeriodoDTO periodoDTO : listaPeriodo) {
			listaRelEscala.addAll(obterEscalaSemanalPorEquipe(periodoDTO.getInicio(), periodoDTO.getFim()));
		}
		InputStream jasper = getClass().getClassLoader().getResourceAsStream(String.format("%sRelatorioEscalaSemanal.jasper", Constantes.REPORT_BASE_DIR));
		return JasperReportUtils.run(jasper, "Escala.pdf", params, listaRelEscala);
	}

	/** Método para obter e montar a escala da equipe para cada semana da segunda até o domingo. */
	private List<RelatorioEscalaDTO> obterEscalaSemanalPorEquipe(Date primeiroDiaSemana, Date ultimoDiaSemana) {
		List<RelatorioEscalaDTO> listaRelEscala = new ArrayList<RelatorioEscalaDTO>();
		this.montagemDaListaPadraoSemHorarios(listaRelEscala);
		List<Escala> listaEscalas = escalaService.obterEscalasPorEquipeEPeriodo(equipe.getId(), primeiroDiaSemana, ultimoDiaSemana);
		RelatorioEscalaDTO temp;
		String horaConcatenada = "";
		Calendar primeiroDia = Calendar.getInstance();
		Calendar ultimoDia = Calendar.getInstance();
		for (Escala escala : listaEscalas) {
			temp = new RelatorioEscalaDTO();
			for (RelatorioEscalaDTO relatorioEscalaDTO : listaRelEscala) {
				if (relatorioEscalaDTO.getIdEmpregado() == escala.getEquipeEmpregado().getEmpregado().getId()) {
					temp = relatorioEscalaDTO;
					break;
				}
			}
			primeiroDia.setTime(escala.getInicio());
			ultimoDia.setTime(escala.getFim());
			horaConcatenada = obterHoraConcatenadaParaRelatorio(primeiroDia, ultimoDia);

			if (primeiroDia.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				temp.setDomingo(horaConcatenada);
			} else if (primeiroDia.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
				temp.setSegunda(horaConcatenada);
			} else if (primeiroDia.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
				temp.setTerca(horaConcatenada);
			} else if (primeiroDia.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
				temp.setQuarta(horaConcatenada);
			} else if (primeiroDia.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
				temp.setQuinta(horaConcatenada);
			} else if (primeiroDia.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
				temp.setSexta(horaConcatenada);
			} else if (primeiroDia.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				temp.setSabado(horaConcatenada);
			}
		}

		/** Adição do campo para agrupar as semanas no relatório. */
		String periodoPorExtenso = "Período: ";
		periodoPorExtenso += DateUtil.format(primeiroDiaSemana, Constantes.DATA_FORMATACAO);
		periodoPorExtenso += " - ";
		periodoPorExtenso += DateUtil.format(ultimoDiaSemana, Constantes.DATA_FORMATACAO);
		for (RelatorioEscalaDTO relEscala : listaRelEscala) {
			relEscala.setPeriodoPorExtenso(periodoPorExtenso);
		}
		return listaRelEscala;
	}

	/** Montagem da hora concatenada */
	private String obterHoraConcatenadaParaRelatorio(Calendar primeiroDia, Calendar ultimoDia) {
		String horaConcatenada = "";
		horaConcatenada += DateUtil.format(primeiroDia.getTime(), DateUtil.TIME_FORMAT);
		horaConcatenada += " - ";
		horaConcatenada += DateUtil.format(ultimoDia.getTime(), DateUtil.TIME_FORMAT);
		return horaConcatenada;
	}

	/** Montagem da tela com os funcionários da equipe e os períodos em branco. */
	private void montagemDaListaPadraoSemHorarios(List<RelatorioEscalaDTO> listaRelEscala) {
		RelatorioEscalaDTO relEscala;
		List<Empregado> listaEmpregados = empregadoService.obterListaEmpregadosAtivosPorEquipe(this.equipe);
		for (Empregado empregado : listaEmpregados) {
			relEscala = new RelatorioEscalaDTO();
			relEscala.setIdEmpregado(empregado.getId());
			relEscala.setNome(empregado.getNome().toUpperCase());
			relEscala.setSegunda(" -- ");
			relEscala.setTerca(" -- ");
			relEscala.setQuarta(" -- ");
			relEscala.setQuinta(" -- ");
			relEscala.setSexta(" -- ");
			relEscala.setSabado(" -- ");
			relEscala.setDomingo(" -- ");
			listaRelEscala.add(relEscala);
		}
	}

	/** Método para fracionar o intervalo de datas por semanas de segunda a domingo */
	public List<PeriodoDTO> listaSemanasFracionadasPorPeriodo(Calendar dtInicio, Calendar dtFim) {
		List<PeriodoDTO> listaPeriodos = new ArrayList<PeriodoDTO>();
		Calendar count = Calendar.getInstance();
		count.setTime(dtInicio.getTime());
		PeriodoDTO periodo = new PeriodoDTO();

		periodo.setInicio(dtInicio.getTime());
		while (!count.after(dtFim)) {
			if (Calendar.SUNDAY == count.get(Calendar.DAY_OF_WEEK)) {
				periodo.setFim(count.getTime());
				listaPeriodos.add(periodo);
				count.add(Calendar.DAY_OF_MONTH, 1);
				if (!count.after(dtFim)) {
					periodo = new PeriodoDTO();
					periodo.setInicio(count.getTime());
				}
			} else {
				count.add(Calendar.DAY_OF_MONTH, 1);
			}
			if (count.equals(dtFim)) {
				periodo.setFim(count.getTime());
				listaPeriodos.add(periodo);
				count.add(Calendar.DAY_OF_MONTH, 1);
			}
		}

		return listaPeriodos;
	}

	public void prepareToResult() throws Exception {

	}

	public String cancelarFolgaSugerida() {
		return null;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public List<MesDTO> getMesDTOList() {
		return mesDTOList;
	}

	public void setMesDTOList(List<MesDTO> mesDTOList) {
		this.mesDTOList = mesDTOList;
	}

	public Date getDiaDetalhe() {
		return diaDetalhe;
	}

	public void setDiaDetalhe(Date diaDetalhe) {
		this.diaDetalhe = diaDetalhe;
	}

	public HashMap<Empregado, String> getEmpregadoHorarioMapa() {
		return empregadoHorarioMapa;
	}

	public void setEmpregadoHorarioMapa(HashMap<Empregado, String> empregadoHorarioMapa) {
		this.empregadoHorarioMapa = empregadoHorarioMapa;
	}

	public List<Empregado> getEmpregadosEscaladosList() {
		return empregadosEscaladosList;
	}

	public void setEmpregadosEscaladosList(List<Empregado> empregadosEscaladosList) {
		this.empregadosEscaladosList = empregadosEscaladosList;
	}

	public Equipe getEquipe() {
		return equipe;
	}

	public void setEquipe(Equipe equipe) {
		this.equipe = equipe;
	}

	public List<Equipe> getListEquipe() {
		return listEquipe;
	}

	public void setListEquipe(List<Equipe> listEquipe) {
		this.listEquipe = listEquipe;
	}

	public Boolean getExibirGerarRelatorio() {
		return exibirGerarRelatorio;
	}

	public void setExibirGerarRelatorio(Boolean exibirGerarRelatorio) {
		this.exibirGerarRelatorio = exibirGerarRelatorio;
	}

}

/** Classe interna apenas para geração da lista de dias por semana para o relatório de escala semanal. */
class PeriodoDTO {

	private Date inicio;
	private Date fim;

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}
}