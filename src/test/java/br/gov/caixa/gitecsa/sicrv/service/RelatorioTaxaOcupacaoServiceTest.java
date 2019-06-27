package br.gov.caixa.gitecsa.sicrv.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.sicrv.dao.AtividadeDAO;
import br.gov.caixa.gitecsa.sicrv.dao.CurvaPadraoDAO;
import br.gov.caixa.gitecsa.sicrv.dao.EquipeDAO;
import br.gov.caixa.gitecsa.sicrv.enumerator.HoraFixaEnum;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.CurvaPadrao;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeAtividade;
import br.gov.caixa.gitecsa.sicrv.model.Escala;
import br.gov.caixa.gitecsa.sicrv.model.dto.RelatorioTaxaOcupacaoDTO;
import br.gov.caixa.gitecsa.sicrv.model.dto.TaxaOcupacaoSemanaDTO;

public class RelatorioTaxaOcupacaoServiceTest {

	@InjectMocks
	private RelatorioTaxaOcupacaoService relatorioTaxaOcupacaoService;

	@Mock
	private EscalaService escalaService;

	@Mock
	private EquipeDAO equipeDAO;

	@Mock
	private CurvaPadraoDAO curvaPadraoDAO;

	@Mock
	private AtividadeDAO atividadeDAO;

	private Equipe equipe;

	private Set<EquipeAtividade> equipeAtividadeSet;

	private Atividade atividade;

	private List<Atividade> atividadeList;

	private List<Escala> escalaList;

	private List<CurvaPadrao> curvaPadraoList;

	private CurvaPadrao curvaPadrao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		atividadeList = new ArrayList<>();

		equipe = new Equipe();
		equipeAtividadeSet = new HashSet<>();
		equipe.setEquipeAtividades(equipeAtividadeSet);

		atividade = new Atividade();
		atividade.setAtivo(true);
		atividade.setAtividadeList(atividadeList);

		EquipeAtividade equipeAtividade = new EquipeAtividade();
		equipeAtividade.setAtividade(atividade);
		equipeAtividadeSet.add(equipeAtividade);

		atividadeList.add(atividade);
		atividade.setAtividadeList(atividadeList);
		escalaList = new ArrayList<>();
		Escala escala = new Escala();
		escala.setAtivo(true);
		escala.setId(1l);
		escala.setInicio(new Date());
		escala.setFim(new Date());
		escalaList.add(escala);
		curvaPadrao = new CurvaPadrao();
		curvaPadrao.setHoraInicial(HoraFixaEnum.DOIS);
		curvaPadrao.setHoraFinal(HoraFixaEnum.DOZE);
		curvaPadrao.setAtividade(atividade);
		curvaPadraoList = new ArrayList<>();
		curvaPadraoList.add(curvaPadrao);

	}

	@Test
	public void testGetRelatorioTaxaOcupacao() {

		Long idEquipe = 1l;
		Date dataInicial = new Date();
		java.util.Date dataFinal = new Date();

		Mockito.when(equipeDAO.findByIdFetch(idEquipe)).thenReturn(equipe);
		Mockito.when(escalaService.obterEscalasPorEquipeEPeriodo(idEquipe, dataInicial, dataFinal)).thenReturn(escalaList);
		Mockito.when(curvaPadraoDAO.buscarPorEquipe(equipe)).thenReturn(curvaPadraoList);
		// Mockito.when(curvaPadraoDAO.buscarPorEquipe(equipe)).thenReturn(curvaPadraoList);

		RelatorioTaxaOcupacaoDTO dto = relatorioTaxaOcupacaoService.getRelatorioTaxaOcupacao(idEquipe, dataInicial, dataFinal);

		assertTrue(dto != null);
		// fail("Not yet implemented");
	}

	@Test
	public void testBuscarTaxaOcupacaoSemanal() {

		Long idEquipe = 1l;
		Date dataInicial = new Date();
		java.util.Date dataFinal = new Date();
		Long idAtividade = 1L;

		Mockito.when(equipeDAO.findByIdFetch(idEquipe)).thenReturn(equipe);
		Mockito.when(atividadeDAO.findById(idAtividade)).thenReturn(atividade);
		Mockito.when(curvaPadraoDAO.buscarPorAtividadeSubAtividade(atividade)).thenReturn(curvaPadraoList);

		List<TaxaOcupacaoSemanaDTO> lista = relatorioTaxaOcupacaoService.buscarTaxaOcupacaoSemanal(dataInicial, dataFinal, idEquipe, idAtividade);

		assertTrue(!lista.isEmpty());
	}

	@Test
	public void testGerarTaxaOcupacaoSemanaDTO() {

		Long idEquipe = 1l;
		Date dataInicial = new Date();
		java.util.Date dataFinal = new Date();

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);

		List<TaxaOcupacaoSemanaDTO> lista = relatorioTaxaOcupacaoService.gerarTaxaOcupacaoSemanaDTO(dataInicial, cal.getTime());

		assertTrue(!lista.isEmpty());

	}

	@Test
	public void testFindEquipeAtividadeByEquipe() {

		Long idEquipe = 1l;

		Mockito.when(atividadeDAO.findEquipeAtividadeByEquipe(equipe)).thenReturn(new ArrayList(equipeAtividadeSet));

		atividadeDAO.findEquipeAtividadeByEquipe(equipe);

		List<Atividade> lista = relatorioTaxaOcupacaoService.findEquipeAtividadeByEquipe(equipe);

		assertTrue(!lista.isEmpty());
	}

}
