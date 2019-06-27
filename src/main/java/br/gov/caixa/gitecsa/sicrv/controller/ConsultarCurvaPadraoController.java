package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.gov.caixa.gitecsa.arquitetura.controller.ConsultarBaseController;
import br.gov.caixa.gitecsa.arquitetura.controller.ContextoController;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.service.BaseService;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.CurvaPadrao;
import br.gov.caixa.gitecsa.sicrv.security.ControleAcesso;
import br.gov.caixa.gitecsa.sicrv.service.AtividadeService;
import br.gov.caixa.gitecsa.sicrv.service.CurvaPadraoService;

@Named
@ViewScoped
public class ConsultarCurvaPadraoController extends ConsultarBaseController<CurvaPadrao> implements Serializable {

	private static final long serialVersionUID = -3722562919848646698L;

	private static final String TELA_ORIGEM = "CONSULTA_CURVA_PADRAO";

	@Inject
	private ContextoController contextoController;	
	@Inject
	private ControleAcesso controleAcesso; 	  
	@Inject
	private AtividadeService atividadeService;
	@Inject
	private CurvaPadraoService curvaPadraoService;

	private Atividade atividade;
	private Atividade subAtividade;
	
	private Long idCurvaPadrao;   
	private Boolean pesquisaAtiva;
	
	private List<Atividade> listaAtividade;
	private List<Atividade> listaSubAtividade;
	
	@Override
	protected void continuarInicializacao() {
		if (TELA_ORIGEM.equals(contextoController.getTelaOrigem())){
			if (!Util.isNullOuVazio(contextoController.getCrudMessage())) {
				getFacesMessager().addMessageInfo(contextoController.getCrudMessage());				
			}
			if (!Util.isNullOuVazio(contextoController.getObjectFilter())) {
				FiltroDTO filtroRetorno = (FiltroDTO) contextoController.getObjectFilter();
				this.listaAtividade = atividadeService.findAtividadesPai(controleAcesso.getUnidade());
				this.atividade = filtroRetorno.getAtividadeDTO(); 
				obterListaSubAtividade(); 
				this.subAtividade = filtroRetorno.getSubAtividadeDTO(); 
				this.pesquisaAtiva = filtroRetorno.getPesquisaAtivaDTO();
			}
			if (this.atividade != null && this.pesquisaAtiva ){
				consultar();
			}
		} else {
			this.listaSubAtividade = new ArrayList<Atividade>();
			this.listaAtividade = atividadeService.findAtividadesPai(controleAcesso.getUnidade());
			this.pesquisaAtiva = Boolean.FALSE;
		}
		contextoController.limpar();
	}
	
	public String novo() {
		FiltroDTO filtroDTO = preencherFiltroDTO();
		filtroDTO.setModoEdicao(Boolean.FALSE);
		this.contextoController.limpar();
	    contextoController.setObjectFilter(filtroDTO);
		contextoController.setTelaOrigem(getTelaOrigem());
		return getRedirectManter();
	}
	
	@Override
	protected CurvaPadrao newInstance() {
		return new CurvaPadrao();
	}

	@Override
	protected BaseService<CurvaPadrao> getService() {
		return curvaPadraoService;
	}

	@Override
	protected String getTelaOrigem() {
		return TELA_ORIGEM;
	}
	
	public String editar(CurvaPadrao curvaPadrao) {
		FiltroDTO filtroDTO = preencherFiltroDTO();
		this.contextoController.limpar();
		this.contextoController.setObject(curvaPadrao);
		filtroDTO.setModoEdicao(Boolean.TRUE);
		this.contextoController.setObjectFilter(filtroDTO);
		this.contextoController.setTelaOrigem(getTelaOrigem());
	    FacesContext.getCurrentInstance().getExternalContext().getFlash().put("curvaPadrao", curvaPadrao);
	    FacesContext.getCurrentInstance().getExternalContext().getFlash().put(Util.ACAO, Util.EDITAR);
	    return getRedirectManter();
	}
	
	public String detalhar(CurvaPadrao curvaPadrao) {
		FiltroDTO filtroDTO = preencherFiltroDTO();
		this.contextoController.limpar();
		this.contextoController.setObject(curvaPadrao);
		filtroDTO.setModoEdicao(Boolean.FALSE);
		this.contextoController.setObjectFilter(filtroDTO);
		this.contextoController.setTelaOrigem(getTelaOrigem());
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("curvaPadrao", curvaPadrao);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put(Util.ACAO, Util.EDITAR);
		return getRedirectManter();
	}


	private FiltroDTO preencherFiltroDTO() {
		FiltroDTO filtroDTO = new FiltroDTO();
		filtroDTO.setAtividadeDTO(this.atividade);
		filtroDTO.setSubAtividadeDTO(this.subAtividade);
		filtroDTO.setPesquisaAtivaDTO(this.pesquisaAtiva);
		return filtroDTO;
	}
	
	
	public void consultar() {
		if (validarConsulta()){
			if (this.subAtividade != null){
				setLista(curvaPadraoService.buscarPorAtividadeSubAtividade(this.subAtividade));			
			} else {
				obterListaSubAtividade();
				if (this.listaSubAtividade != null && !this.listaSubAtividade.isEmpty()){
					List<CurvaPadrao> listaTodos = new ArrayList<CurvaPadrao>();
					for (Atividade item : this.listaSubAtividade) {
						listaTodos.addAll(curvaPadraoService.buscarPorAtividadeSubAtividade(item));
					}
					setLista(listaTodos);
				} else {
					setLista(curvaPadraoService.buscarPorAtividadeSubAtividade(this.atividade));			
				}
			}	
			this.pesquisaAtiva = Boolean.TRUE;
		} 
	}
	
	public Boolean validarConsulta() {
		if (Util.isNullOuVazio(this.atividade)) {
			getFacesMessager().addMessageError(MensagemUtil.obterMensagem("geral.message.required",
					MensagemUtil.obterMensagem("curvaPadrao.label.atividade")));
			return false;
		}
	    return true;
	}
	
	public void obterListaSubAtividade(){		
		this.listaSubAtividade = new ArrayList<Atividade>();
		if (this.atividade != null && this.atividade.getId() != null){
			this.listaSubAtividade = atividadeService.findAtividadesFilhas(this.atividade.getId());	
			if (this.listaSubAtividade == null || this.listaSubAtividade.isEmpty() ){
				this.subAtividade = null;
			}
		}
	}

	@Override
	protected String getRedirectManter() {
		return "/pages/curvaPadrao/manter.xhtml?faces-redirect=true";
	}
	
  /**
   * Método responsável por excluir o registro selecionado.
   *
   * @throws BusinessException Exception Gerada
   * @throws Exception Exception Gerada
   */
	@Override
  public void excluir() {
    try {
      getService().remove(getInstanceCRUD());
      getLista().remove(getInstanceCRUD());
      getFacesMessager().addMessageInfo("curvaPadrao.mensagem.curvaPadraoExcluidoSucesso");
    } catch (BusinessException be) {
      getFacesMessager().addMessageError(MensagemUtil.obterMensagem("mensagem.cadastro.ms011"));
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      e.printStackTrace();
    }
  }

	/** Getters and Setters*/
	public Atividade getAtividade() {
		return atividade;
	}

	public void setAtividade(Atividade atividade) {
		this.atividade = atividade;
	}

	public Atividade getSubAtividade() {
		return subAtividade;
	}

	public void setSubAtividade(Atividade subAtividade) {
		this.subAtividade = subAtividade;
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

	public Long getIdCurvaPadrao() {
		return idCurvaPadrao;
	}

	public void setIdCurvaPadrao(Long idCurvaPadrao) {
		this.idCurvaPadrao = idCurvaPadrao;
	}
	
	public Boolean getPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(Boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}
}

class FiltroDTO {
	
	private Atividade atividadeDTO;
	private Atividade subAtividadeDTO;
	private Boolean pesquisaAtivaDTO;
	private Boolean modoEdicao;
	
	public Atividade getAtividadeDTO() {
		return atividadeDTO;
	}
	public void setAtividadeDTO(Atividade atividadeDTO) {
		this.atividadeDTO = atividadeDTO;
	}
	public Atividade getSubAtividadeDTO() {
		return subAtividadeDTO;
	}
	public void setSubAtividadeDTO(Atividade subAtividadeDTO) {
		this.subAtividadeDTO = subAtividadeDTO;
	}
	public Boolean getPesquisaAtivaDTO() {
		return pesquisaAtivaDTO;
	}
	public void setPesquisaAtivaDTO(Boolean pesquisaAtivaDTO) {
		this.pesquisaAtivaDTO = pesquisaAtivaDTO;
	}
	public Boolean getModoEdicao() {
		return modoEdicao;
	}
	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}
}
