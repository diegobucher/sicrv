package br.gov.caixa.gitecsa.sicrv.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.sicrv.dao.UnidadeDAO;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;

@Stateless
public class UnidadeService extends AbstractService<Unidade>{

	private static final long serialVersionUID = -7633588998855663311L;
	
	@Inject
	private UnidadeDAO unidadeDAO;
	
	@Override
	public List<Unidade> consultar(Unidade entidade) throws Exception {
		return null;
	}

	@Override
	protected void validaCamposObrigatorios(Unidade entity) {
	}

	@Override
	protected void validaRegras(Unidade entity) {
	}

	@Override
	protected void validaRegrasExcluir(Unidade entity) {
	}

	@Override
	protected BaseDAO<Unidade> getDAO() {
		return unidadeDAO;
	}
	
	public Unidade buscarPorCodigo(Integer codigoUnidade) {
		return unidadeDAO.buscarPorCodigo(codigoUnidade);
	}

}
