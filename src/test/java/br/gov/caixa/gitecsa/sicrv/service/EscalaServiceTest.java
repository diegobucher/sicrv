package br.gov.caixa.gitecsa.sicrv.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.arquitetura.util.DateUtil;
import br.gov.caixa.gitecsa.sicrv.dao.AusenciaEmpregadoDAO;
import br.gov.caixa.gitecsa.sicrv.dao.CurvaPadraoDAO;
import br.gov.caixa.gitecsa.sicrv.dao.EmpregadoDAO;
import br.gov.caixa.gitecsa.sicrv.dao.EquipeDAO;
import br.gov.caixa.gitecsa.sicrv.dao.EscalaDAO;
import br.gov.caixa.gitecsa.sicrv.dao.EstacaoTrabalhoDAO;
import br.gov.caixa.gitecsa.sicrv.dao.FeriadoDAO;
import br.gov.caixa.gitecsa.sicrv.dao.FolgaDAO;
import br.gov.caixa.gitecsa.sicrv.enumerator.SituacaoFolgaEnum;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.Ausencia;
import br.gov.caixa.gitecsa.sicrv.model.CurvaPadrao;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeAtividade;
import br.gov.caixa.gitecsa.sicrv.model.EquipeEmpregado;
import br.gov.caixa.gitecsa.sicrv.model.Escala;
import br.gov.caixa.gitecsa.sicrv.model.EstacaoTrabalho;
import br.gov.caixa.gitecsa.sicrv.model.Feriado;
import br.gov.caixa.gitecsa.sicrv.model.Folga;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.EventoFullCalendarDTO;

public class EscalaServiceTest {

	@InjectMocks
	private EscalaService escalaService;

	@Mock
	private EmpregadoDAO empregadoDAO;

	@Mock
	private EscalaDAO escalaDAO;

	@Mock
	private FolgaDAO folgaDAO;

	@Mock
	private FeriadoDAO feriadoDAO;

	@Mock
	private AusenciaEmpregadoDAO ausenciaEmpregadoDAO;

	@Mock
	private CurvaPadraoDAO curvaPadraoDAO;

	@Mock
	private EquipeDAO equipeDAO;

	@Mock
	private EstacaoTrabalhoDAO estacaoTrabalhoDAO;

	private Equipe equipe;
	private Empregado empregado;
	private Escala escala;
	private EquipeEmpregado equipeEmpregado;
	private Folga folga;
	private Ausencia ausencia;
	private Feriado feriado;
	private CurvaPadrao curvaPadrao;
	private EquipeAtividade equipeAtividade;
	private Atividade atividade;
	private EstacaoTrabalho estacaoTrabalho;
	private EventoFullCalendarDTO eventoFullCalendarDTO;
	private List<Empregado> empregados;
	private List<Escala> escalas;
	private List<Folga> folgas;
	private List<SituacaoFolgaEnum> situacaoFolgaEnums;
	private List<Ausencia> ausencias;
	private List<Feriado> feriados;
	private List<CurvaPadrao> curvaPadraos;
	private List<EquipeEmpregado> equipeEmpregados;
	private List<Atividade> atividades;
	private List<EstacaoTrabalho> estacaoTrabalhos;
	private Set<EquipeEmpregado> equipeEmpregados2;
	private Set<EquipeAtividade> equipeAtividades;
	private Set<CurvaPadrao> curvaPadraos2;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		GregorianCalendar gc = new GregorianCalendar();
		gc.add(Calendar.DATE, -15);
		equipe = new Equipe();
		equipe.setId(1L);
		equipe.setNome("Equipe 01");
		equipe.setAtivo(true);

		equipeEmpregados2 = new HashSet<EquipeEmpregado>();
		equipeEmpregado = new EquipeEmpregado();
		equipeEmpregado.setId(1L);
		equipeEmpregado.setSupervisor(false);
		equipeEmpregado.setAtivo(true);
		equipeEmpregado.setEquipe(equipe);
		equipeEmpregado.setHorarioInicio("08:00");
		equipeEmpregado.setHorarioFim("18:00");
		equipeEmpregados2.add(equipeEmpregado);
		equipe.setEquipeEmpregados(equipeEmpregados2);

		equipeAtividades = new HashSet<EquipeAtividade>();
		equipeAtividade = new EquipeAtividade();
		equipeAtividade.setId(1L);

		atividades = new ArrayList<Atividade>();
		atividade = new Atividade();
		atividade.setId(1L);
		atividade.setAtivo(false);

		curvaPadraos2 = new HashSet<CurvaPadrao>();
		curvaPadrao = new CurvaPadrao();
		curvaPadrao.setId(1L);
		curvaPadraos2.add(curvaPadrao);
		atividade.setCurvasPadrao(curvaPadraos2);
		atividades.add(atividade);
		atividade.setAtividadeList(atividades);
		atividade.setHorarioInicio("08:00");
		atividade.setHorarioFim("18:00");
		atividade.setNome("Atividade 01");
		equipeAtividade.setAtividade(atividade);
		equipeAtividades.add(equipeAtividade);
		equipe.setEquipeAtividades(equipeAtividades);

		empregados = new ArrayList<Empregado>();
		empregado = new Empregado();
		empregado.setId(1L);
		empregado.setMaximoFolga(true);
		empregado.setAtivo(true);
		equipeEmpregados = new ArrayList<EquipeEmpregado>();
		equipeEmpregados.add(equipeEmpregado);
		empregado.setEquipesEmpregado(equipeEmpregados);
		empregados.add(empregado);

		equipeEmpregado.setEmpregado(empregado);

		escalas = new ArrayList<Escala>();
		escala = new Escala();
		escala.setId(1L);
		escala.setInicio(gc.getTime());
		escala.setFim(new Date());
		escala.setEquipeEmpregado(equipeEmpregado);
		escalas.add(escala);

		folgas = new ArrayList<Folga>();
		folga = new Folga();
		folga.setId(1L);
		folga.setData(gc.getTime());
		folgas.add(folga);

		situacaoFolgaEnums = new ArrayList<SituacaoFolgaEnum>();
		situacaoFolgaEnums.add(SituacaoFolgaEnum.AGENDADA);
		situacaoFolgaEnums.add(SituacaoFolgaEnum.SUGERIDA);

		ausencias = new ArrayList<Ausencia>();
		ausencia = new Ausencia();
		ausencia.setId(1L);
		ausencia.setDataInicio(gc.getTime());
		ausencia.setDataFim(new Date());
		ausencias.add(ausencia);

		feriados = new ArrayList<Feriado>();
		feriado = new Feriado();
		feriado.setId(1L);
		feriado.setData(gc.getTime());
		feriados.add(feriado);

		curvaPadraos = new ArrayList<CurvaPadrao>();
		curvaPadrao = new CurvaPadrao();
		curvaPadrao.setId(1L);
		curvaPadraos.add(curvaPadrao);

		estacaoTrabalhos = new ArrayList<EstacaoTrabalho>();
		estacaoTrabalho = new EstacaoTrabalho();
		estacaoTrabalho.setId(1L);
		estacaoTrabalhos.add(estacaoTrabalho);

		eventoFullCalendarDTO = new EventoFullCalendarDTO();
		eventoFullCalendarDTO.setId("1");
		eventoFullCalendarDTO.setStart(DateUtil.format(gc.getTime(), EventoFullCalendarDTO.DATE_TIME_PATTERN));
		eventoFullCalendarDTO.setEnd(DateUtil.format(new Date(), EventoFullCalendarDTO.DATE_TIME_PATTERN));
	}

	@Test
	public void testGerarEscalaRevezamentoEmpregadoList() throws BusinessException, ParseException {
		GregorianCalendar gc = new GregorianCalendar();
		gc.add(Calendar.MONTH, -1);

		when(empregadoDAO.findByIdFetch(empregado.getId())).thenReturn(empregado);
		when(escalaDAO.obterEscalasPorEquipeEPeriodo(equipe.getId(), gc.getTime(), new Date())).thenReturn(escalas);
		when(folgaDAO.buscarFolgasNoPeriodo(empregado, gc.getTime(), new Date(), situacaoFolgaEnums)).thenReturn(folgas);
		when(ausenciaEmpregadoDAO.buscarAusenciasNoPeriodo(empregado, gc.getTime(), new Date())).thenReturn(ausencias);
		when(feriadoDAO.buscarFeriadosNoPeriodo(gc.getTime(), new Date())).thenReturn(feriados);
		when(curvaPadraoDAO.buscarPorEquipe(equipe)).thenReturn(curvaPadraos);

		escalaService.gerarEscalaRevezamentoEmpregadoList(equipe, empregados, gc.getTime(), new Date());
	}

	@Test
	public void testGerarEscalaRevezamento() throws BusinessException, ParseException {
		GregorianCalendar gc = new GregorianCalendar();
		gc.add(Calendar.MONTH, -1);

		when(empregadoDAO.findByIdFetch(empregado.getId())).thenReturn(empregado);
		when(equipeDAO.findByIdFetch(equipe.getId())).thenReturn(equipe);
		when(estacaoTrabalhoDAO.findAllAtivas()).thenReturn(estacaoTrabalhos);

		escalaService.gerarEscalaRevezamento(equipe, gc.getTime(), new Date());
	}

	@Test
	public void testCriarObjetoEscala() {
		GregorianCalendar gc = new GregorianCalendar();
		gc.add(Calendar.DATE, -5);

		Escala escala = escalaService.criarObjetoEscala(equipeEmpregado, gc.getTime());

		assertNotNull(escala);
	}

	@Test
	public void testAtualizarEscalasEmpregadoApartirAmanha() throws RequiredException, BusinessException, Exception {
		Calendar amanha = Calendar.getInstance();
		amanha = DateUtils.truncate(amanha, Calendar.DAY_OF_MONTH);
		amanha.add(Calendar.DAY_OF_MONTH, 1);

		when(escalaDAO.obterEscalasFuturasPorEmpregado(equipeEmpregado.getEmpregado().getId(), amanha.getTime())).thenReturn(escalas);

		escalaService.atualizarEscalasEmpregadoApartirAmanha(equipeEmpregado);
	}

	@Test
	public void testAlterarDiaEscala() throws RequiredException, Exception {
		GregorianCalendar gc = new GregorianCalendar();
		gc.add(Calendar.DATE, -20);

		when(escalaDAO.findByIdFetch(escala.getId())).thenReturn(escala);

		escalaService.alterarDiaEscala(eventoFullCalendarDTO, gc.getTime(), new Date());
	}
}
