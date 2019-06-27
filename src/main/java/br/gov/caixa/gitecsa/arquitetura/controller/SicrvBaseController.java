package br.gov.caixa.gitecsa.arquitetura.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.web.FacesMessager;
import br.gov.caixa.gitecsa.sicrv.security.JsfUtil;

/**
 * Classe base para qualquer outro controller.
 */
public abstract class SicrvBaseController implements Serializable {

  private static final long serialVersionUID = 1L;

  @Inject
  private FacesMessager facesMessager;

  public static final Logger LOGGER = Logger.getLogger(SicrvBaseController.class);

  private static final String FACES_REDIRECT = "?faces-redirect=true";

  /**
   * Construtor default.
   */
  public SicrvBaseController() {
    super();
  }

  /**
   * Esconde o componente do widgetvar especificado.
   *
   * @param widgetvar
   *          String widgetvar
   */
  protected void hideDialog(final String widgetvar) {
    RequestContext.getCurrentInstance().execute(widgetvar + ".hide()");
  }

  /**
   * Mostra o componente do widgetvar especificado.
   *
   * @param widgetvar
   *          String widgetvar
   */
  protected void showDialog(final String widgetvar) {
    RequestContext.getCurrentInstance().execute(widgetvar + ".show()");
  }

  /**
   * Faz update nos componentes dos respectivos ids passados como parâmetro.
   *
   * @param idComponente
   *          Array com ids dos componentes
   */
  protected void updateComponentes(final String... idComponente) {
    for (String id : idComponente) {
      FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(id);
    }
  }

  protected String includeRedirect(final String url) {
    String formatedUrl = url.concat(FACES_REDIRECT);
    return formatedUrl;
  }

  protected String getRootErrorMessage(final Exception e) {
    String errorMessage = MensagemUtil.obterMensagem("general.crud.rootErrorMessage");
    if (e == null) {
      // This shouldn't happen, but return the default messages
      return errorMessage;
    }

    JsfUtil.addErrorMessage(errorMessage);

    // Start with the exception and recurse to find the root cause
    Throwable t = e;
    while (t != null) {
      // Get the message from the Throwable class instance
      errorMessage = t.getLocalizedMessage();
      t = t.getCause();
    }
    // This is the root cause message
    return errorMessage;
  }

  protected String createViolationResponse(final Set<ConstraintViolation<?>> violations) {
    Map<String, String> responseObj = new HashMap<String, String>();

    for (ConstraintViolation<?> violation : violations) {
      responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
    }

    return responseObj.toString();
  }

  protected void logErroMessage() {

  }

  protected void escreveRelatorio(final byte[] relatorio, final String nome, final boolean download) {
    try {
      this.escreveRelatorioResponse(relatorio, nome, "PDF", "application/pdf", download);
    } catch (Exception e) {
      getRootErrorMessage(e);
    }
  }

  protected void escreveRelatorio(final byte[] relatorio, final String nome, final String tipo, final String contentType,
      final boolean download) {
    try {
      this.escreveRelatorioResponse(relatorio, nome, tipo, contentType, download);
    } catch (Exception e) {
      getRootErrorMessage(e);
    }
  }

  protected void escreveRelatorio(final byte[] relatorio, final String nome, final String contentType, final boolean download)
      throws IOException {
    try {
      this.escreveRelatorioResponse(relatorio, nome, null, contentType, download);
    } catch (Exception e) {
      getRootErrorMessage(e);
    }
  }

  private void escreveRelatorioResponse(final byte[] relatorio, final String nome, final String tipo, final String contentType,
      final boolean download) throws IOException {
    try {
      String contentDisposition = download ? "attachment;filename=" : "inline;filename=";
      String nomeCompleto;

      if (tipo != null) {
        nomeCompleto = contentDisposition + "\"" + nome + "." + tipo.toLowerCase() + "\"";
      } else {
        nomeCompleto = contentDisposition + "\"" + nome + "\"";
      }

      JSFUtil.getServletResponse().setContentType(contentType);
      JSFUtil.getServletResponse().setHeader("Content-Disposition", nomeCompleto);
      JSFUtil.getServletResponse().setContentLength(relatorio.length);
      JSFUtil.getServletResponse().getOutputStream().write(relatorio);
    } catch (IOException e) {
      throw e;
    } finally {
      JSFUtil.getServletResponse().getOutputStream().flush();
      JSFUtil.getServletResponse().getOutputStream().close();
      JSFUtil.getContext().responseComplete();
    }
  }
  
  /**
   * Realiza a ordenação dos campos
   * @param msg1
   * @param msg2
   * @return
   */
  public static int sortTextIgnoreCase(final Object msg1, final Object msg2) {
	  return (((String) msg1).trim().toUpperCase()).compareToIgnoreCase((((String) msg2).trim().toUpperCase()));
  }
  
  public FacesMessager getFacesMessager() {
    return facesMessager;
  }

  public void setFacesMessager(final FacesMessager facesMessager) {
    this.facesMessager = facesMessager;
  }
}
