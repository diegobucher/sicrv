package br.gov.caixa.gitecsa.sicrv.dao.impl;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.sicrv.dao.UnidadeDAO;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;

public class UnidadeDAOImpl extends BaseDAOImpl<Unidade> implements UnidadeDAO {

	@Override
	public Unidade buscarPorCodigo(Integer codigoUnidade) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM Unidade unidade ");
		hql.append(" WHERE unidade.cgc = :codigoUnidade ");

		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("codigoUnidade", codigoUnidade);
		query.setMaxResults(1);

		try {
			return (Unidade) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
