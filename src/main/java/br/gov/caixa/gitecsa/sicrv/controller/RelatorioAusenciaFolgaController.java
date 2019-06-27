package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.omnifaces.cdi.ViewScoped;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.controller.ContextoController;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.sicrv.enumerator.SituacaoFolgaEnum;
import br.gov.caixa.gitecsa.sicrv.model.Ausencia;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Folga;
import br.gov.caixa.gitecsa.sicrv.model.dto.AusenciaFolgaDTO;
import br.gov.caixa.gitecsa.sicrv.security.ControleAcesso;
import br.gov.caixa.gitecsa.sicrv.service.AusenciaEmpregadoService;
import br.gov.caixa.gitecsa.sicrv.service.EmpregadoService;
import br.gov.caixa.gitecsa.sicrv.service.FolgaService;

@Named
@ViewScoped
public class RelatorioAusenciaFolgaController extends BaseController implements Serializable {

  private static final long serialVersionUID = 4940610294969962830L;

  @Inject
  private ContextoController contextoController;

  @Inject
  private AusenciaEmpregadoService ausenciaEmpregadoService;

  @Inject
  private FolgaService folgaService;

  // ------------------------- Inicio do modal de empregado
  @Inject
  private ControleAcesso controleAcesso;
  @Inject
  private EmpregadoService empregadoService;
  private List<Empregado> listEmpregado;
  private Empregado empregadoSelecionado;
  // ----------------------Final modal empregado

  private Long idAusenciaEmpregado;
  private Boolean pesquisaAtiva;

  private String filtro;
  private Empregado empregado;
  private Ausencia ausencia;

  // NOVOS
  private List<AusenciaFolgaDTO> ausenciaFolgaList;
  private List<AusenciaFolgaDTO> ausenciaFolgaFiltroList;

  @PostConstruct
  public void init() {
    limpar();
    contextoController.limpar();
    listEmpregado = new ArrayList<Empregado>();
    listEmpregado.addAll(empregadoService.obterEmpregadosPorUnidade(controleAcesso.getUnidade()));
    setEmpregadoSelecionado(new Empregado());

    Empregado empregado = empregadoService.obterEmpregadoPorMatricula(controleAcesso.getUsuario().getNuMatricula());
    empregado = empregadoService.findByIdFetch(empregado.getId());
    if(empregado.getEquipeEmpregadoAtivo() == null || !empregado.getEquipeEmpregadoAtivo().getSupervisor()){
      throw new RuntimeException("O Funcionário não pode visualizar pois não é Supervisor!");
    }
    
  }
  
  /**
   * Modal
   */
  public void carregarEmpregado() {
    setEmpregado(getEmpregadoSelecionado());
  }

  public void consultar() {
    if (empregado != null && !Util.isNullOuVazio(empregado.getMatricula())) {
      obterEmpregado();
    }

    if (validarConsulta()) {
      ausenciaFolgaList = new ArrayList<AusenciaFolgaDTO>();
      
      List<SituacaoFolgaEnum> situacaoList = new ArrayList<SituacaoFolgaEnum>();
      situacaoList.add(SituacaoFolgaEnum.AGENDADA);
      situacaoList.add(SituacaoFolgaEnum.SUGERIDA);
      
      List<Folga> folgaList = null;
      List<Ausencia> listaAusenciaEmpregados = null;
      //Caso tenha selecionado algum empregado, senão filtra por equipe
      if(StringUtils.isNotBlank(empregado.getMatricula())){
        listaAusenciaEmpregados = ausenciaEmpregadoService.consultar(empregado.getMatricula(), getAusencia());
        folgaList =
            folgaService.buscarFolgasNoPeriodo(empregado, ausencia.getDataInicio(), ausencia.getDataFim(), situacaoList);
      } else {
        listaAusenciaEmpregados = ausenciaEmpregadoService.buscarAusenciasUnidadeNoPeriodo(controleAcesso.getUnidade(), getAusencia().getDataInicio(), getAusencia().getDataFim());
          folgaList =
            folgaService.buscarFolgasUnidadeNoPeriodo(controleAcesso.getUnidade(), ausencia.getDataInicio(), ausencia.getDataFim(), situacaoList);
        
      }

      carregarListaDTO(listaAusenciaEmpregados, folgaList);
      
      this.pesquisaAtiva = Boolean.TRUE;
    }
  }

  private void carregarListaDTO(List<Ausencia> listaAusenciaEmpregados, List<Folga> folgaList) {
    this.ausenciaFolgaList = new ArrayList<AusenciaFolgaDTO>();

    if (listaAusenciaEmpregados != null && !listaAusenciaEmpregados.isEmpty()) {
      for (Ausencia ausencia : listaAusenciaEmpregados) {
        AusenciaFolgaDTO dto = new AusenciaFolgaDTO();
        dto.setMatricula(ausencia.getEmpregado().getMatricula());
        dto.setNome(ausencia.getEmpregado().getNome());
        dto.setMotivo(ausencia.getMotivo().getDescricao());
        dto.setDataInicio(ausencia.getDataInicio());
        dto.setDataFim(ausencia.getDataFim());
        ausenciaFolgaList.add(dto);
      }
    }

    if (folgaList != null && !folgaList.isEmpty()) {
      Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
      for (Folga folga : folgaList) {
        String descFolga = "";
        
        if(folga.getData().before(hoje)){
          descFolga = "Gozada";
        }else {
          descFolga = folga.getSituacao().getDescricao();
        }
        AusenciaFolgaDTO dto = new AusenciaFolgaDTO();
        dto.setMatricula(folga.getEmpregado().getMatricula());
        dto.setNome(folga.getEmpregado().getNome());
        dto.setMotivo("Folga - "+descFolga);
        dto.setDataInicio(folga.getData());
        dto.setDataFim(folga.getData());
        ausenciaFolgaList.add(dto);
      }
    }

  }

  public Boolean validarConsulta() {
    // Verifica se nenhum campo da consulta foi preenchido
    if (getAusencia().getDataInicio() == null) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required","Data Inicio"));
      return false;
    }
    if (getAusencia().getDataFim() == null) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required","Data Fim"));
      return false;
    }

    if (getAusencia().getDataInicio() != null && getAusencia().getDataFim() != null
        && getAusencia().getDataInicio().after(getAusencia().getDataFim())) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("ausenciaEmpregado.mensagem.dataFimMenor"));
      return false;
    }

    return true;
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

  private void limpar() {
    this.pesquisaAtiva = Boolean.FALSE;
    this.empregado = new Empregado();
    this.ausencia = new Ausencia();

    this.ausenciaFolgaList = new ArrayList<AusenciaFolgaDTO>();
  }

  public int sortTextIgnoreCase(final Object msg1, final Object msg2) {
    return ((String) msg1).compareToIgnoreCase(((String) msg2));
  }

  public void prepareToResult() throws Exception {
  }

  /** Getters and Setters */
  public Long getIdAusenciaEmpregado() {
    return idAusenciaEmpregado;
  }

  public void setIdAusenciaEmpregado(Long idAusenciaEmpregado) {
    this.idAusenciaEmpregado = idAusenciaEmpregado;
  }

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

  public String getFiltro() {
    return filtro;
  }

  public void setFiltro(String filtro) {
    this.filtro = filtro;
  }

  public Boolean getPesquisaAtiva() {
    return pesquisaAtiva;
  }

  public void setPesquisaAtiva(Boolean pesquisaAtiva) {
    this.pesquisaAtiva = pesquisaAtiva;
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

  public List<AusenciaFolgaDTO> getAusenciaFolgaList() {
    return ausenciaFolgaList;
  }

  public void setAusenciaFolgaList(List<AusenciaFolgaDTO> ausenciaFolgaList) {
    this.ausenciaFolgaList = ausenciaFolgaList;
  }

  public List<AusenciaFolgaDTO> getAusenciaFolgaFiltroList() {
    return ausenciaFolgaFiltroList;
  }

  public void setAusenciaFolgaFiltroList(List<AusenciaFolgaDTO> ausenciaFolgaFiltroList) {
    this.ausenciaFolgaFiltroList = ausenciaFolgaFiltroList;
  }
}
