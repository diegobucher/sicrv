package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.ldap.util.Util;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.BancoFolgaDTO;
import br.gov.caixa.gitecsa.sicrv.security.ControleAcesso;
import br.gov.caixa.gitecsa.sicrv.service.EmpregadoService;
import br.gov.caixa.gitecsa.sicrv.service.EquipeService;
import br.gov.caixa.gitecsa.sicrv.service.FolgaService;

@Named
@ViewScoped
public class RelatorioBancoFolgasController extends BaseController implements Serializable {

  private static final long serialVersionUID = 141986430625586252L;
  
  @Inject
  private EquipeService equipeService;

  @Inject
  private EmpregadoService empregadoService;

  @Inject
  private FolgaService folgaService;

  @Inject
  private ControleAcesso controleAcesso;
  
  private Boolean pesquisaAtiva;
  private Date dataProjecao;
  private Equipe equipe;
  private Empregado empregado;
  private String matricula;
  
  private List<BancoFolgaDTO> listaBancoFolgaDTO;
  private List<Equipe> listEquipe;
  
  @PostConstruct
  public void init() {
    limparTela();
    /*  Adição de Filtro da unidade do usuário logado */
    listEquipe = equipeService.obterEquipesAtivasPorUnidade(controleAcesso.getUnidade().getCgc());
  }
  
  public void buscarBancoFolgas(){
    if (validarFiltros()){
      this.listaBancoFolgaDTO = folgaService.buscarListaBancoFolgas(this.dataProjecao, this.equipe, this.empregado, controleAcesso.getUnidade().getCgc());
      if (!this.listaBancoFolgaDTO.isEmpty()){
        this.pesquisaAtiva = Boolean.TRUE;  
       
      } else {
        facesMessager.addMessageError(MensagemUtil.obterMensagem("bancoFolga.mensagem.empregadoEquipeNaoEncontrado"));
        this.pesquisaAtiva = Boolean.FALSE;  
      }
    } 
  }
    
  private boolean validarFiltros() {    
    if (Util.isNullOuVazio(this.dataProjecao)) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required",
          MensagemUtil.obterMensagem("bancoFolga.label.dataProjecao")));
      this.pesquisaAtiva = Boolean.FALSE;
      return false;
    }
    
    if (!Util.isNullOuVazio(this.matricula)){
      this.empregado = empregadoService.obterEmpregadoPorMatricula(this.matricula);
      if (this.empregado!= null && this.empregado.getId() != null){
        this.empregado = empregadoService.findByIdFetch(this.empregado.getId());        
        if (!(this.empregado.getEquipeEmpregadoAtivo() != null && this.empregado.getEquipeEmpregadoAtivo().getEquipe() != null)){
          facesMessager.addMessageError(MensagemUtil.obterMensagem("bancoFolga.mensagem.empregadoNaoEncontrado"));
          this.pesquisaAtiva = Boolean.FALSE;
          return false;
        }
      } else {
        facesMessager.addMessageError(MensagemUtil.obterMensagem("bancoFolga.mensagem.empregadoNaoEncontrado"));
        this.pesquisaAtiva = Boolean.FALSE;
        this.empregado = new Empregado();
        return false;
      }
    } else {
      this.empregado = new Empregado();
    }

    if (this.equipe != null && this.equipe.getId() != null){
      if (this.empregado != null && this.empregado.getId() != null){
        this.empregado = empregadoService.findByIdFetch(this.empregado.getId());
        if (!this.empregado.getEquipeEmpregadoAtivo().getEquipe().getId().equals(this.equipe.getId())){
          facesMessager.addMessageError(MensagemUtil.obterMensagem("bancoFolga.mensagem.empregadoNaoEncontrado"));
          this.pesquisaAtiva = Boolean.FALSE;
          return false;
        }
      } 
    } else {
      this.equipe = new Equipe();
    }
    return true;
  }

  public void limparTela(){
    this.matricula = null;
    this.dataProjecao = null;
    this.pesquisaAtiva = Boolean.FALSE;

    this.equipe = new Equipe();
    this.empregado = new Empregado();
    this.listaBancoFolgaDTO = new ArrayList<BancoFolgaDTO>();
  }

  public Equipe getEquipe() {
    return equipe;
  }

  public void setEquipe(Equipe equipe) {
    this.equipe = equipe;
  }

  public String getMatricula() {
    return matricula;
  }

  public void setMatricula(String matricula) {
    this.matricula = matricula;
  }

  public Date getDataProjecao() {
    return dataProjecao;
  }

  public void setDataProjecao(Date dataProjecao) {
    this.dataProjecao = dataProjecao;
  }

  public List<Equipe> getListEquipe() {
    return listEquipe;
  }

  public void setListEquipe(List<Equipe> listEquipe) {
    this.listEquipe = listEquipe;
  }

  public Empregado getEmpregado() {
    return empregado;
  }

  public void setEmpregado(Empregado empregado) {
    this.empregado = empregado;
  }

  public List<BancoFolgaDTO> getListaBancoFolgaDTO() {
    return listaBancoFolgaDTO;
  }

  public void setListaBancoFolgaDTO(List<BancoFolgaDTO> listaBancoFolgaDTO) {
    this.listaBancoFolgaDTO = listaBancoFolgaDTO;
  }

  public Boolean getPesquisaAtiva() {
    return pesquisaAtiva;
  }

  public void setPesquisaAtiva(Boolean pesquisaAtiva) {
    this.pesquisaAtiva = pesquisaAtiva;
  }
}