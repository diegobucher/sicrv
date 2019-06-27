package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.controller.ContextoController;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.sicrv.comparator.AtividadeNomeComparator;
import br.gov.caixa.gitecsa.sicrv.enumerator.HoraFixaEnum;
import br.gov.caixa.gitecsa.sicrv.enumerator.PeriodicidadeEnum;
import br.gov.caixa.gitecsa.sicrv.enumerator.TipoAtividadeEnum;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeAtividade;
import br.gov.caixa.gitecsa.sicrv.model.dto.EquipeDTO;
import br.gov.caixa.gitecsa.sicrv.security.ControleAcesso;
import br.gov.caixa.gitecsa.sicrv.service.AtividadeService;
import br.gov.caixa.gitecsa.sicrv.service.EquipeService;

@Named
@ViewScoped
public class ManterAtividadeController extends BaseController implements Serializable {

  private static final long serialVersionUID = 7549837331749728175L;

  public static final String EDITAR = "EDITAR";

  public static final String DETALHAR = "DETALHAR";

  @Inject
  private ContextoController contextoController;

  @Inject
  private ControleAcesso controleAcesso;

  @Inject
  private AtividadeService atividadeService;

  @Inject
  private EquipeService equipeService;

  private Atividade atividade;

  private List<Atividade> atividadePaiList;

  private List<Equipe> equipeList;

  private List<Equipe> equipeSelecionadaList;

  private TipoAtividadeEnum tipoAtividadeEnum;

  private PeriodicidadeEnum periodicidade;

  private String acao;

  @PostConstruct
  public void init() {

    Long idAtividade = (Long) contextoController.getObject();
    acao = contextoController.getAcao();

    limpar();

    if ((EDITAR.equals(acao) || DETALHAR.equals(acao)) && idAtividade != null) {
      this.atividade = atividadeService.findByIdFetchAll(idAtividade);
      if (atividade.getAtividadePai() != null && atividade.getAtividadePai().getId() != null) {
        tipoAtividadeEnum = TipoAtividadeEnum.SUBATIVIDADE;
      }
      List<EquipeAtividade> listEquipeAtividade = atividadeService.buscarEquipeAtividade(atividade);

      if (listEquipeAtividade != null && !listEquipeAtividade.isEmpty()) {
        equipeSelecionadaList = new ArrayList<Equipe>();
        for (EquipeAtividade equipeAtividade : listEquipeAtividade) {
          equipeSelecionadaList.add(equipeAtividade.getEquipe());
        }
      }
      montarEquipe();
      isNomeVoltar();
    }
  }

  private void limpar() {

    atividade = new Atividade();
    tipoAtividadeEnum = TipoAtividadeEnum.ATIVIDADE;

    atividadePaiList = atividadeService.findAtividadesPaiSemCurvaPadrao(controleAcesso.getUnidade());
    if (atividadePaiList != null) {
      Collections.sort(atividadePaiList, new AtividadeNomeComparator());
    }
  }

  public String salvar() {

    if (isCamposValidos()) {

      try {

        atividade.setUnidade(controleAcesso.getUnidade());
        atividadeService.validarSalvarAtividade(atividade);
        
        if(atividade.getAtividadePai() != null ){
          atividade.setPeriodicidade(atividade.getAtividadePai().getPeriodicidade());
        }

        atividade.setAtivo(true);
        List<EquipeAtividade> listEquipeAtividade = new ArrayList<EquipeAtividade>();
        if (equipeSelecionadaList != null) {
          for (Equipe equipe : equipeSelecionadaList) {
            EquipeAtividade equipeAtividade = new EquipeAtividade();
            equipeAtividade.setAtividade(getAtividade());
            equipeAtividade.setEquipe(equipe);
            listEquipeAtividade.add(equipeAtividade);
          }
        }
        atividade.setEquipeAtividades(new HashSet<EquipeAtividade>(listEquipeAtividade));
        atividadeService.salvar(atividade);

        if (EDITAR.equals(acao)) {
          contextoController.setCrudMessage(MensagemUtil.obterMensagem("geral.crud.atualizada", "atividade.label.atividade"));
        } else {
          contextoController.setCrudMessage(MensagemUtil.obterMensagem("geral.crud.salva", "atividade.label.atividade"));
        }

        return redirectTelaConsulta();

      } catch (BusinessException e) {
        facesMessager.addMessageError(e.getMessage());
      }

    }

    return null;
  }

  private boolean isCamposValidos() {

    if (Util.isNullOuVazio(atividade.getNome())) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required",
          MensagemUtil.obterMensagem("atividade.label.nomeAtividade")));
      return false;
    }

    if (tipoAtividadeEnum.equals(TipoAtividadeEnum.ATIVIDADE) && atividade.getPeriodicidade() == null) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required",
          MensagemUtil.obterMensagem("atividade.label.escala")));
      return false;
    } else if (tipoAtividadeEnum.equals(TipoAtividadeEnum.SUBATIVIDADE) && atividade.getAtividadePai() == null) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required",
          MensagemUtil.obterMensagem("atividade.label.macroAtividade")));
      return false;
    }

    if (Util.isNullOuVazio(atividade.getHorarioInicio())) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required",
          MensagemUtil.obterMensagem("atividade.label.horaInicio")));
      return false;
    }

    if (Util.isNullOuVazio(atividade.getHorarioFim())) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required",
          MensagemUtil.obterMensagem("atividade.label.horaFim")));
      return false;
    }

    if (tipoAtividadeEnum.equals(TipoAtividadeEnum.SUBATIVIDADE) && Util.isNullOuVazio(atividade.getPrioridade())) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required",
          MensagemUtil.obterMensagem("atividade.label.prioridade")));
      return false;
    }

    return true;
  }

  public String redirectTelaManter() {
    return "/pages/atividade/manter.xhtml?faces-redirect=true";
  }

  public String redirectTelaConsulta() {
    if (Util.DETALHAR.equals(contextoController.getAcao())) {
      return contextoController.getTelaOrigem();
    }
    return "/pages/atividade/consulta.xhtml?faces-redirect=true";
  }

  public Atividade getAtividade() {
    return atividade;
  }

  public void setAtividade(Atividade atividade) {
    this.atividade = atividade;
  }

  public TipoAtividadeEnum getTipoAtividadeEnum() {
    return tipoAtividadeEnum;
  }

  public void setTipoAtividadeEnum(TipoAtividadeEnum tipoAtividadeEnum) {
    this.tipoAtividadeEnum = tipoAtividadeEnum;
  }

  public TipoAtividadeEnum[] getTipoAtividadeList() {
    return TipoAtividadeEnum.values();
  }

  public PeriodicidadeEnum[] getPeriodicidadeList() {
    return PeriodicidadeEnum.values();
  }

  public PeriodicidadeEnum getPeriodicidade() {
    return periodicidade;
  }

  public void setPeriodicidade(PeriodicidadeEnum periodicidade) {
    this.periodicidade = periodicidade;
  }

  public List<Atividade> getAtividadePaiList() {
    return atividadePaiList;
  }

  public void setAtividadePaiList(List<Atividade> atividadePaiList) {
    this.atividadePaiList = atividadePaiList;
  }

  public String getTitulo() {
    String titulo = "NOVA ATIVIDADE";

    if (EDITAR.equals(acao)) {
      titulo = "EDIÇÃO ATIVIDADE";
    } else if (DETALHAR.equals(acao)) {
      titulo = "DETALHE ATIVIDADE";
    }

    return titulo;
  }

  public String getBreadcrumb() {
    String titulo = "Nova Atividade";

    if (EDITAR.equals(acao)) {
      titulo = "Edição Atividade";
    } else if (DETALHAR.equals(acao)) {
      titulo = "Detalhe Atividade";
    }

    return titulo;
  }

  public boolean isDesativarCampos() {

    if (DETALHAR.equals(acao)) {
      return true;
    }
    return false;
  }

  public boolean isNomeVoltar() {
    if (DETALHAR.equals(acao) && contextoController.getObjectFilter() instanceof EquipeDTO) {
      return true;
    }
    return false;
  }

  public void montarEquipe() {
    try {
      if (tipoAtividadeEnum == TipoAtividadeEnum.ATIVIDADE && atividade.getPeriodicidade() != null) {
        Equipe equipe = new Equipe();
        equipe.setUnidade(controleAcesso.getUnidade());
        equipe.setPeriodicidade(atividade.getPeriodicidade());
        setEquipeList(equipeService.consultar(equipe));
      } else {
        setEquipeList(new ArrayList<Equipe>());
      }
    } catch (Exception e) {
      e.printStackTrace();
      facesMessager.addMessageError(e.getMessage());
    }
  }

  public List<Equipe> getEquipeList() {
    return equipeList;
  }

  public void setEquipeList(List<Equipe> equipeList) {
    this.equipeList = equipeList;
  }

  public List<Equipe> getEquipeSelecionadaList() {
    return equipeSelecionadaList;
  }

  public void setEquipeSelecionadaList(List<Equipe> equipeSelecionadaList) {
    this.equipeSelecionadaList = equipeSelecionadaList;
  }

  public List<HoraFixaEnum> getListaHoras() {
    return Arrays.asList(HoraFixaEnum.values());
  }

}
