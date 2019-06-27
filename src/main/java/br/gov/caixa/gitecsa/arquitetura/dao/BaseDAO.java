package br.gov.caixa.gitecsa.arquitetura.dao;

import java.io.Serializable;
import java.util.List;

public interface BaseDAO<T> {
	public T save(T entity);

	public List<T> saveList(List<T> entity);

	public List<T> updateList(List<T> entity);

	public T update(T entity);

	public void delete(T entity);

	public List<T> findAll();

	public List<T> findAll(int first, int max);

	public T findById(Serializable id);

	public T find(Class<T> classe, Serializable id);

}
