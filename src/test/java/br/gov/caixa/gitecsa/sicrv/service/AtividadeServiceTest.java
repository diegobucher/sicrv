package br.gov.caixa.gitecsa.sicrv.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.sicrv.dao.AtividadeDAO;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;

public class AtividadeServiceTest {

	@InjectMocks
	private AtividadeService atividadeService;

	@Mock
	private AtividadeDAO atividadeDAO;

	private Atividade atividade;
	private Unidade unidade;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		atividade = new Atividade();
		atividade.setId(1L);
		atividade.setNome("Atividade teste 01");
		atividade.setPrioridade(2);

		unidade = new Unidade();
		unidade.setId(1L);
		unidade.setNome("Unidade teste 01");
		atividade.setUnidade(unidade);

		Atividade atividadePai = new Atividade();
		atividadePai.setId(3L);
		atividadePai.setNome("Atividade Pai teste");
		atividade.setAtividadePai(atividadePai);

		Atividade atividade2 = new Atividade();
		atividade2.setId(2L);
		atividade2.setNome("Atividade teste 02");
		Atividade atividade3 = new Atividade();
		atividade3.setId(3L);
		atividade3.setNome("Atividade teste 03");
		atividade.setAtividadeList(Arrays.asList(atividade2, atividade3));
	}

	@Test
	public void testDelete() {
		when(atividadeDAO.findByIdFetchAll(this.atividade.getId())).thenReturn(this.atividade);

		atividadeService.delete(this.atividade);

		assertTrue(!this.atividade.getAtivo());

		for (Atividade atividade : this.atividade.getAtividadeList()) {
			assertTrue(!atividade.getAtivo());
		}
	}

	@Test
	public void testValidarSalvarAtividade() throws BusinessException {
		Atividade atividade = new Atividade();
		atividade.setId(1L);
		atividade.setNome("Atividade teste 02");
		atividade.setPrioridade(2);

		Unidade unidade = new Unidade();
		unidade.setId(2L);
		atividade.setUnidade(unidade);

		Atividade atividadePai = new Atividade();
		atividadePai.setId(3L);
		atividade.setAtividadePai(atividadePai);

		when(atividadeDAO.findAtividadeByNomeUnidade(this.atividade.getNome(), this.unidade)).thenReturn(atividade);

		Atividade atividade4 = new Atividade();
		atividade4.setId(4L);
		atividade4.setNome("Atividade teste 04");
		atividade4.setPrioridade(4);

		Atividade atividade5 = new Atividade();
		atividade5.setId(5L);
		atividade5.setNome("Atividade teste 05");
		atividade5.setPrioridade(5);

		List<Atividade> atividades = Arrays.asList(atividade4, atividade5);

		when(atividadeDAO.findAtividadesFilhas(atividadePai.getId())).thenReturn(atividades);

		atividadeService.validarSalvarAtividade(this.atividade);

		assertTrue(true);
	}

}
