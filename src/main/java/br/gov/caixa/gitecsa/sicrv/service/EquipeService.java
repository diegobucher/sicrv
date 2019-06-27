package br.gov.caixa.gitecsa.sicrv.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.time.DateUtils;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.sicrv.dao.EquipeDAO;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeAtividade;
import br.gov.caixa.gitecsa.sicrv.model.EquipeEmpregado;

@Stateless
public class EquipeService extends AbstractService<Equipe> {

	private static final long serialVersionUID = -6044223883960124337L;

	@Inject
	private EquipeDAO equipeDAO;

	@Inject
	private EmpregadoService empregadoService;

	@Inject
	private EquipeEmpregadoService equipeEmpregadoService;

	@Inject
	private EquipeAtividadeService equipeAtividadeService;

	@Inject
	private EscalaService escalaService;

	@Override
	protected void validaCamposObrigatorios(Equipe entity) {

	}

	@Override
	protected void validaRegras(Equipe entity) {

	}

	@Override
	protected void validaRegrasExcluir(Equipe entity) {

	}

	@Override
	protected BaseDAO<Equipe> getDAO() {
		return equipeDAO;
	}

	@Override
	public void remove(Equipe entity) throws BusinessException, Exception {
		entity.setAtivo(Boolean.FALSE);
		getDAO().update(entity);

		equipeEmpregadoService.inativarEquipeEmpregadosPorEquipe(entity);

	}

	public List<Equipe> consultar(Equipe equipe) throws Exception {
		return equipeDAO.consultar(equipe);
	}

	public Equipe findByIdFetch(Long idEquipe) {
		return equipeDAO.findByIdFetch(idEquipe);
	}

	public void incluir(Equipe instance, List<Atividade> listAtividadeSelecionadas, List<EquipeEmpregado> listEquipeEmpregado) throws RequiredException, BusinessException,
			Exception {

		List<EquipeAtividade> equipeAtividadeList = new ArrayList<EquipeAtividade>();
		for (Atividade atividade : listAtividadeSelecionadas) {
			EquipeAtividade equipeAtividade = new EquipeAtividade();
			equipeAtividade.setAtividade(atividade);
			equipeAtividade.setEquipe(instance);
			equipeAtividadeList.add(equipeAtividade);
		}

		// instance.setEquipeEmpregados(new HashSet<EquipeEmpregado>(listEquipeEmpregado));
		// instance.setEquipeAtividades(new HashSet<EquipeAtividade>(equipeAtividadeList));

		// Validando as regras do incluir
		validarIncluir(instance, listAtividadeSelecionadas, listEquipeEmpregado);

		// Verificação se existe o empregado, para fazer alteração, senao inclusao do novo empregado
		for (EquipeEmpregado equipeEmpregado : listEquipeEmpregado) {
			if (equipeEmpregado.getEmpregado().getId() == null) {
				empregadoService.save(equipeEmpregado.getEmpregado());
			} else {
				empregadoService.update(equipeEmpregado.getEmpregado());
			}
		}
		instance.setAtivo(Boolean.TRUE);

		equipeDAO.save(instance);
		instance.setEquipeEmpregados(new HashSet<EquipeEmpregado>(listEquipeEmpregado));
		instance.setEquipeAtividades(new HashSet<EquipeAtividade>(equipeAtividadeList));

		for (EquipeAtividade equipeAtividade : equipeAtividadeList) {
			equipeAtividadeService.save(equipeAtividade);
		}
		for (EquipeEmpregado equipeEmpregado : instance.getEquipeEmpregados()) {
			equipeEmpregadoService.save(equipeEmpregado);
		}

	}

	private void validarIncluir(Equipe entity, List<Atividade> listAtividadeSelecionadas, List<EquipeEmpregado> listEquipeEmpregado) throws BusinessException {
		validar(entity, listAtividadeSelecionadas, listEquipeEmpregado);
		Equipe equipeConsulta = new Equipe();
		equipeConsulta.setNome(entity.getNome());
		equipeConsulta.setUnidade(entity.getUnidade());
		List<Equipe> listaResultado = equipeDAO.consultar(equipeConsulta);
		if (listaResultado != null && !listaResultado.isEmpty()) {
			throw new BusinessException("equipe.message.nomeEquipeJaCadastrado");
		}

	}

	private void validarAlterar(Equipe entity, List<Atividade> listAtividadeSelecionadas, List<EquipeEmpregado> listEquipeEmpregado) throws BusinessException {
		validar(entity, listAtividadeSelecionadas, listEquipeEmpregado);

		Equipe equipeConsulta = new Equipe();
		equipeConsulta.setNome(entity.getNome());
		equipeConsulta.setUnidade(entity.getUnidade());

		List<Equipe> listaResultado = equipeDAO.consultar(equipeConsulta);
		if (listaResultado != null && !listaResultado.isEmpty()) {
			for (Equipe equipe : listaResultado) {
				if (!entity.getId().equals(equipe.getId())) {
					throw new BusinessException("equipe.message.nomeEquipeJaCadastrado");
				}
			}
		}

	}

	private void validar(Equipe entity, List<Atividade> listAtividadeSelecionadas, List<EquipeEmpregado> listEquipeEmpregado) throws BusinessException {

		if (listAtividadeSelecionadas == null || listAtividadeSelecionadas.isEmpty()) {
			throw new BusinessException("equipe.message.atividadeNaoSelecionada");
		}
		if (listEquipeEmpregado == null || listEquipeEmpregado.isEmpty()) {
			throw new BusinessException("equipe.message.empregadoNaoAdicionado");
		}
		if (Util.isNullOuVazio(entity.getNome())) {
			throw new BusinessException("equipe.message.nomeObrigatorio");
		}

	}

	public void alterar(Equipe instance, List<Atividade> listAtividadeSelecionadas, List<EquipeEmpregado> listEquipeEmpregado, Boolean flagGerarEscalaNovosEmpregados)
			throws RequiredException, BusinessException, Exception {

		validarAlterar(instance, listAtividadeSelecionadas, listEquipeEmpregado);

		// PARTE DE ATIVIDADES
		List<EquipeAtividade> atividadesNovasList = new ArrayList<EquipeAtividade>();
		List<EquipeAtividade> atividadesRemovidasList = new ArrayList<EquipeAtividade>();

		// Procura por Atividades Adicionadas
		for (Atividade atividadeSelecionada : listAtividadeSelecionadas) {
			boolean atividadeJaExistente = false;
			for (EquipeAtividade equipeAtividadeCadastradas : instance.getEquipeAtividades()) {
				if (equipeAtividadeCadastradas.getAtividade().equals(atividadeSelecionada)) {
					atividadeJaExistente = true;
					break;
				}
			}
			// Se ela não existe no banco então é nova
			if (!atividadeJaExistente) {
				EquipeAtividade equipeAtividade = new EquipeAtividade();
				equipeAtividade.setAtividade(atividadeSelecionada);
				equipeAtividade.setEquipe(instance);
				atividadesNovasList.add(equipeAtividade);
			}
		}

		// Procura por Atividades Removidas
		for (EquipeAtividade equipeAtividadeCadastrada : instance.getEquipeAtividades()) {

			boolean atividadeDaBaseExisteNaTela = false;

			for (Atividade atividadeSelecionada : listAtividadeSelecionadas) {
				if (equipeAtividadeCadastrada.getAtividade().equals(atividadeSelecionada)) {
					atividadeDaBaseExisteNaTela = true;
				}
			}
			if (!atividadeDaBaseExisteNaTela) {
				atividadesRemovidasList.add(equipeAtividadeCadastrada);
			}
		}

		for (EquipeAtividade equipeAtividade : atividadesNovasList) {
			equipeAtividadeService.save(equipeAtividade);
		}
		for (EquipeAtividade equipeAtividade : atividadesRemovidasList) {
			equipeAtividadeService.remove(equipeAtividade);
		}

		Set<EquipeAtividade> equipeAtividadeSet = instance.getEquipeAtividades();

		equipeAtividadeSet.removeAll(atividadesRemovidasList);
		equipeAtividadeSet.addAll(atividadesNovasList);

		// instance.setEquipeEmpregados(new HashSet<EquipeEmpregado>(listEquipeEmpregado));
		// instance.setEquipeAtividades(equipeAtividadeSet);

		// TODO AJUSTAR PARTE DE EMPREGADOS
		// PARTE DE EMPREGADOS
		List<EquipeEmpregado> empregadosNovosList = new ArrayList<EquipeEmpregado>();
		List<EquipeEmpregado> empregadosAtualizarList = new ArrayList<EquipeEmpregado>();
		List<EquipeEmpregado> empregadosRemovidosList = new ArrayList<EquipeEmpregado>();

		// Procura por Empregados Adicionados
		for (EquipeEmpregado equipeEmpregadoSelecionado : listEquipeEmpregado) {

			boolean empregadoJaExistente = false;
			for (EquipeEmpregado equipeEmpregadoCadastrado : instance.getEquipeEmpregados()) {
				if (equipeEmpregadoCadastrado.getEmpregado().equals(equipeEmpregadoSelecionado.getEmpregado())) {
					empregadoJaExistente = true;
					break;
				}
			}
			// Se ela não existe no banco então é nova
			if (!empregadoJaExistente) {
				empregadosNovosList.add(equipeEmpregadoSelecionado);
			} else {
				empregadosAtualizarList.add(equipeEmpregadoSelecionado);
			}
		}

		for (EquipeEmpregado equipeEmpregadoCadastrado : instance.getEquipeEmpregados()) {
			boolean empregadoDaBaseExisteNaTela = false;

			for (EquipeEmpregado equipeEmpregadoSelecionado : listEquipeEmpregado) {
				if (equipeEmpregadoCadastrado.equals(equipeEmpregadoSelecionado)) {
					empregadoDaBaseExisteNaTela = true;
				}
			}

			if (!empregadoDaBaseExisteNaTela) {
				empregadosRemovidosList.add(equipeEmpregadoCadastrado);
			}
		}

		// Salva/Atualiza o empregado e EquipeEmpregado
		List<Empregado> empregadosGerarEscalaList = new ArrayList<Empregado>();
		for (EquipeEmpregado equipeEmpregado : empregadosNovosList) {
			if (equipeEmpregado.getEmpregado().getId() == null) {
				empregadoService.save(equipeEmpregado.getEmpregado());
			} else {
				empregadoService.update(equipeEmpregado.getEmpregado());
			}
			equipeEmpregadoService.save(equipeEmpregado);

			// Ajuste De problema, Gambiarra; Não excluir
			// O objeto Empregado não está sendo atualizado na geração de escala individual.
			// Fica com mesmo endereço de memória e não faz FETCH na lista de equipes empregado, ficando null e dando null pointer
			equipeEmpregado.getEmpregado().setEquipesEmpregado(new ArrayList<EquipeEmpregado>());
			equipeEmpregado.getEmpregado().addEquipeEmpregado(equipeEmpregado);

			if (flagGerarEscalaNovosEmpregados && escalaService.existeEscalasFuturasPorEquipe(instance.getId()) && !equipeEmpregado.getSupervisor()
					&& !escalaService.existeEscalasFuturasPorEmpregado(equipeEmpregado.getEmpregado().getId())) {
				empregadosGerarEscalaList.add(equipeEmpregado.getEmpregado());
			}
		}

		// Caso seja selecionado a flag então gera escalas para os novos empregados
		if (flagGerarEscalaNovosEmpregados) {
			if (empregadosGerarEscalaList != null && !empregadosGerarEscalaList.isEmpty()) {
				Object[] dataInicioFim = escalaService.obterDatasPeriodoEscalasFuturasPorEquipe(instance.getId());
				Date dataIni = DateUtils.truncate((Date) dataInicioFim[0], Calendar.DAY_OF_MONTH);
				Date dataFim = DateUtils.truncate((Date) dataInicioFim[1], Calendar.DAY_OF_MONTH);

				dataIni = getProximaSegundaFeiraDaSemana(dataIni);

				if (dataIni.before(dataFim)) {
					escalaService.gerarEscalaRevezamentoEmpregadoList(instance, empregadosGerarEscalaList, dataIni, dataFim);
				}

			}
		}

		// Caso seja uma alteração de equipe empregado então as atualiza
		for (EquipeEmpregado equipeEmpregado : empregadosAtualizarList) {
			equipeEmpregadoService.update(equipeEmpregado);
			if (equipeEmpregado.isAdicionado() && escalaService.existeEscalasFuturasPorEmpregado(equipeEmpregado.getEmpregado().getId())) {
				// TODO ATUALIZAR ESCALAS FUTURAS

				escalaService.atualizarEscalasEmpregadoApartirAmanha(equipeEmpregado);
			}
		}

		// Faz a remoção os empregados na equipe
		Calendar calAmanha = Calendar.getInstance();
		calAmanha = DateUtils.truncate(calAmanha, Calendar.DAY_OF_MONTH);
		calAmanha.add(Calendar.DAY_OF_MONTH, 1);
		for (EquipeEmpregado equipeEmpregado : empregadosRemovidosList) {
			if (escalaService.existeEscalasFuturasPorEmpregado(equipeEmpregado.getEmpregado().getId())) {
				escalaService.deletarEscalaFolgaExistenteEmpregado(equipeEmpregado.getEmpregado(), calAmanha.getTime());
			}

			equipeEmpregado.setAtivo(Boolean.FALSE);
			equipeEmpregado.setDataDesligamento(new Date());
			equipeEmpregadoService.update(equipeEmpregado);
		}

		// Verificação se existe o empregado, para fazer alteração, senao inclusao do novo empregado
		for (EquipeEmpregado equipeEmpregado : listEquipeEmpregado) {
			equipeEmpregado.getEmpregado().setNome(equipeEmpregado.getEmpregado().getNome().toLowerCase());
			if (equipeEmpregado.getEmpregado().getId() == null) {
				empregadoService.save(equipeEmpregado.getEmpregado());
			} else {
				empregadoService.update(equipeEmpregado.getEmpregado());
			}
		}

		equipeDAO.update(instance);

	}

	private Date getProximaSegundaFeiraDaSemana(Date dia) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dia);

		while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			cal.add(Calendar.DAY_OF_WEEK, 1);
		}

		return cal.getTime();
	}

	public List<Equipe> consultarEquipePorEmpregado(Empregado empregado) {
		return equipeDAO.consultarEquipePorEmpregado(empregado);
	}

	public List<Equipe> obterEquipesAtivas() {
		return equipeDAO.obterEquipesAtivas();
	}

	public List<Equipe> obterEquipesAtivasPorUnidade(Integer cgc) {
		return equipeDAO.obterEquipesAtivasPorUnidade(cgc);
	}

}
