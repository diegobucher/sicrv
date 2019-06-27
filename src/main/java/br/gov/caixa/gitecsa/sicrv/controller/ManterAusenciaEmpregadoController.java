package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.controller.ContextoController;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.sicrv.enumerator.MotivoAusenciaEmpregadoEnum;
import br.gov.caixa.gitecsa.sicrv.model.Ausencia;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.security.ControleAcesso;
import br.gov.caixa.gitecsa.sicrv.service.AusenciaEmpregadoService;
import br.gov.caixa.gitecsa.sicrv.service.EmpregadoService;
import br.gov.caixa.gitecsa.sicrv.service.EscalaService;

@Named
@ViewScoped
public class ManterAusenciaEmpregadoController extends BaseController implements Serializable {

  private static final long serialVersionUID = -9200436055121702272L;
  public static final String EDITAR = "EDITAR";

  @Inject
  private ContextoController contextoController;

  @Inject
  private AusenciaEmpregadoService ausenciaEmpregadoService;

  private Long idAusenciaEmpregado;
  private String mensagemDatas;

  private Empregado empregado;
  private Ausencia ausencia;

  private List<Ausencia> listaAusenciaEmpregados;

  // ------------------------- Inicio do modal de empregado
  @Inject
  private ControleAcesso controleAcesso;
  @Inject
  private EmpregadoService empregadoService;
  private List<Empregado> listEmpregado;
  private Empregado empregadoSelecionado;

  // ----------------------Final modal empregado
  
  @Inject
  private EscalaService escalaService;

  @PostConstruct
  public void init() {
    this.idAusenciaEmpregado =
        (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("idAusenciaEmpregado");
    limparTela();
    if (this.idAusenciaEmpregado != null) {
      this.ausencia = ausenciaEmpregadoService.findByIdFetchAll(idAusenciaEmpregado);
      this.empregado = this.ausencia.getEmpregado();
    }

    listEmpregado = new ArrayList<Empregado>();
    listEmpregado.addAll(empregadoService.obterEmpregadosPorUnidade(controleAcesso.getUnidade()));
    setEmpregadoSelecionado(new Empregado());
  }

  public String salvar() throws BusinessException, ParseException {
    if (validarCampos()) {
      this.ausencia.setEmpregado(this.empregado);

      //É necessário ser a ausência antes da alteração pois se reduzir 
      Ausencia ausenciaAntesAlteracao = null;
      if (this.idAusenciaEmpregado != null) {
          ausenciaAntesAlteracao = ausenciaEmpregadoService.findByIdFetchAll(this.idAusenciaEmpregado);
      }      
      
      ausenciaEmpregadoService.salvar(this.ausencia);

      if (this.idAusenciaEmpregado != null) {
        
        verificarNecessidadeAdicionarEscala(ausenciaAntesAlteracao);

        contextoController.setCrudMessage(MensagemUtil.obterMensagem("ausenciaEmpregado.mensagem.ausenciaAlteradoComSucesso"));
        return redirectTelaConsulta();

      } else {
        facesMessager.addMessageInfo(MensagemUtil.obterMensagem("ausenciaEmpregado.mensagem.ausenciaSalvoComSucesso"));
      }
      limparTela();
      this.idAusenciaEmpregado = null;
    }
    return null;
  }
  
  private void verificarNecessidadeAdicionarEscala(Ausencia ausenciaAntesAlteracao) throws BusinessException, ParseException{
      
      Date dataInicio = this.ausencia.getDataInicio();
      Date dataFim = this.ausencia.getDataFim();
      
      if (ausenciaAntesAlteracao.getDataInicio().before(dataInicio)){
          dataInicio = ausenciaAntesAlteracao.getDataInicio();
      }

      if (ausenciaAntesAlteracao.getDataFim().after(dataFim)){
          dataFim = ausenciaAntesAlteracao.getDataFim();
      }
      
      ausenciaEmpregadoService.verificarNecessidadeAdicionarEscala(dataInicio, dataFim, this.empregado);

  }
  
  public String validarSalvar() throws BusinessException, ParseException{
    if(validarCampos()){
      
      if(escalaService.existeEscalasFuturasPorEmpregadoPorPeriodo(this.empregado.getId(), ausencia.getDataInicio(), ausencia.getDataFim())){
        showDialog("cancelarEscalaVar");
      } else {
        return salvar();
      }
    }
    
    return null;
  }

  private boolean validarCampos() {

    if (Util.isNullOuVazio(empregado.getMatricula())) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required",
          MensagemUtil.obterMensagem("ausenciaEmpregado.label.matricula")));
      return false;
    }

    if (Util.isNullOuVazio(ausencia.getDataInicio())) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required",
          MensagemUtil.obterMensagem("ausenciaEmpregado.label.dataInicio")));
      return false;
    }

    if (Util.isNullOuVazio(ausencia.getDataFim())) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required",
          MensagemUtil.obterMensagem("ausenciaEmpregado.label.dataFim")));
      return false;
    }

    if (Util.isNullOuVazio(ausencia.getMotivo())) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required",
          MensagemUtil.obterMensagem("ausenciaEmpregado.label.motivo")));
      return false;
    }

    if (validarDatas()) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem(this.mensagemDatas));
      return false;
    }

    if (this.empregado != null && this.empregado.getMatricula() != null) {
      Empregado empregadoCadastrado =
          ausenciaEmpregadoService.obterEmpregadoPorMatriculaUnidade(empregado.getMatricula().toLowerCase(),
              controleAcesso.getUnidade());

      if (empregadoCadastrado == null) {
        facesMessager.addMessageError(MensagemUtil.obterMensagem("ausenciaEmpregado.mensagem.empregadoNaoEncontrado"));
        return false;
      }
    }

    return true;
  }

  private Boolean validarDatas() {

    if (!(ausencia.getDataFim().after(ausencia.getDataInicio()) || ausencia.getDataFim().equals(ausencia.getDataInicio()))) {
      this.mensagemDatas = "ausenciaEmpregado.mensagem.dataFimMenor";
      return Boolean.TRUE;
    }
    if (this.empregado != null && this.empregado.getId() != null && this.ausencia != null) {
      listaAusenciaEmpregados =
          ausenciaEmpregadoService.validarPeriodoExistente(empregado.getMatricula(), ausencia.getDataInicio(),
              ausencia.getDataFim());
      if (listaAusenciaEmpregados != null && !listaAusenciaEmpregados.isEmpty()) {
        for (Ausencia ausenciaCadastrada : listaAusenciaEmpregados) {
          if (ausenciaCadastrada.getId() != ausencia.getId()) {
            this.mensagemDatas = "ausenciaEmpregado.mensagem.dataUtilizada";
            return Boolean.TRUE;
          }
        }
      }
    }
    return Boolean.FALSE;
  }

  public void limparTela() {
    this.empregado = new Empregado();
    this.ausencia = new Ausencia();
    this.mensagemDatas = null;
  }

  public List<MotivoAusenciaEmpregadoEnum> getListaMotivos() {
    return Arrays.asList(MotivoAusenciaEmpregadoEnum.values());
  }

  public void obterEmpregado() {
    if (empregado != null && empregado.getMatricula() != null) {
      Empregado empregadoInformado =
          ausenciaEmpregadoService.obterEmpregadoPorMatriculaUnidade(empregado.getMatricula(), controleAcesso.getUnidade());
      if (empregadoInformado != null) {
        empregado = empregadoInformado;
      } else {
        facesMessager.addMessageError(MensagemUtil.obterMensagem("ausenciaEmpregado.mensagem.empregadoNaoEncontrado"));
        empregado.setNome("");
      }
    }
  }

  public void carregarEmpregado() {
    setEmpregado(getEmpregadoSelecionado());
  }

  public String redirectTelaConsulta() {
    return "/pages/ausenciaEmpregado/consulta.xhtml?faces-redirect=true";
  }

  public int sortTextIgnoreCase(final Object msg1, final Object msg2) {
    return ((String) msg1).compareToIgnoreCase(((String) msg2));
  }

  public String getBreadcrumb() {
    String titulo = "Nova Ausência Empregado";

    if (this.idAusenciaEmpregado != null) {
      titulo = "Edição Ausência Empregado";
    }
    return titulo;
  }

  /** Getters and Setters */
  public Empregado getEmpregado() {
    return empregado;
  }

  public void setEmpregado(Empregado empregado) {
    this.empregado = empregado;
  }

  public Ausencia getAusencia() {
    return ausencia;
  }

  public void setAusencia(Ausencia ausencia) {
    this.ausencia = ausencia;
  }

  public List<Ausencia> getListaAusenciaEmpregados() {
    return listaAusenciaEmpregados;
  }

  public void setListaAusenciaEmpregados(List<Ausencia> listaAusenciaEmpregados) {
    this.listaAusenciaEmpregados = listaAusenciaEmpregados;
  }

  public Long getIdAusenciaEmpregado() {
    return idAusenciaEmpregado;
  }

  public void setIdAusenciaEmpregado(Long idAusenciaEmpregado) {
    this.idAusenciaEmpregado = idAusenciaEmpregado;
  }

  public String getMensagemDatas() {
    return mensagemDatas;
  }

  public void setMensagemDatas(String mensagemDatas) {
    this.mensagemDatas = mensagemDatas;
  }

  public List<Empregado> getListEmpregado() {
    return listEmpregado;
  }

  public void setListEmpregado(List<Empregado> listEmpregado) {
    this.listEmpregado = listEmpregado;
  }

  public Empregado getEmpregadoSelecionado() {
    return empregadoSelecionado;
  }

  public void setEmpregadoSelecionado(Empregado empregadoSelecionado) {
    this.empregadoSelecionado = empregadoSelecionado;
  }

}
