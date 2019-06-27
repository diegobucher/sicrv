package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.gov.caixa.gitecsa.arquitetura.controller.ConsultarBaseController;
import br.gov.caixa.gitecsa.arquitetura.service.BaseService;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.security.ControleAcesso;
import br.gov.caixa.gitecsa.sicrv.service.EquipeService;

@Named
@ViewScoped
public class ConsultarEquipeController extends ConsultarBaseController<Equipe> implements Serializable {

	private static final long serialVersionUID = 8172181314853937861L;
	
	@Inject
	private ControleAcesso controleAcesso;
	
	@Inject
	private EquipeService equipeService;
	  
	@Override
	protected String getRedirectManter() {
		return "/pages/equipe/manter.xhtml?faces-redirect=true";
	}

	@Override
	protected Equipe newInstance() {
		return new Equipe();
	}
	
	@Override
	protected BaseService<Equipe> getService() {
		return equipeService;
	}
	
	@Override
	protected String getTelaOrigem() {
		return "consultarEquipe";
	}
	
	@Override
	protected void continuarInicializacao() {
		try {
			Equipe equipe = new Equipe();
			equipe.setUnidade(controleAcesso.getUnidade());
			getLista().addAll(getService().consultar(equipe));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
