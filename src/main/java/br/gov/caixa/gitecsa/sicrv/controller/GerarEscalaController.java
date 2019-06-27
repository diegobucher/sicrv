package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.dto.MesEscalaDTO;
import br.gov.caixa.gitecsa.sicrv.security.ControleAcesso;
import br.gov.caixa.gitecsa.sicrv.service.EmpregadoService;
import br.gov.caixa.gitecsa.sicrv.service.EquipeService;
import br.gov.caixa.gitecsa.sicrv.service.EscalaService;

@Named
@ViewScoped
public class GerarEscalaController extends BaseController implements Serializable {

	private static final long serialVersionUID = 1146952451931212504L;

	@Inject
	private EquipeService equipeService;

	@Inject
	private ControleAcesso controleAcesso;

	@Inject
	private EscalaService escalaService;

	@Inject
	private EmpregadoService empregadoService;

	private List<Equipe> listEquipe;

	private Equipe equipe;

	private Date dataInicio;

	private Date dataFim;

	private String dataInicioStr;
	private String dataFimStr;

	private String dataInicioFormatadaStr;
	private String dataFimFormatadaStr;

	private List<String> validacaoInicialList;

	private List<MesEscalaDTO> mesEscalaDTOList;

	private Boolean existeEscalaFutura;

	@PostConstruct
	public void init() {

		listEquipe = new ArrayList<Equipe>();

		Empregado empregado = empregadoService.obterEmpregadoPorMatricula(controleAcesso.getUsuario().getNuMatricula());
		empregado = empregadoService.findByIdFetch(empregado.getId());

		if (empregado.getEquipeEmpregadoAtivo() == null || !empregado.getEquipeEmpregadoAtivo().getSupervisor()) {
			facesMessager.addMessageError("O Funcionário não pode gerar escala pois não é Supervisor!");

		} else {
			equipe = empregado.getEquipeEmpregadoAtivo().getEquipe();
			listEquipe.add(equipe);

			try {

				buscarEscalasGeradas(equipe);
				escalaService.validarAvaliacaoInicial(equipe);
			} catch (BusinessException e) {
				this.validacaoInicialList = e.getErroList();
			}
		}

	}

	public String reverterEscala() throws Exception {

		if (dataInicio != null) {
			escalaService.deletarEscalaExistente(equipe, dataInicio);

			return gerarEscala();
		}

		return null;
	}

	private void buscarEscalasGeradas(Equipe equipe) {
		try {
			this.mesEscalaDTOList = new ArrayList<MesEscalaDTO>();
			Object[] dataInicioFim = escalaService.obterDatasPeriodoEscalasFuturasPorEquipeInclusoHoje(equipe.getId());

			Date dataIni = (Date) dataInicioFim[0];
			Date dataFim = (Date) dataInicioFim[1];

			if (dataIni != null & dataFim != null) {

				Calendar calIni = Calendar.getInstance();
				calIni.setTime(dataIni);

				while (calIni.getTime().before(dataFim)) {

					Calendar calUltimoDiaMesFim = Calendar.getInstance();
					calUltimoDiaMesFim.setTime(calIni.getTime());
					Integer maximoDia = calIni.getActualMaximum(Calendar.DAY_OF_MONTH);
					calUltimoDiaMesFim.set(Calendar.DAY_OF_MONTH, maximoDia);

					MesEscalaDTO mesEscalaDTO = null;

					if (calUltimoDiaMesFim.getTime().before(dataFim)
							&& (calIni.getTime().equals(dataIni) || calIni.getActualMinimum(Calendar.DAY_OF_MONTH) == calIni.get(Calendar.DAY_OF_MONTH))) {
						mesEscalaDTO = new MesEscalaDTO(calIni.getTime(), calUltimoDiaMesFim.getTime());
						this.mesEscalaDTOList.add(mesEscalaDTO);

						// Ultimo Mês
					} else if (!calUltimoDiaMesFim.getTime().before(dataFim)) {
						mesEscalaDTO = new MesEscalaDTO(calIni.getTime(), dataFim);
						this.mesEscalaDTOList.add(mesEscalaDTO);
						break;
					}

					calIni.add(Calendar.DAY_OF_MONTH, 1);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String gerarEscala() throws Exception {
		try {

			if (validarDados()) {
				equipe = equipeService.findByIdFetch(equipe.getId());
				escalaService.gerarEscalaRevezamento(equipe, dataInicio, dataFim);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				setDataInicioStr(sdf.format(dataInicio));
				setDataFimStr(sdf.format(dataFim));
				return "/pages/escalaTrabalho/editarEscala.xhtml?faces-redirect=true&idEquipe=" + equipe.getId() + "&dtIni=" + getDataInicioStr() + "&dtFim="
						+ getDataFimStr();
				// return "/pages/escalaTrabalho/editarEscala.xhtml?faces-redirect=true&includeViewParams=true";
			}
		} catch (BusinessException be) {
			for (String msg : be.getErroList()) {
				facesMessager.addMessageError(msg);
			}
		} catch (Exception e) {
			facesMessager.addMessageError(e.getMessage());
		}
		return null;
	}

	public String visualizarEscala(MesEscalaDTO mesEscalaDTO) throws Exception {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			setDataInicioStr(sdf.format(mesEscalaDTO.getDataInicio()));
			setDataFimStr(sdf.format(mesEscalaDTO.getDataFim()));
			return "/pages/escalaTrabalho/editarEscala.xhtml?faces-redirect=true&idEquipe=" + equipe.getId() + "&dtIni=" + getDataInicioStr() + "&dtFim=" + getDataFimStr();
			// return "/pages/escalaTrabalho/editarEscala.xhtml?faces-redirect=true&includeViewParams=true";

		} catch (Exception e) {
			facesMessager.addMessageError(e.getMessage());
		}
		return null;
	}

	private Boolean validarDados() {

		this.existeEscalaFutura = false;

		if (Util.isNullOuVazio(dataInicio)) {
			facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("geral.label.data.inicio")));
			return false;
		}

		if (Util.isNullOuVazio(dataFim)) {
			facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("geral.label.data.fim")));
			return false;
		}

		Calendar calInicio = Calendar.getInstance();
		Calendar calFim = Calendar.getInstance();

		if (!dataFim.after(dataInicio)) {
			facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.validation.data.inicio.menor.data.fim"));
			return false;
		}

		calInicio.setTime(dataInicio);
		calFim.setTime(dataFim);

		if (calInicio.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			facesMessager.addMessageError(MensagemUtil.obterMensagem("gerarEscala.label.dataInicio.segunda"));
			return false;
		}

		if (calFim.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			facesMessager.addMessageError(MensagemUtil.obterMensagem("gerarEscala.label.dataFim.domingo"));
			return false;
		}

		Date hoje = new Date();
		if (hoje.after(dataInicio)) {
			facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.validation.data.inicio.menor.hoje"));
			return false;
		}

		if (escalaService.existeEscalasFuturasPorEquipe(equipe.getId(), this.dataInicio)) {
			existeEscalaFutura = true;
			RequestContext.getCurrentInstance().execute("reverterModal.show()");
			return false;
		}

		return true;

	}

	public String atualizarCamposData() {

		SimpleDateFormat sdfAmericano = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfBrasileiro = new SimpleDateFormat("dd/MM/yyyy");
		try {
			dataInicio = sdfAmericano.parse(dataInicioStr);
			dataFim = sdfAmericano.parse(dataFimStr);

			dataInicioFormatadaStr = sdfBrasileiro.format(dataInicio);
			dataFimFormatadaStr = sdfBrasileiro.format(dataFim);
		} catch (ParseException e) {
		}

		return null;
	}

	public Equipe getEquipe() {
		return equipe;
	}

	public void setEquipe(Equipe equipe) {
		this.equipe = equipe;
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

	public List<Equipe> getListEquipe() {
		return listEquipe;
	}

	public void setListEquipe(List<Equipe> listEquipe) {
		this.listEquipe = listEquipe;
	}

	public String getDataInicioStr() {
		return dataInicioStr;
	}

	public void setDataInicioStr(String dataInicioStr) {
		this.dataInicioStr = dataInicioStr;
	}

	public String getDataFimStr() {
		return dataFimStr;
	}

	public void setDataFimStr(String dataFimStr) {
		this.dataFimStr = dataFimStr;
	}

	public List<String> getValidacaoInicialList() {
		return validacaoInicialList;
	}

	public String getDataInicioFormatadaStr() {
		return dataInicioFormatadaStr;
	}

	public void setDataInicioFormatadaStr(String dataInicioFormatadaStr) {
		this.dataInicioFormatadaStr = dataInicioFormatadaStr;
	}

	public String getDataFimFormatadaStr() {
		return dataFimFormatadaStr;
	}

	public void setDataFimFormatadaStr(String dataFimFormatadaStr) {
		this.dataFimFormatadaStr = dataFimFormatadaStr;
	}

	public List<MesEscalaDTO> getMesEscalaDTOList() {
		return mesEscalaDTOList;
	}

	public void setMesEscalaDTOList(List<MesEscalaDTO> mesEscalaDTOList) {
		this.mesEscalaDTOList = mesEscalaDTOList;
	}

	public Boolean getExisteEscalaFutura() {
		return existeEscalaFutura;
	}

	public void setExisteEscalaFutura(Boolean existeEscalaFutura) {
		this.existeEscalaFutura = existeEscalaFutura;
	}

}
