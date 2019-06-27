package br.gov.caixa.gitecsa.sicrv.dao;

import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.CurvaPadrao;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;

public interface CurvaPadraoDAO extends BaseDAO<CurvaPadrao> {

	public List<CurvaPadrao> buscarPorAtividadeSubAtividade(Atividade atividade);
	
	public CurvaPadrao findByIdFetchAll(Long idcurvaPadrao);

	/**
	 * Busca as CurvaPadrao que se chocam com a curva atual.
	 */
	public List<CurvaPadrao> listaGarantirUnicidade(CurvaPadrao curva);

	public List<CurvaPadrao> buscarPorEquipe(Equipe equipe);

}
