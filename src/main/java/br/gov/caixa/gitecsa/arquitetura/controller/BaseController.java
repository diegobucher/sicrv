package br.gov.caixa.gitecsa.arquitetura.controller;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import br.gov.caixa.gitecsa.arquitetura.web.FacesMessager;

public abstract class BaseController implements Serializable {

  private static final long serialVersionUID = -3630069325535795544L;
  
  @Inject
  protected FacesMessager facesMessager;
  

  public BaseController() {
    super();
  }
  
  public String ajaxHandler() {
    return null;
  }

  /**
   * Esconde o componente do id especificado
   * 
   * @param widgetvar
   */
  protected void hideDialog(String idComponente) {
    RequestContext.getCurrentInstance().execute(idComponente + ".hide()");
  }

  /**
   * Mostra o componente do id especificado
   * 
   * @param widgetvar
   */
  protected void showDialog(String idComponente) {
    RequestContext.getCurrentInstance().execute(idComponente + ".show()");
  }

  /**
   * Faz update nos componentes dos respectivos ids passados como parâmetro
   * 
   * @param idComponente
   */
  protected void updateComponentes(String... idComponente) {
    for (String id : idComponente) {
      FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(id);
    }
  }
}
