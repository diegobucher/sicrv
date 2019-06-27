package br.gov.caixa.gitecsa.sicrv.dao;

import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;

public interface EquipeDAO extends BaseDAO<Equipe> {

    public List<Equipe> consultar(Equipe equipe);

    public Equipe findByIdFetch(Long idEquipe);

    public List<Equipe> obterEquipePorUnidade(Unidade unidade);

    public List<Equipe> consultarEquipePorEmpregado(Empregado empregado);

    public List<Equipe> obterEquipesAtivas();

    public List<Equipe> obterEquipesAtivasPorUnidade(Integer cgc);
}
