package br.gov.caixa.gitecsa.sicrv.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.sicrv.dao.AtividadeDAO;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.EquipeAtividade;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;

@Stateless
public class AtividadeService {

	@Inject
	private AtividadeDAO atividadeDao;

	public List<Atividade> findAll() {
		return atividadeDao.findAll();
	}

	public Atividade findById(final Long id) {
		return atividadeDao.findById(id);

	}

	public void delete(final Atividade atividade) {

		Atividade atividadeCadastrado = atividadeDao.findByIdFetchAll(atividade.getId());

		atividadeCadastrado.setAtivo(false);
		atividadeDao.update(atividadeCadastrado);

		for (Atividade subAtividade : atividadeCadastrado.getAtividadeList()) {
			subAtividade.setAtivo(false);
			atividadeDao.update(subAtividade);
		}

	}

	public Atividade salvar(final Atividade atividade) {

		if (atividade.getId() == null) {
			return atividadeDao.save(atividade);
		} else {
			return atividadeDao.update(atividade);
		}
	}

	public List<Atividade> findAtividadesPai(Unidade unidade) {
		return atividadeDao.findAtividadesPai(unidade);
	}

	public List<Atividade> findAtividadesFilhas(Long idPai) {
		return atividadeDao.findAtividadesFilhas(idPai);
	}

	public List<Atividade> consultar(Atividade atividade) {
		return atividadeDao.consultar(atividade);
	}

	public Atividade findByIdFetchAll(Long id) {
		return atividadeDao.findByIdFetchAll(id);
	}

	public void validarSalvarAtividade(Atividade atividade) throws BusinessException {

		Atividade atividadeCadastrada = atividadeDao.findAtividadeByNomeUnidade(atividade.getNome(), atividade.getUnidade());

		if (atividadeCadastrada != null) {
			if (!atividadeCadastrada.getId().equals(atividade.getId())) {
				throw new BusinessException("atividade.cadastro.validacao.atividade.existente");
			}
		}

		// É uma atividade Filha verifica nos irmãos
		if (atividade.getAtividadePai() != null) {

			List<Atividade> subAtividadeList = atividadeDao.findAtividadesFilhas(atividade.getAtividadePai().getId());

			for (Atividade subAtividade : subAtividadeList) {
				if (subAtividade.getPrioridade().equals(atividade.getPrioridade()) && !subAtividade.getId().equals(atividade.getId())) {
					throw new BusinessException(MensagemUtil.obterMensagem("atividade.cadastro.validacao.prioridade.existente"));
				}
			}

		}
	}

	public List<EquipeAtividade> buscarEquipeAtividade(Atividade atividade) {
		return atividadeDao.buscarEquipeAtividade(atividade);
	}

	public List<Atividade> findAtividadesPaiSemCurvaPadrao(Unidade unidade) {
		return atividadeDao.findAtividadesPaiSemCurvaPadrao(unidade);
	}

}
