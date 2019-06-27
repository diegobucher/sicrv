package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.omnifaces.cdi.ViewScoped;

import br.gov.caixa.gitecsa.arquitetura.controller.ConsultarBaseController;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.arquitetura.service.BaseService;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.sicrv.model.EstacaoTrabalho;
import br.gov.caixa.gitecsa.sicrv.service.EstacaoTrabalhoService;

@Named
@ViewScoped
public class ConsultarEstacaoTrabalhoController extends ConsultarBaseController<EstacaoTrabalho> implements Serializable {

	private static final long serialVersionUID = 8172181314853937861L;

	// @Inject
	// private ControleAcesso controleAcesso;

	@Inject
	private EstacaoTrabalhoService estacaoTrabalhoService;

	private EstacaoTrabalho estacaoTrabalhoDetalhe;

	private String filtro;

	@Override
	protected String getRedirectManter() {
		return "";
	}

	@Override
	protected EstacaoTrabalho newInstance() {
		return new EstacaoTrabalho();
	}

	@Override
	protected BaseService<EstacaoTrabalho> getService() {
		return estacaoTrabalhoService;
	}

	@Override
	protected String getTelaOrigem() {
		return "";
	}

	public void adicionarEstacao() {

		try {

			estacaoTrabalhoService.save(getInstanceCRUD());
			continuarInicializacao();
			getFacesMessager().addMessageInfo(MensagemUtil.obterMensagem("geral.crud.salva", "estacaoTrabalho.label.estacaoTrabalho"));

		} catch (RequiredException e) {
			e.printStackTrace();
		} catch (BusinessException e) {
			getFacesMessager().addMessageError(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void detalharEstacao(EstacaoTrabalho estacaoTrabalho) {
		this.estacaoTrabalhoDetalhe = estacaoTrabalhoService.findById(estacaoTrabalho.getId());
	}

	public void ativarEstacao() {

		estacaoTrabalhoDetalhe.setAtiva(true);
		estacaoTrabalhoDetalhe.setMotivo(null);
		try {
			estacaoTrabalhoService.update(getEstacaoTrabalhoDetalhe());
			continuarInicializacao();
			hideDialog("dlgAtivar");

			getFacesMessager().addMessageInfo(MensagemUtil.obterMensagem("geral.crud.ativada", MensagemUtil.obterMensagem("estacaoTrabalho.label.estacaoTrabalho")));

			updateComponentes("formConsulta");
		} catch (RequiredException e) {
			e.printStackTrace();
		} catch (BusinessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void desativarEstacao() {

		if (StringUtils.isNotBlank(getEstacaoTrabalhoDetalhe().getMotivo())) {
			estacaoTrabalhoDetalhe.setAtiva(false);
			try {
				estacaoTrabalhoService.update(getEstacaoTrabalhoDetalhe());
				continuarInicializacao();
				hideDialog("dlgInativar");

				getFacesMessager().addMessageInfo(MensagemUtil.obterMensagem("geral.crud.desativada", MensagemUtil.obterMensagem("estacaoTrabalho.label.estacaoTrabalho")));

				updateComponentes("formConsulta");
			} catch (RequiredException e) {
				e.printStackTrace();
			} catch (BusinessException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

			getFacesMessager().addMessageError(MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("estacaoTrabalho.label.motivo")));
		}
	}

	public void removerEstacao() {
		try {
			estacaoTrabalhoService.remove(getEstacaoTrabalhoDetalhe());
			continuarInicializacao();

			getFacesMessager().addMessageInfo(MensagemUtil.obterMensagem("geral.crud.excluido", MensagemUtil.obterMensagem("estacaoTrabalho.label.estacaoTrabalho")));

		} catch (RequiredException e) {
			e.printStackTrace();
		} catch (BusinessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void continuarInicializacao() {
		try {
			setInstanceCRUD(new EstacaoTrabalho());
			setEstacaoTrabalhoDetalhe(null);
			setLista(new ArrayList<EstacaoTrabalho>());
			getLista().addAll(getService().consultar(super.getInstanceCRUD()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getFiltro() {
		return filtro;
	}

	public void setFiltro(String filtro) {
		this.filtro = filtro;
	}

	public EstacaoTrabalho getEstacaoTrabalhoDetalhe() {
		return estacaoTrabalhoDetalhe;
	}

	public void setEstacaoTrabalhoDetalhe(EstacaoTrabalho estacaoTrabalhoDetalhe) {
		this.estacaoTrabalhoDetalhe = estacaoTrabalhoDetalhe;
	}
}
