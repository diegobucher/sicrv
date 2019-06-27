package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseViewCrud;
import br.gov.caixa.gitecsa.arquitetura.controller.ContextoController;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.service.BaseService;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.ldap.usuario.UsuarioLdap;
import br.gov.caixa.gitecsa.sicrv.enumerator.HoraFixaEnum;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeAtividade;
import br.gov.caixa.gitecsa.sicrv.model.EquipeEmpregado;
import br.gov.caixa.gitecsa.sicrv.model.dto.EquipeDTO;
import br.gov.caixa.gitecsa.sicrv.security.ControleAcesso;
import br.gov.caixa.gitecsa.sicrv.service.AtividadeService;
import br.gov.caixa.gitecsa.sicrv.service.EmpregadoService;
import br.gov.caixa.gitecsa.sicrv.service.EquipeEmpregadoService;
import br.gov.caixa.gitecsa.sicrv.service.EquipeService;
import br.gov.caixa.gitecsa.sicrv.service.EscalaService;

@Named
@ViewScoped
public class ManterEquipeController extends BaseViewCrud<Equipe> implements Serializable {

  private static final long serialVersionUID = -6700359979354152479L;

  public static String ABA_EQUIPE = "equipe";
  public static String ABA_ATIDADE = "atividade";
  public static String ABA_EMPREGADO = "empregado";

  @Inject
  private ContextoController contextoController;

  @Inject
  private EquipeService equipeService;

  @Inject
  private EquipeEmpregadoService equipeEmpregadoService;

  @Inject
  private ControleAcesso controleAcesso;

  @Inject
  private AtividadeService atividadeService;

  @Inject
  private EmpregadoService empregadoService;

  @Inject
  private EscalaService escalaService;

  private List<Atividade> listAtividade;

  private List<Atividade> listAtividadeSelecionadas;

  private List<EquipeEmpregado> listEquipeEmpregado;

  private EquipeEmpregado equipeEmpregado;

  private EquipeEmpregado equipeEmpregadoValidaEdicao;

  private EquipeEmpregado instanceCRUD;

  private Boolean detalhar;

  private String abaAtiva;

  private String msgConfirmExclusaoEmpregado;
  
  private Boolean flagGerarEscalaNovosEmpregados;

  @PostConstruct
  public void init() throws Exception {
    if (getInstance() == null) {
      newInstance();
    }
    setAbaAtiva(ABA_EQUIPE);
    setListEquipeEmpregado(new ArrayList<EquipeEmpregado>());
    setListAtividade(new ArrayList<Atividade>());
    setListAtividadeSelecionadas(new ArrayList<Atividade>());
    setDetalhar(Boolean.FALSE);
    setFlagGerarEscalaNovosEmpregados(false);

    Object objetoFiltro = contextoController.getObjectFilter();
    if (objetoFiltro instanceof EquipeDTO) {
      EquipeDTO equipeDTO = (EquipeDTO) objetoFiltro;
      setInstance(equipeDTO.getEquipe());
      setListAtividadeSelecionadas(equipeDTO.getListAtividadeSelecionadas());
      setListEquipeEmpregado(equipeDTO.getListEquipeEmpregado());
      montarAtividade();
      setAbaAtiva(ABA_ATIDADE);
    }

    Equipe equipe = ((Equipe) FacesContext.getCurrentInstance().getExternalContext().getFlash().get(Util.OBJETO));
    if (equipe != null && !Util.isNullOuVazio(equipe.getId())) {
      setInstance(equipeService.findByIdFetch(equipe.getId()));

      // Feito isso para retirar os espaços em branco
      getInstance().setNome(getInstance().getNome().trim());
      if (getInstance().getEquipeEmpregados() != null) {
        setListEquipeEmpregado(new ArrayList<>(getInstance().getEquipeEmpregados()));
        Collections.sort(getListEquipeEmpregado());
      }
      if (getInstance().getEquipeAtividades() != null) {
        for (EquipeAtividade equipeAtividade : getInstance().getEquipeAtividades()) {
          listAtividadeSelecionadas.add(equipeAtividade.getAtividade());
        }
      }

      String acao = (String) FacesContext.getCurrentInstance().getExternalContext().getFlash().get(Util.ACAO);
      if (!Util.isNullOuVazio(acao) && Util.DETALHAR.equals(acao)) {
        setDetalhar(Boolean.TRUE);
      }
    }

    getInstance().setUnidade(controleAcesso.getUnidade());
    setEquipeEmpregado(new EquipeEmpregado());
    getEquipeEmpregado().setEmpregado(new Empregado());
  }

  private void montarAtividade() {

    Atividade atividade = new Atividade();
    atividade.setUnidade(getInstance().getUnidade());

    if (!Util.isNullOuVazio(getInstance().getPeriodicidade())) {
      atividade.setPeriodicidade(getInstance().getPeriodicidade());
    }
    setListAtividade(new ArrayList<Atividade>());
    getListAtividade().addAll(atividadeService.consultar(atividade));
  }

  public String redirectTelaConsulta() {
    return "/pages/equipe/consulta.xhtml?faces-redirect=true";
  }

  public String redirectTelaManter() {
    return "/pages/equipe/manter.xhtml?faces-redirect=true";
  }
  
  public String preValidarSalvar() throws Exception{
    
    //Se for uma alteração de equipe
    if (getInstance().getId() != null) {
      
      Boolean existeEscalasFuturas = escalaService.existeEscalasFuturasPorEquipe(getInstance().getId());
      
      //Caso existam escalas futuras geradas e Algum novo empregado adicionado
      if(existeEscalasFuturas){
        
        List<EquipeEmpregado> empregadosNovosList = new ArrayList<EquipeEmpregado>();
        // Procura por Empregados Adicionados
        for (EquipeEmpregado equipeEmpregadoSelecionado : listEquipeEmpregado) {
          
          boolean empregadoJaExistente = false;
          for (EquipeEmpregado equipeEmpregadoCadastrado : getInstance().getEquipeEmpregados()) {
            if (equipeEmpregadoCadastrado.getEmpregado().equals(equipeEmpregadoSelecionado.getEmpregado())) {
              empregadoJaExistente = true;
              break;
            }
          }
          // Se ela não existe no banco então é nova, E NAO for supervisor
          if (!empregadoJaExistente && !equipeEmpregadoSelecionado.getSupervisor()) {
            empregadosNovosList.add(equipeEmpregadoSelecionado);
          } 
        }
        
        if(empregadosNovosList != null && !empregadosNovosList.isEmpty()){
          showDialog("gerarEscalaVar");
          return null;
        }
        
      }
    } 
    
    return salvar();
  }

  public String salvar() throws Exception {
    try {
      if (getInstance().getId() == null) {
        equipeService.incluir(getInstance(), getListAtividadeSelecionadas(), getListEquipeEmpregado());
        contextoController.setCrudMessage(MensagemUtil.obterMensagem("geral.crud.salva", "geral.breadcrumb.equipe"));
      } else {
        equipeService.alterar(getInstance(), getListAtividadeSelecionadas(), getListEquipeEmpregado(), flagGerarEscalaNovosEmpregados);
        contextoController.setCrudMessage(MensagemUtil.obterMensagem("geral.crud.atualizada", "geral.breadcrumb.equipe"));
      }
      return redirectTelaConsulta();
    } catch (BusinessException e) {
      getFacesMessager().addMessageError(e.getMessage());
    }
    return null;
  }

  public String getBreadcrumb() {
    String titulo = "Nova Equipe";

    if (getInstance().getId() != null && getDetalhar()) {
      titulo = "Detalhar Equipe";
    } else if (getInstance().getId() != null) {
      titulo = "Edição Equipe";
    }
    return titulo;
  }

  public EquipeEmpregado getInstanceExcluir() {
    return instanceCRUD;
  }

  public void setInstanceCRUD(EquipeEmpregado instanceCRUD) {
    this.instanceCRUD = instanceCRUD;
  }

  public void excluir() {

    if (instanceCRUD.getId() != null) {
      getListEquipeEmpregado().remove(instanceCRUD);
    } else {
      Iterator<EquipeEmpregado> it = getListEquipeEmpregado().iterator();
      while (it.hasNext()) {
        EquipeEmpregado next = it.next();
        if (next.getEmpregado() == instanceCRUD.getEmpregado() ||
            next.getEmpregado().equals(instanceCRUD.getEmpregado()) 
            ) {
          it.remove();
        }
      }
    }

    setInstanceCRUD(null);
  }

  public void validarExcluir(EquipeEmpregado equipeEmpregado) {
    this.msgConfirmExclusaoEmpregado = MensagemUtil.obterMensagem("geral.crud.confirmExcluir");

    if (equipeEmpregado.getEmpregado().getId() != null) {
      if (escalaService.existeEscalasFuturasPorEmpregado(equipeEmpregado.getEmpregado().getId())) {
        this.msgConfirmExclusaoEmpregado =
            MensagemUtil.obterMensagem("equipe.message.delete.empregado.empregadoEscaladoNoFuturo");
      }
    }
    this.instanceCRUD = equipeEmpregado;
  }

  public void validarEditar(EquipeEmpregado equipeEmpregado) {
    boolean existeEscalanoFuturo = false;
    equipeEmpregadoValidaEdicao = null;
         
    if (equipeEmpregado.getEmpregado().getId() != null) {
      if (escalaService.existeEscalasFuturasPorEmpregado(equipeEmpregado.getEmpregado().getId())) {
        existeEscalanoFuturo = true;
      }
    }
    
    if(!existeEscalanoFuturo){
      editar(equipeEmpregado);
    } else {
      equipeEmpregadoValidaEdicao = equipeEmpregado;
      showDialog("edicaoVar");
    }
    
  }

  public void editar(EquipeEmpregado equipeEmpregado) {
    setInstanceCRUD(equipeEmpregado);
    EquipeEmpregado equipeEmpregadoAlterar = new EquipeEmpregado();

    // Realiza o preenchimento de empregado buscando no LDAP novamente.
    Empregado empregado = new Empregado();
    UsuarioLdap usuarioLDAP = controleAcesso.buscarUsuarioLDAP(equipeEmpregado.getEmpregado().getMatricula().toUpperCase());
    if (usuarioLDAP != null) {
      empregado.setNome(usuarioLDAP.getNomeUsuario());
      empregado.setFuncao(usuarioLDAP.getNoFuncao());
    } else {
      empregado.setNome(equipeEmpregado.getEmpregado().getNome());
      empregado.setFuncao(equipeEmpregado.getEmpregado().getFuncao());
    }
    empregado.setId(equipeEmpregado.getEmpregado().getId());
    empregado.setMatricula(equipeEmpregado.getEmpregado().getMatricula());
    empregado.setAtivo(equipeEmpregado.getEmpregado().getAtivo());
    empregado.setMaximoFolga(equipeEmpregado.getEmpregado().getMaximoFolga());

    equipeEmpregadoAlterar.setEmpregado(empregado);
    equipeEmpregadoAlterar.setEquipe(equipeEmpregado.getEquipe());
    equipeEmpregadoAlterar.setSupervisor(equipeEmpregado.getSupervisor());
    equipeEmpregadoAlterar.setHorarioInicio(equipeEmpregado.getHorarioInicio());
    equipeEmpregadoAlterar.setHorarioFim(equipeEmpregado.getHorarioFim());
    equipeEmpregadoAlterar.setAtivo(equipeEmpregado.getAtivo());
    equipeEmpregadoAlterar.setId(equipeEmpregado.getId());

    setEquipeEmpregado(equipeEmpregadoAlterar);
  }

  public void detalhar(EquipeEmpregado equipeEmpregado) {

  }

  public String detalharAtividade(Atividade atividade) {
    contextoController.limpar();
    contextoController.setObject(atividade.getId());
    contextoController.setAcao(Util.DETALHAR);
    contextoController.setTelaOrigem(redirectTelaManter());

    // Retorno para minha tela de manterEquipe
    EquipeDTO equipeDTO = new EquipeDTO();
    equipeDTO.setEquipe(getInstance());
    equipeDTO.setListAtividadeSelecionadas(getListAtividadeSelecionadas());
    equipeDTO.setListEquipeEmpregado(getListEquipeEmpregado());
    contextoController.setObjectFilter(equipeDTO);

    return "/pages/atividade/manter.xhtml?faces-redirect=true";
  }

  public String onFlowProcess(FlowEvent event) {

    if (event.getOldStep().equals(ABA_EQUIPE)) {
      if (Util.isNullOuVazio(getInstance().getNome())) {
        getFacesMessager().addMessageError(
            MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("equipe.label.nomeEquipe")));
        return ABA_EQUIPE;
      }
      if (Util.isNullOuVazio(getInstance().getPeriodicidade())) {
        getFacesMessager().addMessageError(
            MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("equipe.label.periodicidade")));
        return ABA_EQUIPE;
      }

      montarAtividade();
    }
    if (event.getOldStep().equals(ABA_ATIDADE)) {
      if (event.getNewStep().equals(ABA_EMPREGADO) && getListAtividadeSelecionadas().isEmpty()) {
        getFacesMessager().addMessageError(MensagemUtil.obterMensagem("equipe.message.atividadeNaoSelecionada"));
        return ABA_ATIDADE;
      }
    }
    return event.getNewStep();
  }

  public void obterEmpregado() {
    if (Util.isNullOuVazio(getEquipeEmpregado().getEmpregado().getMatricula())) {
      getFacesMessager().addMessageError(
          MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("equipe.label.matricula")));
    } else {
      UsuarioLdap usuarioLDAP = controleAcesso.buscarUsuarioLDAP(equipeEmpregado.getEmpregado().getMatricula().toUpperCase());
      if (usuarioLDAP != null) {
        Empregado empregadoInformado = empregadoService.obterEmpregadoPorMatricula(usuarioLDAP.getNuMatricula().toUpperCase());
        if (empregadoInformado == null) {
          // TODO Verificar o valor dos campos que não são exibidos se estão sendo salvos corretamente
          empregadoInformado = new Empregado();
          empregadoInformado.setAtivo(Boolean.TRUE);
          empregadoInformado.setMaximoFolga(Boolean.FALSE);
        }

        empregadoInformado.setMatricula(usuarioLDAP.getNuMatricula().toUpperCase());
        empregadoInformado.setNome(usuarioLDAP.getNomeUsuario());
        empregadoInformado.setFuncao(usuarioLDAP.getNoFuncao() != null ? usuarioLDAP.getNoFuncao() : "");
        equipeEmpregado.setEmpregado(empregadoInformado);
      } else {
        getFacesMessager().addMessageInfo(MensagemUtil.obterMensagem("equipe.mensagem.empregadoNaoEncontrado"));
      }
    }
  }

  public void adicionarEquipeEmpregado() {
    if (validarCamposAdicaoEquipeEmpregado()) {
      equipeEmpregado.setAtivo(Boolean.TRUE);
      equipeEmpregado.setEquipe(getInstance());
      equipeEmpregado.setAdicionado(Boolean.TRUE);
      listEquipeEmpregado.add(equipeEmpregado);
      setEquipeEmpregado(new EquipeEmpregado());
      getEquipeEmpregado().setEmpregado(new Empregado());
      if (instanceCRUD != null) {
        listEquipeEmpregado.remove(instanceCRUD);
        setInstanceCRUD(null);
      }
          
      RequestContext.getCurrentInstance().execute("dtItem.filter();");
      Collections.sort(listEquipeEmpregado);
    }
  }

  private boolean validarCamposAdicaoEquipeEmpregado() {
    if (Util.isNullOuVazio(getEquipeEmpregado().getEmpregado().getMatricula())
        || Util.isNullOuVazio(getEquipeEmpregado().getEmpregado().getNome())) {
      getFacesMessager().addMessageError(
          MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("equipe.label.matricula")));
      return false;
    }
    if (Util.isNullOuVazio(getEquipeEmpregado().getHorarioInicio())) {
      getFacesMessager().addMessageError(
          MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("equipe.label.horaInicio")));
      return false;
    }
    if (Util.isNullOuVazio(getEquipeEmpregado().getHorarioFim())) {
      getFacesMessager().addMessageError(
          MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("equipe.label.horaFim")));
      return false;
    }

     Integer horaInicio = Integer.valueOf(getEquipeEmpregado().getHorarioInicio().replace(":", ""));
     Integer horaFinal = Integer.valueOf(getEquipeEmpregado().getHorarioFim().replace(":", ""));
    
     if (horaInicio.equals(horaFinal) ) {
       getFacesMessager().addMessageError(MensagemUtil.obterMensagem("equipe.message.horaInicioIgualHoraFinal"));
       return false;
     }

    if (getEquipeEmpregado() != null && getEquipeEmpregado().getEmpregado().getMatricula() != null) {
      EquipeEmpregado empregadoEquipe =
          equipeEmpregadoService.obterEmpregadoOutrasEquipes(getEquipeEmpregado().getEmpregado().getId());
      if (empregadoEquipe != null) {
        if (!(empregadoEquipe.getEquipe().getId().equals(getInstance().getId()))) {
          // if (getEquipeEmpregado().getEquipe()== null ||
          // !(empregadoEquipe.getEquipe().getId().equals(getEquipeEmpregado().getEquipe().getId()))){
          getFacesMessager().addMessageError(MensagemUtil.obterMensagem("equipe.message.empregadoAssociadoOutraEquipe"));
          return false;
        }
      }
    }

    for (EquipeEmpregado equipeEmp : listEquipeEmpregado) {
      if (equipeEmp.getEmpregado().getMatricula().equals(getEquipeEmpregado().getEmpregado().getMatricula())) {
        if (instanceCRUD != null
            && instanceCRUD.getEmpregado().getMatricula().equals(getEquipeEmpregado().getEmpregado().getMatricula())) {
          continue;
        }
        getFacesMessager().addMessageError(MensagemUtil.obterMensagem("equipe.message.empregadoJaAdicionado"));
        return false;
      }
    }
    return true;
  }

  public List<HoraFixaEnum> getListaHoras() {
    return Arrays.asList(HoraFixaEnum.values());
  }

  @Override
  protected BaseService<Equipe> getService() {
    return equipeService;
  }

  @Override
  protected Long getEntityId(Equipe referenceValue) {
    return referenceValue.getId();
  }

  @Override
  protected Equipe newInstance() {
    return new Equipe();
  }

  public List<Atividade> getListAtividade() {
    return listAtividade;
  }

  public void setListAtividade(List<Atividade> listAtividade) {
    this.listAtividade = listAtividade;
  }

  public List<EquipeEmpregado> getListEquipeEmpregado() {
    return listEquipeEmpregado;
  }

  public void setListEquipeEmpregado(List<EquipeEmpregado> listEquipeEmpregado) {
    this.listEquipeEmpregado = listEquipeEmpregado;
  }

  public EquipeEmpregado getEquipeEmpregado() {
    return equipeEmpregado;
  }

  public void setEquipeEmpregado(EquipeEmpregado equipeEmpregado) {
    this.equipeEmpregado = equipeEmpregado;
  }

  public List<Atividade> getListAtividadeSelecionadas() {
    return listAtividadeSelecionadas;
  }

  public void setListAtividadeSelecionadas(List<Atividade> listAtividadeSelecionadas) {
    this.listAtividadeSelecionadas = listAtividadeSelecionadas;
  }

  public Boolean getDetalhar() {
    return detalhar;
  }

  public void setDetalhar(Boolean detalhar) {
    this.detalhar = detalhar;
  }

  public String getAbaAtiva() {
    return abaAtiva;
  }

  public void setAbaAtiva(String abaAtiva) {
    this.abaAtiva = abaAtiva;
  }

  public String getMsgConfirmExclusaoEmpregado() {
    return msgConfirmExclusaoEmpregado;
  }

  public void setMsgConfirmExclusaoEmpregado(String msgConfirmExclusaoEmpregado) {
    this.msgConfirmExclusaoEmpregado = msgConfirmExclusaoEmpregado;
  }

  public EquipeEmpregado getEquipeEmpregadoValidaEdicao() {
    return equipeEmpregadoValidaEdicao;
  }

  public void setEquipeEmpregadoValidaEdicao(EquipeEmpregado equipeEmpregadoValidaEdicao) {
    this.equipeEmpregadoValidaEdicao = equipeEmpregadoValidaEdicao;
  }

  public Boolean getFlagGerarEscalaNovosEmpregados() {
    return flagGerarEscalaNovosEmpregados;
  }

  public void setFlagGerarEscalaNovosEmpregados(Boolean flagGerarEscalaNovosEmpregados) {
    this.flagGerarEscalaNovosEmpregados = flagGerarEscalaNovosEmpregados;
  }
}
