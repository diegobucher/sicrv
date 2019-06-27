package br.gov.caixa.gitecsa.arquitetura.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.component.datatable.DataTable;

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.service.BaseService;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.Util;


/**
 * Classe que concentra o fluxo de uma tela de consulta que pode redirecionar para cadastro,edição.
 * Sendo possível excluir e filtrar
 *
 * @author wfjesus
 *
 * @param <T>
 */
public abstract class ConsultarBaseController<T> extends SicrvBaseController {

  private T instanceFilter;

  private List<T> lista;

  private static final long serialVersionUID = 4674809733896204945L;

  private static final String DATA_TABLE_CRUD = ":consultaForm:dataTableCrud";

  /**
   * Método responsável por criar a instancia que será utilzado pelo filtro da consulta.
   *
   * @return T
   */
  protected abstract T newInstance();

  /**
   * Método responsável por vincular o service da Entidade parametrizada.
   *
   * @return BaseService
   */
  protected abstract BaseService<T> getService();

  /**
   * Sufixo do nome tela que o controller está vinculada, Isso serve Para tornar possível voltar
   * para tela chamadora.
   *
   * @return Tela origem
   */
  protected abstract String getTelaOrigem();

  protected abstract String getRedirectManter();

  /**
   * Método que concentra toda inilização necessário ao funcionamento da tela.
   */
  protected abstract void continuarInicializacao();

  @Inject
  private ContextoController contextoController;

  private T instanceCRUD;

  @PostConstruct
  public void inicializar() {
    lista = new ArrayList<T>();
    instanceFilter = newInstance();
    if (!Util.isNullOuVazio(contextoController.getCrudMessage())) {
    	getFacesMessager().addMessageInfo(contextoController.getCrudMessage());
    	contextoController.setCrudMessage(null);
	}
    continuarInicializacao();
  }

  /**
   * Retorna a instância utilizada para filtrar a consulta.
   * 
   * @return T instanceFilter
   */
  public T getInstanceFilter() {
    if (instanceFilter == null) {
      instanceFilter = newInstance();
    }
    return instanceFilter;
  }

  public void setInstanceFilter(final T instanceFilter) {
    this.instanceFilter = instanceFilter;
  }

  /**
   * Método responsável por limpar os campos de filtro.
   */
  public void limparFiltro() {
    instanceFilter = newInstance();
    lista = new ArrayList<T>();
  }

  /**
   * Redireciona para tela de cadastro.
   *
   * @return String de navegação
   */
  public String novo() {
    contextoController.limpar();
    contextoController.setObjectFilter(instanceFilter);
    contextoController.setTelaOrigem(getTelaOrigem());
    return getRedirectManter();
  }

  /**
   * Método responsável por redirecionara para tela que edita o registro selecionado.
   *
   * @param t
   *          - Objeto que representa a linha selecionada
   * @return String de navegação
   */
  public String editar(final T t) {
    this.contextoController.limpar();
    contextoController.setObject(t);
    contextoController.setObjectFilter(instanceFilter);
    contextoController.setTelaOrigem(getTelaOrigem());
    FacesContext.getCurrentInstance().getExternalContext().getFlash().put(Util.OBJETO, t);
    FacesContext.getCurrentInstance().getExternalContext().getFlash().put(Util.ACAO, Util.EDITAR);
    return getRedirectManter();
  }

  /**
   * Método responsável por redirecionara para tela que detalha o registro selecionado.
   *
   * @param t - Objeto que representa a linha selecionada
   * @return String de navegação
   */
  public String detalhar(final T t) {
	  this.contextoController.limpar();
	  contextoController.setObject(t);
	  contextoController.setObjectFilter(instanceFilter);
	  contextoController.setTelaOrigem(getTelaOrigem());
	  FacesContext.getCurrentInstance().getExternalContext().getFlash().put(Util.OBJETO, t);
	  FacesContext.getCurrentInstance().getExternalContext().getFlash().put(Util.ACAO, Util.DETALHAR);
	  return getRedirectManter();
  }

  /**
   * Método responsável por consultar os registros de acordo aos filtros informados.
   *
   * @throws Exception Exception Gerada
   */
  public void consultar() throws Exception {
    lista = getService().consultar(instanceFilter);
    if (Util.isNullOuVazio(lista)) {
      getFacesMessager().addMessageError("general.crud.noItemFound");
    }
  }

  /**
   * Método responsável por excluir o registro selecionado.
   *
   * @throws BusinessException Exception Gerada
   * @throws Exception Exception Gerada
   */
  public void excluir() {
    try {
      getService().remove(instanceCRUD);
      lista.remove(instanceCRUD);
      getFacesMessager().addMessageInfo("geral.crud.itemRemovido");
    } catch (BusinessException be) {
      getFacesMessager().addMessageError(MensagemUtil.obterMensagem("mensagem.cadastro.ms011"));
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      e.printStackTrace();
    }
  }

  public void prepareToResult() throws Exception {

  }

  /**
   * limpa a ordenação e paginação da data table.
   */
  public void resetDatatable() {
    final DataTable table = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(getIdDataTableCrud());
    if (table != null) {
      table.reset();
      table.setSortBy(null);
    }
  }
  
  /**
   * Lista exibida na grid de resultados da consulta.
   *
   * @return lista
   */
  public List<T> getLista() {
    return lista;
  }

  public void setLista(final List<T> lista) {
    this.lista = lista;
  }

  public T getInstanceCRUD() {
    return instanceCRUD;
  }

  public void setInstanceCRUD(final T instanceCRUD) {
    this.instanceCRUD = instanceCRUD;
  }

  /**
   * Método responsável por retornar o id do data table que guarda o resultado da consulta.
   *
   * @return Id do datatable
   */
  protected String getIdDataTableCrud() {
    return DATA_TABLE_CRUD;
  }
  
}
