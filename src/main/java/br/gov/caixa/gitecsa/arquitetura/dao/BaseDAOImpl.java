package br.gov.caixa.gitecsa.arquitetura.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class BaseDAOImpl<T> extends AbstractBaseDAOImpl implements BaseDAO<T> {

  private Class<T> persistentClass;

  @SuppressWarnings("unchecked")
  public BaseDAOImpl() {
    this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }

  public T save(final T entity) {
    getEntityManager().persist(entity);

    return entity;
  }

  public List<T> saveList(final List<T> entitys) {

    final int batchSize = 100;

    for (int i = 0; i < entitys.size(); i++) {

      T entity = entitys.get(i);
      getEntityManager().persist(entity);

      if (i % batchSize == 0) {
        getEntityManager().flush();
        getEntityManager().clear();
      }
    }

    getEntityManager().flush();
    getEntityManager().clear();

    return entitys;
  }

  public List<T> updateList(final List<T> entitys) {

    final int batchSize = 100;

    for (int i = 0; i < entitys.size(); i++) {

      T entity = entitys.get(i);
      getEntityManager().merge(entity);

      if (i % batchSize == 0) {
        getEntityManager().flush();
        getEntityManager().clear();
      }
    }

    getEntityManager().flush();
    getEntityManager().clear();

    return entitys;
  }

  public T update(final T entity) {
    return getEntityManager().merge(entity);
  }

  public void delete(final T entity) {
    final T attachedEntity = getEntityManager().merge(entity);

    getEntityManager().remove(attachedEntity);
  }

  @SuppressWarnings("unchecked")
  public List<T> findAll() {
    return getEntityManager().createQuery("select a from " + persistentClass.getName() + " a").getResultList();
  }

  @SuppressWarnings("unchecked")
  public List<T> findAll(final int first, final int max) {
    final org.hibernate.Query query = getSession().createQuery("select a from " + persistentClass.getName() + " a");

    query.setFirstResult(first);

    query.setFetchSize(max);

    query.setMaxResults(max);

    return query.list();
  }

  @SuppressWarnings("unchecked")
  public T findById(final Serializable id) {
    return (T) getSession().get(persistentClass, id);
  }

  public T find(final Class<T> classe, final Serializable id) {
    return getEntityManager().find(classe, id);
  }
}
