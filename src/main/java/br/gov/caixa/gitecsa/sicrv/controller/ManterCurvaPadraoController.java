package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseViewCrud;
import br.gov.caixa.gitecsa.arquitetura.controller.ContextoController;
import br.gov.caixa.gitecsa.arquitetura.service.BaseService;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.sicrv.enumerator.DiaSemanaEnum;
import br.gov.caixa.gitecsa.sicrv.enumerator.HoraFixaEnum;
import br.gov.caixa.gitecsa.sicrv.enumerator.MotivoAusenciaEmpregadoEnum;
import br.gov.caixa.gitecsa.sicrv.enumerator.PeriodoEnum;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.CurvaPadrao;
import br.gov.caixa.gitecsa.sicrv.security.ControleAcesso;
import br.gov.caixa.gitecsa.sicrv.service.AtividadeService;
import br.gov.caixa.gitecsa.sicrv.service.CurvaPadraoService;

@Named
@ViewScoped
public class ManterCurvaPadraoController extends BaseViewCrud<CurvaPadrao> implements Serializable {

	private static final long serialVersionUID = -2390322905324353290L;

	public static final String EDITAR = "EDITAR";

	@Inject
	private ContextoController contextoController;
	@Inject
	private CurvaPadraoService curvaPadraoService;
	@Inject
	private ControleAcesso controleAcesso;
	@Inject
	private AtividadeService atividadeService;

	/**
	 * Atividade Selecionada.
	 */
	private Atividade atividade;

	/**
	 * SubAtividade Selecionada.
	 */
	private Atividade subAtividade;

	/**
	 * Flag para marcar se existe duplicidade para exibir o modal de confirmação.
	 */
	private Boolean existeDuplicidade;
	private Boolean modoEdicao;

	/**
	 * Lista do combo de Atividade. (Atividades Pai).
	 */
	private List<Atividade> listaAtividade;

	/**
	 * Lista do combo de SubAtividades.
	 */
	private List<Atividade> listaSubAtividade;

	/**
	 * Lista de Curva Padrão que chocaram com a Curva atual. E serão removidadas caso o Usuario confirme.
	 */
	private List<CurvaPadrao> listaCurvaPadraoRemocao;

	@PostConstruct
	public void init() {
		limparTela();
		if (getInstance() == null) {
			newInstance();
		}
		this.listaSubAtividade = new ArrayList<Atividade>();
		this.listaAtividade = atividadeService.findAtividadesPai(controleAcesso.getUnidade());
		this.listaCurvaPadraoRemocao = new ArrayList<CurvaPadrao>();
		setInstance((CurvaPadrao) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("curvaPadrao"));
		if (getInstance() != null && getInstance().getId() != null) {
			CurvaPadrao curva = curvaPadraoService.findByIdFetchAll(getInstance().getId());
			Atividade atividadeCurva = atividadeService.findByIdFetchAll(curva.getAtividade().getId());
			if (atividadeCurva != null && atividadeCurva.getAtividadePai() != null) {
				this.atividade = atividadeService.findByIdFetchAll(atividadeCurva.getAtividadePai().getId());
				obterListaSubAtividade();
				this.subAtividade = atividadeCurva;
			} else {
				this.atividade = atividadeCurva;
				this.subAtividade = null;
			}
			setInstance(curva);
		} else {
			getInstance().setPeriodo(PeriodoEnum.DIA_DA_SEMANA);
		}
		if (contextoController.getObjectFilter() != null && ((FiltroDTO) this.contextoController.getObjectFilter()).getModoEdicao() != null) {
			this.modoEdicao = ((FiltroDTO) this.contextoController.getObjectFilter()).getModoEdicao();
		}
	}

	public void obterListaSubAtividade() {
		this.listaSubAtividade = new ArrayList<Atividade>();
		if (this.atividade != null && this.atividade.getId() != null) {
			this.listaSubAtividade = atividadeService.findAtividadesFilhas(this.atividade.getId());
		}
	}

	public List<PeriodoEnum> getListaPeriodo() {
		return Arrays.asList(PeriodoEnum.values());
	}

	public List<DiaSemanaEnum> getListaDias() {
		return Arrays.asList(DiaSemanaEnum.values());
	}

	public List<HoraFixaEnum> getListaHoras() {
		return Arrays.asList(HoraFixaEnum.values());
	}

	public String salvarSubstituindo() {
		if (this.listaCurvaPadraoRemocao != null && !this.listaCurvaPadraoRemocao.isEmpty()) {
			curvaPadraoService.removerLista(listaCurvaPadraoRemocao);
		}
		salvarPadrao();
		return redirectTelaConsulta();
	}

	public String salvar() {
		if (validarCampos()) {
			salvarPadrao();
			return redirectTelaConsulta();
		}
		return null;
	}

	private void salvarPadrao() {
		if (this.listaSubAtividade != null && !this.listaSubAtividade.isEmpty()) {
			if (this.subAtividade != null) {
				Atividade atividadeCadastrada = atividadeService.findById(this.subAtividade.getId());
				gravarEditarCurvaPadrao(atividadeCadastrada);
			} else {
				for (Atividade item : this.listaSubAtividade) {
					Atividade atividadeCadastrada = atividadeService.findById(item.getId());
					gravarEditarCurvaPadrao(atividadeCadastrada);
				}
			}
		} else {
			Atividade atividadeCadastrada = atividadeService.findById(this.atividade.getId());
			gravarEditarCurvaPadrao(atividadeCadastrada);
		}
		obterMensagemRetorno();
	}

	private void obterMensagemRetorno() {
		FiltroDTO filtroDTO = (FiltroDTO) contextoController.getObjectFilter();
		if (filtroDTO.getModoEdicao()) {
			contextoController.setCrudMessage(MensagemUtil.obterMensagem("curvaPadrao.mensagem.curvaPadraoAlteradoComSucesso"));
		} else {
			contextoController.setCrudMessage(MensagemUtil.obterMensagem("curvaPadrao.mensagem.curvaPadraoSalvoComSucesso"));
		}
	}

	private void gravarEditarCurvaPadrao(Atividade atividadeCadastrada) {

		if (atividadeCadastrada != null && getInstance() != null) {
			getInstance().setAtividade(atividadeCadastrada);
			getInstance().setIc_ativo(Boolean.TRUE);

			DiaSemanaEnum diaSemanaEnumInstance = trataDataParaDiaDaSemana();

			if (PeriodoEnum.DIA_DA_SEMANA.equals(getInstance().getPeriodo()) && getInstance().getDia() == null) {
				List<DiaSemanaEnum> listaDias = getListaDias();
				for (DiaSemanaEnum diaSemanaEnum : listaDias) {
					CurvaPadrao curva = new CurvaPadrao();
					curva = curvaCloneSemId(getInstance());
					curva.setDia(diaSemanaEnum);
					curva.setDataEspecifica(null);

					if (diaSemanaEnumInstance != null && diaSemanaEnum.equals(diaSemanaEnumInstance) && getInstance().getId() != null) {
						curva.setId(getInstance().getId());
					}

					curvaPadraoService.salvar(curva);
				}
			} else if (PeriodoEnum.DIA_DA_SEMANA.equals(getInstance().getPeriodo()) && getInstance().getDia() != null) {
				CurvaPadrao curva = new CurvaPadrao();
				curva = curvaCloneSemId(getInstance());
				curva.setDataEspecifica(null);

				curva.setId(getInstance().getId());

				curvaPadraoService.salvar(curva);
			} else if (PeriodoEnum.DATA_ESPECIFICA.equals(getInstance().getPeriodo()) && getInstance().getDataEspecifica() != null) {
				if (getInstance() != null && getInstance().getId() != null) {
					getInstance().setDia(null);
					curvaPadraoService.salvar(getInstance());
				} else {
					CurvaPadrao curva = new CurvaPadrao();
					curva = curvaCloneSemId(getInstance());
					curva.setDia(null);

					curva.setId(getInstance().getId());

					curvaPadraoService.salvar(curva);
				}
			}
		}
	}

	private boolean validarCampos() {

		if (this.atividade == null) {
			getFacesMessager().addMessageError(MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("curvaPadrao.label.atividade")));
			return false;
		}

		if (Util.isNullOuVazio(getInstance().getPeriodo())) {
			getFacesMessager().addMessageError(MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("curvaPadrao.label.tipo")));
			return false;
		} else {
			if (!PeriodoEnum.DIA_DA_SEMANA.equals(getInstance().getPeriodo())) {
				if (Util.isNullOuVazio(getInstance().getDataEspecifica())) {
					getFacesMessager().addMessageError(MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("curvaPadrao.label.data")));
					return false;
				}
			}
		}

		if (Util.isNullOuVazio(getInstance().getQuantidadeChamados())) {
			getFacesMessager().addMessageError(MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("curvaPadrao.label.qtdChamados")));
			return false;
		}
		if (Util.isNullOuVazio(getInstance().getTempoAtendimento())) {
			getFacesMessager().addMessageError(MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("curvaPadrao.label.atendimento")));
			return false;
		}

		if (!isIntervaloHorasValido()) {
			return false;
		}

		if (validarDuplicidade()) {
			RequestContext.getCurrentInstance().execute("PF('dlgDuplicidade').show();");
			return false;
		}
		return true;
	}

	private boolean validarDuplicidade() {

		Atividade atividadeCadastrada;
		this.existeDuplicidade = Boolean.FALSE;
		// Caso tenha SubAtividades
		if (this.listaSubAtividade != null && !this.listaSubAtividade.isEmpty()) {
			// Se selecionar uma no combo
			if (this.subAtividade != null) {
				atividadeCadastrada = atividadeService.findById(this.subAtividade.getId());
				validarDuplicidadeCurvaPadrao(atividadeCadastrada);
				// Faz a validação em todas as SubAtividades do combo
			} else {
				for (Atividade item : this.listaSubAtividade) {
					atividadeCadastrada = atividadeService.findById(item.getId());
					validarDuplicidadeCurvaPadrao(atividadeCadastrada);
				}
			}
			// Caso não tenha SubAtividades
		} else {
			atividadeCadastrada = atividadeService.findById(this.atividade.getId());
			validarDuplicidadeCurvaPadrao(atividadeCadastrada);
		}
		if (this.existeDuplicidade) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	private void validarDuplicidadeCurvaPadrao(Atividade atividadeCadastrada) {
		if (atividadeCadastrada != null && getInstance() != null) {
			CurvaPadrao curva = new CurvaPadrao();
			curva = curvaCloneSemId(getInstance());
			curva.setAtividade(atividadeCadastrada);
			curva.setIc_ativo(Boolean.TRUE);
			// Quando Seleciona dia da Semana para TODOS os dias.
			if (PeriodoEnum.DIA_DA_SEMANA.equals(getInstance().getPeriodo()) && getInstance().getDia() == null) {
				List<DiaSemanaEnum> listaDias = getListaDias();
				for (DiaSemanaEnum diaSemanaEnum : listaDias) {
					curva.setDia(diaSemanaEnum);
					garantirUnicidadeCurvaPadrao(curva);
				}
				// Quando Seleciona dia da Semana para UM dia especifico.
			} else if (PeriodoEnum.DIA_DA_SEMANA.equals(getInstance().getPeriodo()) && getInstance().getDia() != null) {
				garantirUnicidadeCurvaPadrao(curva);
				// Quando seleciona a Data Especifica
			} else if (PeriodoEnum.DATA_ESPECIFICA.equals(getInstance().getPeriodo()) && getInstance().getDataEspecifica() != null) {
				if (getInstance() != null && getInstance().getId() != null) {
					garantirUnicidadeCurvaPadrao(curva);
				} else {
					garantirUnicidadeCurvaPadrao(curva);
				}
			}
		}
	}

	private void garantirUnicidadeCurvaPadrao(CurvaPadrao curva) {
		List<CurvaPadrao> resultado = curvaPadraoService.listaGarantirUnicidade(curva);
		if (!resultado.isEmpty()) {
			for (CurvaPadrao item : resultado) {
				this.listaCurvaPadraoRemocao.add(item);
			}
			this.existeDuplicidade = Boolean.TRUE;
		} else {
			this.existeDuplicidade = Boolean.FALSE;
		}
	}

	public void limparTela() {
		this.atividade = new Atividade();
		this.modoEdicao = Boolean.FALSE;
		newInstance();
		getInstance().setPeriodo(PeriodoEnum.DIA_DA_SEMANA);
	}

	public List<MotivoAusenciaEmpregadoEnum> getListaMotivos() {
		return Arrays.asList(MotivoAusenciaEmpregadoEnum.values());
	}

	public String redirectTelaConsulta() {
		return "/pages/curvaPadrao/consulta.xhtml?faces-redirect=true";
	}

	public String getBreadcrumb() {
		String titulo = "Nova Curva Padrão";

		if (this.getInstance().getId() != null && Boolean.TRUE.equals(this.modoEdicao)) {
			titulo = "Edição Curva Padrão";
		} else if (this.getInstance().getId() != null && Boolean.FALSE.equals(this.modoEdicao)) {
			titulo = "Detalhes Curva Padrão";
		}
		return titulo;
	}

	public String ajaxHandler() {
		return null;
	}

	/** Getters and Setters */
	public Atividade getAtividade() {
		return atividade;
	}

	public void setAtividade(Atividade atividade) {
		this.atividade = atividade;
	}

	public List<Atividade> getListaAtividade() {
		return listaAtividade;
	}

	public void setListaAtividade(List<Atividade> listaAtividade) {
		this.listaAtividade = listaAtividade;
	}

	public List<Atividade> getListaSubAtividade() {
		return listaSubAtividade;
	}

	public void setListaSubAtividade(List<Atividade> listaSubAtividade) {
		this.listaSubAtividade = listaSubAtividade;
	}

	public Atividade getSubAtividade() {
		return subAtividade;
	}

	public void setSubAtividade(Atividade subAtividade) {
		this.subAtividade = subAtividade;
	}

	private DiaSemanaEnum trataDataParaDiaDaSemana() {
		if (getInstance().getDataEspecifica() != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(getInstance().getDataEspecifica());
			return DiaSemanaEnum.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
		}
		return null;
	}

	private CurvaPadrao curvaCloneSemId(CurvaPadrao curvaOriginal) {
		CurvaPadrao clone = new CurvaPadrao();
		clone.setAtividade(curvaOriginal.getAtividade());
		clone.setDataEspecifica(curvaOriginal.getDataEspecifica());
		clone.setDia(curvaOriginal.getDia());
		clone.setHoraInicial(curvaOriginal.getHoraInicial());
		clone.setHoraFinal(curvaOriginal.getHoraFinal());
		clone.setIc_ativo(curvaOriginal.getIc_ativo());
		clone.setMeta(curvaOriginal.getMeta());
		clone.setPeriodo(curvaOriginal.getPeriodo());
		clone.setQuantidadeChamados(curvaOriginal.getQuantidadeChamados());
		clone.setTempoAtendimento(curvaOriginal.getTempoAtendimento());
		return clone;
	}

	@SuppressWarnings("unused")
	private CurvaPadrao curvaCloneComId(CurvaPadrao curvaOriginal) {
		CurvaPadrao clone = new CurvaPadrao();
		clone.setId(curvaOriginal.getId());
		clone.setAtividade(curvaOriginal.getAtividade());
		clone.setDataEspecifica(curvaOriginal.getDataEspecifica());
		clone.setDia(curvaOriginal.getDia());
		clone.setHoraInicial(curvaOriginal.getHoraInicial());
		clone.setHoraFinal(curvaOriginal.getHoraFinal());
		clone.setIc_ativo(curvaOriginal.getIc_ativo());
		clone.setMeta(curvaOriginal.getMeta());
		clone.setPeriodo(curvaOriginal.getPeriodo());
		clone.setQuantidadeChamados(curvaOriginal.getQuantidadeChamados());
		clone.setTempoAtendimento(curvaOriginal.getTempoAtendimento());
		return clone;
	}

	private Boolean isIntervaloHorasValido() {
		if (getInstance() != null && getInstance().getHoraInicial() != null && getInstance().getHoraFinal() != null) {
			Integer horaInicio = getInstance().getHoraInicial().getValor();
			Integer horaFinal = getInstance().getHoraFinal().getValor();

			if (horaFinal.equals(0)) {
				if (!horaInicio.equals(23)) {
					getFacesMessager().addMessageError(MensagemUtil.obterMensagem("equipe.message.horaInicioMaiorIgualHoraFinal"));
					return Boolean.FALSE;
				}
			} else {

				if (horaInicio >= horaFinal) {
					getFacesMessager().addMessageError(MensagemUtil.obterMensagem("equipe.message.horaInicioMaiorIgualHoraFinal"));
					return Boolean.FALSE;
				}

				if (Math.abs(horaFinal - horaInicio) > 1) {
					getFacesMessager().addMessageError(MensagemUtil.obterMensagem("equipe.message.intervaloHoraInicioFim"));
					return Boolean.FALSE;
				}
			}
		}
		return Boolean.TRUE;
	}

	@Override
	protected BaseService<CurvaPadrao> getService() {
		return curvaPadraoService;
	}

	@Override
	protected Long getEntityId(CurvaPadrao referenceValue) {
		return referenceValue.getId();
	}

	@Override
	protected CurvaPadrao newInstance() {
		return new CurvaPadrao();
	}

	public List<CurvaPadrao> getListaCurvaPadraoRemocao() {
		return listaCurvaPadraoRemocao;
	}

	public void setListaCurvaPadraoRemocao(List<CurvaPadrao> listaCurvaPadraoRemocao) {
		this.listaCurvaPadraoRemocao = listaCurvaPadraoRemocao;
	}

	public Boolean getExisteDuplicidade() {
		return existeDuplicidade;
	}

	public void setExisteDuplicidade(Boolean existeDuplicidade) {
		this.existeDuplicidade = existeDuplicidade;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}
}
