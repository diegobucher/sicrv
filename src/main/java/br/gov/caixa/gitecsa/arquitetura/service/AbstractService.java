package br.gov.caixa.gitecsa.arquitetura.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.arquitetura.util.Util;


/**
 * Classe utilizada por todo serviço EJB que faz operação de CRUD e/ou precisa implementar regras de campos obrigatórios
 * e regras negócio. Contém métodos comuns que devem ser implementados, assim como métodos utilitários.
 *
 * @author cagalvaom
 *
 * @param <T>
 */
@Stateless
public abstract class AbstractService<T> implements BaseService<T> {

  /**
   * SerialUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Lista das mensagens.
   */
  private transient List<String> mensagens;

  /**
   * Valida os campos obrigatórios.
   * 
   * @param entity
   *          entidade
   */
  protected abstract void validaCamposObrigatorios(T entity);

  /**
   * Valida as regras.
   * 
   * @param entity
   *          entidade
   */
  protected abstract void validaRegras(T entity);

  /**
   * Valida as regras a excluir.
   * 
   * @param entity
   *          entidade
   */
  protected abstract void validaRegrasExcluir(T entity);

  /**
   * Retorna o DAO.
   * 
   * @return DAO
   */
  protected abstract BaseDAO<T> getDAO();

  /**
   * Implementação default do do método para fazer update em lote.
   *
   * @param listEntity
   *          Lista de entidades
   * @return Retorna uma lista das entidades salvas.
   * @throws RequiredException
   *           Exception Gerada
   * @throws BusinessException
   *           Exception Gerada
   * @throws Exception
   *           Exception Gerada
   */
  protected List<T> updateLote(Collection<T> listEntity) throws RequiredException, BusinessException, Exception {
    List<T> listReturn = new ArrayList<T>();
    for (T entity : listEntity) {
      listReturn.add(updateImpl(entity));
    }
    return listReturn;
  }

  /**
   * Flag editando.
   */
  private boolean editando;

  /**
   * Retorna todos do banco.
   *
   * @return Lista das entidades
   */
  public List<T> findAll() {
    return getDAO().findAll();
  }

  /**
   * Retorna a entidade por ID.
   *
   * @param id
   *          Long id
   * @return Entidade encontrada
   */
  public T findById(Long id) {
    return getDAO().findById(id);
  }

  /**
   * Chamado pelo controller para salvar alguma entidade.
   *
   * @param entity
   *          Entidade a ser salva
   * @return Retorna a entidade salva.
   * @throws RequiredException
   *           Exception Gerada
   * @throws BusinessException
   *           Exception Gerada
   * @throws Exception
   *           Exception Gerada
   */
  public T save(T entity) throws RequiredException, BusinessException, Exception {
    processValidations(entity, false);
    return saveImpl(entity);
  }

  /**
   * Método que salva uma entidade. Se necessário, ele será sobrescrito para realizar algo antes e/ou depois da operação
   * de save.
   *
   * @param entity
   *          Entidade a ser salva
   * @return Entidade salva
   * @throws RequiredException
   *           Exception Gerada
   * @throws BusinessException
   *           Exception Gerada
   * @throws Exception
   *           Exception Gerada
   */
  protected T saveImpl(T entity) throws RequiredException, BusinessException, Exception {
    getDAO().save(entity);
    return entity;
  }

  /**
   * Chamado pelo controller para atualizar alguma entidade.
   *
   * @param entity
   *          Entidade a atualizada
   * @return Retorna a entidade atualizada.
   * @throws RequiredException
   *           Exception Gerada
   * @throws BusinessException
   *           Exception Gerada
   * @throws Exception
   *           Exception Gerada
   */
  public T update(T entity) throws RequiredException, BusinessException, Exception {
    processValidations(entity, true);
    return updateImpl(entity);
  }

  /**
   * Método que atualiza uma entidade. Se necessário, ele será sobrescrito para realizar algo antes e/ou depois da
   * operação de update.
   *
   * @param entity
   *          Entidade a ser atualizada
   * @return Entidade atualizada
   * @throws RequiredException
   *           Exception Gerada
   * @throws BusinessException
   *           Exception Gerada
   * @throws Exception
   *           Exception Gerada
   */
  protected T updateImpl(T entity) throws RequiredException, BusinessException, Exception {
    getDAO().update(entity);

    return entity;
  }

  /**
   * Chamado pelo controller para atualizar uma lista de alguma entidade.
   *
   * @param listEntity
   *          Lista de entidades
   * @return Retorna a lista das entidades atualizadas.
   * @throws RequiredException
   *           Exception Gerada
   * @throws BusinessException
   *           Exception Gerada
   * @throws Exception
   *           Exception Gerada
   */
  public List<T> update(Collection<T> listEntity) throws RequiredException, BusinessException, Exception {
    processValidations(listEntity, true);
    return updateLote(listEntity);
  }

  /**
   * Chamado pelo controller para remover alguma entidade.
   *
   * @param entity
   *          Entidade a ser removida
   * @throws BusinessException
   *           Exception Gerada
   * @throws Exception
   *           Exception Gerada
   */
  public void remove(T entity) throws BusinessException, Exception {
    processDeleteValidations(entity);
    removeImpl(entity);
  }

  /**
   * Método que remove uma entidade. Se necessário, ele será sobrescrito para realizar algo antes e/ou depois da
   * operação de delete.
   *
   * @param entity
   *          Entidade a ser removida
   * @throws BusinessException
   *           Exception Gerada
   * @throws Exception
   *           Exception Gerada
   */
  protected void removeImpl(T entity) throws BusinessException, Exception {
    getDAO().delete(entity);

  }

  /**
   * Processa todas as validações implementadas no validaCamposObrigatorios e no validaRegras durante o save e o update.
   *
   * @param entity
   *          Entidade a ser validada.
   * @param editando
   *          Parametro que será usado nos métodos das RNs.
   * @throws RequiredException
   *           Exception Gerada Quando algum campo obrigatório não foi preenchido.
   * @throws BusinessException
   *           Exception Gerada Quando alguma RN não foi atendida.
   */
  protected void processValidations(T entity, boolean editando) throws RequiredException, BusinessException {
    setEditando(editando);

    mensagens = new ArrayList<String>();

    validaCamposObrigatorios(entity);

    if (!Util.isNullOuVazio(mensagens)) {
      throw new RequiredException(mensagens);
    }

    validaRegras(entity);

    if (!Util.isNullOuVazio(mensagens)) {
      throw new BusinessException(mensagens);
    }
  }

  /**
   * Processa todas as validações implementadas no validaCamposObrigatorios e no validaRegras durante o save e o update.
   *
   * @param listEntity
   *          lista da Entidade a ser validada.
   * @param editando
   *          Parametro que será usado nos métodos das RNs.
   * @throws RequiredException
   *           Exception Gerada Quando algum campo obrigatório não foi preenchido.
   * @throws BusinessException
   *           Exception Gerada Quando alguma RN não foi atendida.
   */
  protected void processValidations(Collection<T> listEntity, boolean editando) throws RequiredException,
      BusinessException {
    setEditando(editando);

    mensagens = new ArrayList<String>();

    for (T entity : listEntity) {
      validaCamposObrigatorios(entity);
    }

    if (!Util.isNullOuVazio(mensagens)) {
      throw new RequiredException(mensagens);
    }

    for (T entity : listEntity) {
      validaRegras(entity);
    }

    if (!Util.isNullOuVazio(mensagens)) {
      throw new BusinessException(mensagens);
    }
  }

  /**
   * Processa as RNs implementadas no validaRegrasExcluir durante o remove.
   *
   * @param entity
   *          Entidade a ser validada.
   * @throws BusinessException
   *           Exception Gerada Se alguma regra não foi atendida.
   */
  protected final void processDeleteValidations(T entity) throws BusinessException {
    mensagens = new ArrayList<String>();

    validaRegrasExcluir(entity);

    if (!Util.isNullOuVazio(mensagens)) {
      throw new BusinessException(mensagens);
    }
  }

  /**
   * Retorna o editando.
   * 
   * @return entidando
   */
  protected boolean isEditando() {
    return editando;
  }

  /**
   * Define o editando.
   * 
   * @param editando
   */
  protected void setEditando(boolean editando) {
    this.editando = editando;
  }

  /**
   * Retorna a lista de mensagens.
   * 
   * @return mensagens
   */
  public List<String> getMensagens() {
    return mensagens;
  }

  /**
   * Define as mensagens.
   * 
   * @param mensagens
   *          Lista de mensagens.
   */
  public void setMensagens(List<String> mensagens) {
    this.mensagens = mensagens;
  }
}
