package br.gov.caixa.gitecsa.sicrv.dao;

import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.sicrv.model.EstacaoTrabalho;

public interface EstacaoTrabalhoDAO extends BaseDAO<EstacaoTrabalho> {

  public abstract EstacaoTrabalho buscarPorNome(String nome);

  public List<EstacaoTrabalho> findAllAtivas();
}
