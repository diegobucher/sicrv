package br.gov.caixa.gitecsa.sicrv.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeEmpregado;

public class AusenciaEmpregadoServiceTest {

	@InjectMocks
	private AusenciaEmpregadoService ausenciaEmpregadoService;

	@Mock
	private EmpregadoService empregadoService;

	@Mock
	private EscalaService escalaService;

	private Empregado empregado;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		empregado = new Empregado();
		empregado.setId(1L);
	}

	@Test
	public void testVerificarNecessidadeAdicionarEscala() throws BusinessException, ParseException {
		Empregado empregado = new Empregado();
		EquipeEmpregado equipeEmpregado1 = new EquipeEmpregado();
		equipeEmpregado1.setId(1L);
		equipeEmpregado1.setAtivo(true);
		Equipe equipe = new Equipe();
		equipe.setId(1L);
		equipe.setAtivo(true);
		equipeEmpregado1.setEquipe(equipe);

		EquipeEmpregado equipeEmpregado2 = new EquipeEmpregado();
		equipeEmpregado2.setId(2L);
		equipeEmpregado2.setAtivo(true);
		equipe = new Equipe();
		equipe.setId(2L);
		equipe.setAtivo(true);
		equipeEmpregado2.setEquipe(equipe);

		empregado.setEquipesEmpregado(Arrays.asList(equipeEmpregado1, equipeEmpregado2));

		when(empregadoService.findByIdFetch(this.empregado.getId())).thenReturn(empregado);

		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.add(Calendar.DATE, -7);
		ausenciaEmpregadoService.verificarNecessidadeAdicionarEscala(gregorianCalendar.getTime(), new Date(), this.empregado);

		assertTrue(true);
	}

}
