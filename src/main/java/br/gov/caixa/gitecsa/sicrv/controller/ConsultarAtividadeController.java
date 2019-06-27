package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.controller.ContextoController;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.ldap.util.Util;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.security.ControleAcesso;
import br.gov.caixa.gitecsa.sicrv.service.AtividadeService;

@Named
@ViewScoped
public class ConsultarAtividadeController extends BaseController implements Serializable {

  private static final long serialVersionUID = 8172181314853937861L;

  @Inject
  private ContextoController contextoController;

  @Inject
  private AtividadeService atividadeService;
  
  @Inject
  private ControleAcesso controleAcesso; 

  private Long idAtividade;

  private String nomeAtividade;

  private boolean pesquisado;


  @PostConstruct
  public void init() {
    limpar();

    if (!Util.isNullOuVazio(contextoController.getCrudMessage())) {
      facesMessager.addMessageInfo(contextoController.getCrudMessage());
    }

    if (redirectTelaConsulta().equals(contextoController.getTelaOrigem())) {
      if (contextoController.getObjectFilter() != null) {
        pesquisado = true;
      }
      if (!Util.isNullOuVazio(contextoController.getObjectFilter())) {
        nomeAtividade = (String) contextoController.getObjectFilter();
      }

    }

    contextoController.limpar();
  }

  public void consultar() {
  }

  private void limpar() {
  }

  public String redirectTelaManter() {
    contextoController.limpar();

    if (pesquisado && nomeAtividade == null) {
      nomeAtividade = "";
    }
    contextoController.setAcao("");

    contextoController.setTelaOrigem(redirectTelaConsulta());
    contextoController.setObjectFilter(nomeAtividade);
    return "/pages/atividade/manter.xhtml?faces-redirect=true";
  }

  public String redirectTelaConsulta() {
    return "/pages/atividade/consulta.xhtml?faces-redirect=true";
  }

  public String editar() {
	contextoController.limpar();
	contextoController.setObject(idAtividade);
    contextoController.setAcao(br.gov.caixa.gitecsa.arquitetura.util.Util.EDITAR);
    contextoController.setTelaOrigem(redirectTelaConsulta());
    contextoController.setObjectFilter(nomeAtividade);
    if (pesquisado && nomeAtividade == null) {
      nomeAtividade = "";
    }

    return "/pages/atividade/manter.xhtml?faces-redirect=true";
  }

  public String detalhar() {
	contextoController.limpar();
    
    if (pesquisado && nomeAtividade == null) {
      nomeAtividade = "";
    }

    contextoController.setObject(idAtividade);
    contextoController.setAcao(br.gov.caixa.gitecsa.arquitetura.util.Util.DETALHAR);
    contextoController.setTelaOrigem(redirectTelaConsulta());
    contextoController.setObjectFilter(nomeAtividade);

    return "/pages/atividade/manter.xhtml?faces-redirect=true";
  }

  public String excluir() {
    Atividade atividade = new Atividade();
    atividade.setId(idAtividade);
    atividadeService.delete(atividade);

    facesMessager.addMessageInfo(MensagemUtil.obterMensagem("geral.crud.excluido", "atividade.label.atividade"));

    return "";
  }

  public Long getIdAtividade() {
    return idAtividade;
  }

  public void setIdAtividade(Long idAtividade) {
    this.idAtividade = idAtividade;
  }

  public void prepareToResult() throws Exception {
  }

  public String getNomeAtividade() {
    return nomeAtividade;
  }

  public void setNomeAtividade(String nomeAtividade) {
    this.nomeAtividade = nomeAtividade;
  }

  public boolean isPesquisado() {
    return pesquisado;
  }

  public void setPesquisado(boolean pesquisado) {
    this.pesquisado = pesquisado;
  }
  
  public Long getIdUnidadeUsuario(){
    Long idUnidade = null;
    
    if(controleAcesso.getUnidade() != null){
      idUnidade = controleAcesso.getUnidade().getId();
    }
    return idUnidade;
  }
  
  public void setIdUnidadeUsuario(Long idUnidade) {
  }


}
