package br.gov.caixa.gitecsa.arquitetura.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.event.ActionEvent;

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.arquitetura.service.BaseService;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;

/**
 * Classe base para CRUD de classes que utilizam o ViewScoped com apenas uma tela.
 *
 * @author cagalvaom
 *
 * @param <T>
 *          Parametro tipado
 */
public abstract class BaseViewCrud<T> extends SicrvBaseController implements Serializable {

  private static final long serialVersionUID = 1L;

  private T instance;

  private List<T> lista;

  private Long id;

  public BaseViewCrud() {
  }

  /**
   * Retorna a service responsável pelo controle da página.
   *
   * @return BaseService
   */
  protected abstract BaseService<T> getService();

  /**
   * Retorna o id da entity.
   *
   * @return Long
   */
  protected abstract Long getEntityId(T referenceValue);

  /**
   * Criacao da nova instancia.
   *
   * @return T
   */
  protected abstract T newInstance();

  /**
   * Retorna a entity pelo id.
   *
   * @return T
   */
  protected T load(final Long id) throws Exception {
    return getService().findById(id);
  }

  /**
   * Lista todas os objetos da classe.
   *
   * @return - List com todos os objetos
   * @throws Exception
   *           - Exception gerada
   */
  protected List<T> getAll() throws Exception {
    return getService().findAll();
  }

  /**
   * Finaliza a edicao de um registro Normalmente esse metodo deve ser invocado chamando um metodo
   * de atualizacao na entidade(update).
   *
   */
  protected void updateImpl(final T referenceValue) throws RequiredException, BusinessException, Exception {
    getService().update(referenceValue);
    getFacesMessager().info(MensagemUtil.obterMensagem("general.crud.atualizado"));
    limparForm();
  }

  /**
   * Finaliza a criacao de um registro Normalmente esse metodo deve ser invocado chamando um metodo
   * de criacao na entidade(insert).
   */
  protected void saveImpl(final T referenceValue) throws RequiredException, BusinessException, Exception {
    getService().save(referenceValue);
    getFacesMessager().info(MensagemUtil.obterMensagem("general.crud.salvo"));
    limparForm();
  }

  /**
   * Finaliza a remocao de um registro Normalmente esse metodo deve ser invocado chamando um metodo
   * de remocao na entidade(delete).
   */
  protected void deleteImpl(final T referenceValue) throws BusinessException, Exception {
    getService().remove(referenceValue);
    getFacesMessager().info(MensagemUtil.obterMensagem("general.crud.excluido"));
  }

  /**
   * Indica se a instancia e nova, ou uma ja existente.
   *
   * @return boolean
   */
  public boolean isManaged() {
    return getEntityId(instance) != null && getEntityId(instance) != 0;
  }

  /**
   * Carrega o instance com base no id.
   * 
   * @return T
   */
  public T loadInstance() {
    try {
      return load(getId());
    } catch (Exception e) {
      getRootErrorMessage(e);
    }

    return null;
  }

  /**
   * Lista todos os objetos da classe.
   * 
   * @return List
   */
  public List<T> allInstance() {
    try {
      return getAll();
    } catch (Exception e) {
      getRootErrorMessage(e);
    }

    return null;
  }

  /**
   * Método utilizado para limpar o formulario na tela de consulta.
   */
  public void limparForm() {
    id = null;
    instance = newInstance();
  }

  /**
   * Persiste ou atualiza uma instancia na base de dados.
   */
  public void save() {
    try {
      if (isManaged()) {
        updateImpl(getInstance());
      } else {
        saveImpl(getInstance());
      }
    } catch (RequiredException re) {
      getFacesMessager().addMessageError(re);
    } catch (BusinessException be) {
      getFacesMessager().addMessageError(be);
    } catch (Exception e) {
      getFacesMessager().addMessageError(getRootErrorMessage(e));
    }
  }

  /**
   * Remove uma entidade.
   */
  public void delete(final ActionEvent event) {
    try {
      deleteImpl(instance);
    } catch (BusinessException be) {
      getFacesMessager().addMessageError(be);
    } catch (Exception e) {
      getRootErrorMessage(e);
    }
  }

  /**
   * Retorna o instance existente ou cria um novo.
   * 
   * @return T Instance
   */
  public T getInstance() {
    if (instance == null) {
      if (getId() != null) {
        instance = loadInstance();
      } else {
        instance = newInstance();
      }
    }
    return instance;
  }

  public void setInstance(final T instance) {
    this.instance = instance;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * Método responsável por informar se a instance é nula ou não.
   *
   * @return boolean se instance é nulo
   */
  public boolean instanceIsNull() {
    return this.instance == null;
  }

  public List<T> getLista() {
	  return lista;
  }
	
  public void setLista(List<T> lista) {
	  this.lista = lista;
  }
}
