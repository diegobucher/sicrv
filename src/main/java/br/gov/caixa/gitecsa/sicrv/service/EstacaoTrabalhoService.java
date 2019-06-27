package br.gov.caixa.gitecsa.sicrv.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.sicrv.dao.EstacaoTrabalhoDAO;
import br.gov.caixa.gitecsa.sicrv.model.EstacaoTrabalho;

@Stateless
public class EstacaoTrabalhoService extends AbstractService<EstacaoTrabalho>{

	private static final long serialVersionUID = -6044223883960124337L;
	
	@Inject
	private EstacaoTrabalhoDAO estacaoTrabalhoDAO;

  @Override
  public List<EstacaoTrabalho> consultar(EstacaoTrabalho entidade) throws Exception {
    return estacaoTrabalhoDAO.findAll();
  }
  
  @Override
  public EstacaoTrabalho save(EstacaoTrabalho entity) throws RequiredException, BusinessException, Exception {
    
    if(StringUtils.isBlank(entity.getNome())){
      throw new BusinessException(MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("estacaoTrabalho.label.nomeEstacao")));
    }
    
    EstacaoTrabalho estacaoExistente = estacaoTrabalhoDAO.buscarPorNome(entity.getNome());
    
    if(estacaoExistente != null){
      throw new BusinessException(MensagemUtil.obterMensagem("estacaoTrabalho.mensagem.nomeEstacaoCadastrada"));
    }
    return super.save(entity);
  }
  
  @Override
  public EstacaoTrabalho update(EstacaoTrabalho entity) throws RequiredException, BusinessException, Exception {
   
    EstacaoTrabalho estacaoExistente = estacaoTrabalhoDAO.buscarPorNome(entity.getNome());

    if(estacaoExistente != null && !estacaoExistente.getId().equals(entity.getId())){
      throw new BusinessException("ESTACAO JA CADASTRADA");
    }
    
    return super.update(entity);
  }

  @Override
  protected void validaCamposObrigatorios(EstacaoTrabalho entity) {
  }

  @Override
  protected void validaRegras(EstacaoTrabalho entity) {
  }

  @Override
  protected void validaRegrasExcluir(EstacaoTrabalho entity) {
  }

  @Override
  protected BaseDAO<EstacaoTrabalho> getDAO() {
    return estacaoTrabalhoDAO;
  }

}
