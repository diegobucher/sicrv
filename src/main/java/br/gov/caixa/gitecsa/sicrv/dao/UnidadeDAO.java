package br.gov.caixa.gitecsa.sicrv.dao;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;

public interface UnidadeDAO extends BaseDAO<Unidade> {
	
	/**
	 * Método responsável por buscar a unidade através do seu codigo
	 * @param codigoUnidade
	 * @return
	 */
	public Unidade buscarPorCodigo(Integer codigoUnidade);

}
