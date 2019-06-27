package br.gov.caixa.gitecsa.sicrv.dao;

import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeAtividade;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;

public interface AtividadeDAO extends BaseDAO<Atividade> {

    public List<Atividade> findAtividadesPai(Unidade unidade);

    public List<Atividade> consultar(Atividade atividade);

    public Atividade findAtividadeByNomeUnidade(String nomeAtividade, Unidade unidade);

    public Atividade findByIdFetchAll(Long id);

    public List<Atividade> findAtividadesFilhas(Long idPai);

    public List<EquipeAtividade> buscarEquipeAtividade(Atividade atividade);

    List<EquipeAtividade> findEquipeAtividadeByEquipe(Equipe equipe);
    
    public List<Atividade> findAtividadesPaiSemCurvaPadrao(Unidade unidade);

}
