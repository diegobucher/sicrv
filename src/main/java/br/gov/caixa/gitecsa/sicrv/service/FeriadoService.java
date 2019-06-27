package br.gov.caixa.gitecsa.sicrv.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.sicrv.dao.FeriadoDAO;
import br.gov.caixa.gitecsa.sicrv.model.Feriado;

@Stateless
public class FeriadoService extends AbstractService<Feriado> {
	private static final long serialVersionUID = -8465875684943300026L;
	
	@Inject
	private FeriadoDAO feriadoDAO;

	@Override
	public List<Feriado> consultar(Feriado entidade) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void validaCamposObrigatorios(Feriado entity) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validaRegras(Feriado entity) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validaRegrasExcluir(Feriado entity) {
		// TODO Auto-generated method stub

	}

	public Feriado buscarFeriadoNaData(Date data) {
		return feriadoDAO.buscarFeriadoNaData(data);
	}

	@Override
	protected BaseDAO<Feriado> getDAO() {
		// TODO Auto-generated method stub
		return feriadoDAO;
	}
}
