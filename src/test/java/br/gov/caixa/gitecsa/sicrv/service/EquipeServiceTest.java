package br.gov.caixa.gitecsa.sicrv.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.sicrv.dao.EquipeDAO;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeAtividade;
import br.gov.caixa.gitecsa.sicrv.model.EquipeEmpregado;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;

public class EquipeServiceTest {

	@InjectMocks
	private EquipeService equipeService;

	@Mock
	private EquipeDAO equipeDAO;

	@Mock
	private EquipeEmpregadoService equipeEmpregadoService;

	@Mock
	private EmpregadoService empregadoService;

	@Mock
	private EquipeAtividadeService equipeAtividadeService;

	@Mock
	private EscalaService escalaService;

	private Equipe equipe;
	private Unidade unidade;
	private Atividade atividade;
	private EquipeEmpregado equipeEmpregado;
	private Empregado empregado;
	private EquipeAtividade equipeAtividade;
	private List<Atividade> atividades;
	private List<EquipeEmpregado> equipeEmpregados;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Set<EquipeAtividade> equipeAtividades1 = new HashSet<EquipeAtividade>();
		Set<EquipeEmpregado> equipeEmpregados1 = new HashSet<EquipeEmpregado>();

		equipe = new Equipe();
		equipe.setId(1L);
		equipe.setNome("Equipe nome 01");

		unidade = new Unidade();
		unidade.setId(1L);
		unidade.setNome("Unidade teste 01");
		equipe.setUnidade(unidade);

		equipeAtividade = new EquipeAtividade();
		equipeAtividade.setId(1L);
		atividade = new Atividade();
		atividade.setId(1L);
		equipeAtividade.setAtividade(atividade);
		equipeAtividades1.add(equipeAtividade);
		equipe.setEquipeAtividades(equipeAtividades1);

		equipeEmpregado = new EquipeEmpregado();
		equipeEmpregado.setId(1L);
		empregado = new Empregado();
		empregado.setId(1L);
		equipeEmpregado.setEmpregado(empregado);
		equipeEmpregados1.add(equipeEmpregado);
		equipe.setEquipeEmpregados(equipeEmpregados1);

		atividades = new ArrayList<Atividade>();
		atividade = new Atividade();
		atividade.setId(1L);
		atividade.setNome("Atividade teste 01");
		atividades.add(atividade);

		atividade = new Atividade();
		atividade.setId(2L);
		atividade.setNome("Atividade teste 02");
		atividades.add(atividade);

		equipeEmpregados = new ArrayList<EquipeEmpregado>();
		equipeEmpregado = new EquipeEmpregado();
		equipeEmpregado.setId(3L);
		empregado = new Empregado();
		empregado.setId(1L);
		empregado.setNome("Empregado teste 01");
		equipeEmpregado.setEmpregado(empregado);
		equipeEmpregado.setSupervisor(false);
		equipeEmpregado.setAdicionado(true);
		equipeEmpregados.add(equipeEmpregado);

		equipeEmpregado = new EquipeEmpregado();
		equipeEmpregado.setId(2L);
		empregado = new Empregado();
		empregado.setId(2L);
		empregado.setNome("Empregado teste 02");
		equipeEmpregado.setEmpregado(empregado);
		equipeEmpregado.setSupervisor(false);
		equipeEmpregado.setAdicionado(true);
		equipeEmpregados.add(equipeEmpregado);
	}

	@Test
	public void testRemove() throws BusinessException, Exception {
		when(equipeDAO.update(equipe)).thenReturn(equipe);
		doNothing().when(equipeEmpregadoService).inativarEquipeEmpregadosPorEquipe(equipe);
		equipeService.remove(equipe);
		assertTrue(!equipe.getAtivo());
	}

	@Test
	public void testeIncluir() throws RequiredException, BusinessException, Exception {
		when(equipeDAO.consultar(new Equipe())).thenReturn(null);
		when(empregadoService.save(empregado)).thenReturn(null);
		when(empregadoService.update(empregado)).thenReturn(null);
		when(equipeDAO.save(equipe)).thenReturn(equipe);
		when(equipeAtividadeService.save(new EquipeAtividade())).thenReturn(null);

		equipeService.incluir(equipe, atividades, equipeEmpregados);

		assertTrue(equipe.getAtivo() && !equipe.getEquipeEmpregados().isEmpty() && !equipe.getEquipeAtividades().isEmpty());
	}

	@Test
	public void testAlterar() throws RequiredException, BusinessException, Exception {
		GregorianCalendar gc = new GregorianCalendar();
		gc.add(Calendar.MONTH, -1);

		when(equipeDAO.consultar(new Equipe())).thenReturn(null);
		when(equipeAtividadeService.save(new EquipeAtividade())).thenReturn(equipeAtividade);
		doNothing().when(equipeAtividadeService).remove(new EquipeAtividade());
		when(empregadoService.save(empregado)).thenReturn(null);
		when(empregadoService.update(empregado)).thenReturn(null);
		when(equipeEmpregadoService.save(equipeEmpregado)).thenReturn(null);
		when(escalaService.existeEscalasFuturasPorEquipe(equipe.getId())).thenReturn(true);
		when(escalaService.existeEscalasFuturasPorEmpregado(equipeEmpregado.getEmpregado().getId())).thenReturn(false);
		when(escalaService.obterDatasPeriodoEscalasFuturasPorEquipe(equipe.getId())).thenReturn(new Object[] { gc.getTime(), new Date() });
		doNothing().when(escalaService).gerarEscalaRevezamentoEmpregadoList(equipe, new ArrayList<Empregado>(), gc.getTime(), new Date());
		doNothing().when(escalaService).atualizarEscalasEmpregadoApartirAmanha(equipeEmpregado);
		doNothing().when(escalaService).deletarEscalaFolgaExistenteEmpregado(empregado, gc.getTime());
		when(equipeEmpregadoService.update(equipeEmpregado)).thenReturn(null);

		equipeService.alterar(equipe, atividades, equipeEmpregados, true);

		for (EquipeEmpregado equipeEmpregado : equipe.getEquipeEmpregados()) {
			assertTrue(!equipeEmpregado.getAtivo() && equipeEmpregado.getDataDesligamento() != null);
		}
		for (EquipeEmpregado equipeEmpregado : equipeEmpregados) {
			assertTrue(equipeEmpregado.getEmpregado().getNome().equals(equipeEmpregado.getEmpregado().getNome().toLowerCase()));
		}
	}

}
