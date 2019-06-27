package br.gov.caixa.gitecsa.sicrv.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.sicrv.dao.CurvaPadraoDAO;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.CurvaPadrao;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;

@Stateless
public class CurvaPadraoService extends AbstractService<CurvaPadrao> {

	private static final long serialVersionUID = 8753127762706109022L;

	@Inject
	private CurvaPadraoDAO curvaPadraoDAO;

	public List<CurvaPadrao> findAll() {
		return curvaPadraoDAO.findAll();
	}

	public CurvaPadrao findById(final Long id) {
		return curvaPadraoDAO.findById(id);
	}

	public void delete(final CurvaPadrao curvaPadrao) {
		curvaPadrao.setIc_ativo(Boolean.FALSE);
		curvaPadraoDAO.update(curvaPadrao);
	}

	public CurvaPadrao salvar(final CurvaPadrao curvaPadrao) {
		if (curvaPadrao.getId() == null) {
			return curvaPadraoDAO.save(curvaPadrao);
		} else {
			return curvaPadraoDAO.update(curvaPadrao);
		}
	}

	public List<CurvaPadrao> buscarPorEquipe(Equipe equipe) {
		return this.curvaPadraoDAO.buscarPorEquipe(equipe);
	}

	@Override
	public List<CurvaPadrao> consultar(CurvaPadrao entidade) throws Exception {
		return null;
	}

	@Override
	protected void validaCamposObrigatorios(CurvaPadrao entity) {

	}

	@Override
	protected void validaRegras(CurvaPadrao entity) {

	}

	@Override
	protected void validaRegrasExcluir(CurvaPadrao entity) {

	}

	@Override
	protected BaseDAO<CurvaPadrao> getDAO() {
		return curvaPadraoDAO;
	}

	public CurvaPadrao findByIdFetchAll(Long idcurvaPadrao) {
		return curvaPadraoDAO.findByIdFetchAll(idcurvaPadrao);
	}

	public List<CurvaPadrao> buscarPorAtividadeSubAtividade(Atividade atividade) {
		return curvaPadraoDAO.buscarPorAtividadeSubAtividade(atividade);
	}

	public List<CurvaPadrao> listaGarantirUnicidade(CurvaPadrao curva) {
		return curvaPadraoDAO.listaGarantirUnicidade(curva);
	}

	public void removerLista(List<CurvaPadrao> curvaPadraoList) {
		for (CurvaPadrao curvaPadrao : curvaPadraoList) {
			curvaPadraoDAO.delete(curvaPadrao);
		}
	}

}