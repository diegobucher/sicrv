package br.gov.caixa.gitecsa.sicrv.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.sicrv.dao.CurvaPadraoDAO;
import br.gov.caixa.gitecsa.sicrv.model.CurvaPadrao;

public class CurvaPadraoServiceTest {

	@InjectMocks
	private CurvaPadraoService curvaPadraoService;

	@Mock
	private CurvaPadraoDAO curvaPadraoDAO;

	private CurvaPadrao curvaPadrao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		curvaPadrao = new CurvaPadrao();
		curvaPadrao.setId(1L);
	}

	@Test
	public void testDelete() {
		when(curvaPadraoDAO.update(curvaPadrao)).thenReturn(curvaPadrao);
		curvaPadraoService.delete(curvaPadrao);
		assertTrue(!curvaPadrao.getIc_ativo());
	}

}
