package br.gov.caixa.gitecsa.sicrv.security;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.context.RequestContext;

/**
 * Classe utilitaria para tratamento das mensagens.
 *
 * @author rmotal
 *
 */
public final class JsfUtil {

  /**
   * Construtor.
   */
  private JsfUtil() {
  }

  /**
   * Cria um array de selectItem.
   * @param entities List
   * @param selectOne boolean
   * @return SelectItem[]
   */
  public static SelectItem[] getSelectItems(final List<?> entities, final boolean selectOne) {
    int size = selectOne ? entities.size() + 1 : entities.size();

    SelectItem[] items = new SelectItem[size];

    int i = 0;

    if (selectOne) {
      items[0] = new SelectItem("", "---");
      i++;
    }

    for (Object x : entities) {
      items[i++] = new SelectItem(x, x.toString());
    }

    return items;
  }

  /**
   * Adiciona um Message do tipo Error.
   * 
   * @param ex
   *          Exception
   * @param defaultMsg
   *          message
   */
  public static void addErrorMessage(final Exception ex, final String defaultMsg) {
    String msg = ex.getLocalizedMessage();

    if (msg != null && msg.length() > 0) {
      addErrorMessage(msg);
    } else {
      addErrorMessage(defaultMsg);
    }
  }

  /**
   * Adiciona um Message do tipo Error.
   * 
   * @param msg
   *          message
   */
  public static void addErrorMessage(final String msg) {
    FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
    FacesContext.getCurrentInstance().addMessage(null, facesMsg);
  }

  /**
   * Adiciona mensagens de erro.
   * 
   * @param messages List de mensagens
   */
  public static void addErrorMessages(final List<String> messages) {
    for (String message : messages) {
      addErrorMessage(message);
    }
  }

  /**
   * Adiciona um Message do tipo Warn.
   * 
   * @param msg
   *          message
   */
  public static void addWarnMessage(final String msg) {
    FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg);
    FacesContext.getCurrentInstance().addMessage("Mensagem", facesMsg);
  }

  /**
   * Adiciona um Message do tipo Success.
   * 
   * @param msg
   *          message
   */
  public static void addSuccessMessage(final String msg) {
    FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
    FacesContext.getCurrentInstance().addMessage("Sucesso", facesMsg);
  }

  /**
   * Adiciona um Message do tipo Success.
   * 
   * @param msg
   *          message
   * @param fiedlId
   *          id do campo
   */
  public static void addSuccessMessage(final String msg, final String fiedlId) {
    FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
    FacesContext.getCurrentInstance().addMessage(fiedlId, facesMsg);
  }

  /**
   * Retorna o valor da key passada no RequestParameterMap.
   * 
   * @param key
   *          String
   * @return Valor
   */
  public static String getRequestParameter(final String key) {
    return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
  }

  /**
   * Retorna o objeto pelo request parameter informado.
   * 
   * @param requestParameterName
   *          String
   * @param converter
   *          Converter
   * @param component
   *          UIComponent
   * @return Object
   */
  public static Object getObjectFromRequestParameter(final String requestParameterName, final Converter converter,
      final UIComponent component) {
    String theId = JsfUtil.getRequestParameter(requestParameterName);
    return converter.getAsObject(FacesContext.getCurrentInstance(), component, theId);
  }

  /**
   * Adiciona uma mensagem de validação.
   * 
   * @param validacao
   *          Boolean
   * @param message
   *          String
   */
  public static void setListenerValidadao(final Boolean validacao, final String message) {
    RequestContext context = RequestContext.getCurrentInstance();

    if (!validacao) {
      JsfUtil.addErrorMessage(message);
    }

    context.addCallbackParam("validacao", validacao);
  }

  /**
   * Retorna o IP.
   * 
   * @return String
   */
  public static String getIP() {
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

    String ip = request.getLocalAddr();

    return ip;
  }

  /**
   * Retorna o FlashScope.
   * 
   * @return Flash
   */
  public static Flash flashScope() {
    return (FacesContext.getCurrentInstance().getExternalContext().getFlash());
  }

  /**
   * Retorna o valor da key passada no SessionMap.
   * 
   * @param key
   *          String
   * @return Object
   */
  public static Object getSessionMapValue(final String key) {
    if (FacesContext.getCurrentInstance() != null) {
      return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(key);
    } else {
      return null;
    }
  }

  /**
   * Adiciona um par chave/valor no SessionMap.
   * 
   * @param key
   *          String
   * @param value
   *          Object
   */
  public static void setSessionMapValue(final String key, final Object value) {
    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(key, value);
  }

  /**
   * Retorna o valor da key passada no ApplicationMap.
   * 
   * @param key
   *          String
   * @return Object
   */
  public static Object getApplicationMapValue(final String key) {
    return FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(key);
  }

  /**
   * Adiciona um par chave/valor no ApplicationMap.
   * 
   * @param key
   *          String
   * @param value
   *          Object
   */
  public static void setApplicationMapValue(final String key, final Object value) {
    FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put(key, value);
  }
}
