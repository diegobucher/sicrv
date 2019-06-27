package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.security.ControleAcesso;
import br.gov.caixa.gitecsa.sicrv.service.EmpregadoService;
import br.gov.caixa.gitecsa.sicrv.service.EquipeService;

@Named
@ViewScoped
public class EditarEscalaController extends BaseController implements Serializable {

  private static final long serialVersionUID = -7477447904704309590L;

  @Inject
  private EquipeService equipeService;

  @Inject
  private ControleAcesso controleAcesso;

  @Inject
  private EmpregadoService empregadoService;

  private Equipe equipe;
  private String dataInicioStr;
  private String dataFimStr;

  private String dataInicioFormatadaStr;
  private String dataFimFormatadaStr;

  @PostConstruct
  public void init() {
    equipe = new Equipe();
  }

  public String atualizarCampos() {

    Empregado empregado = empregadoService.obterEmpregadoPorMatricula(controleAcesso.getUsuario().getNuMatricula());
    empregado = empregadoService.findByIdFetch(empregado.getId());

    equipe = equipeService.findById(equipe.getId());

    if (empregado.getEquipeEmpregadoAtivo() == null || !empregado.getEquipeEmpregadoAtivo().getSupervisor()) {
      facesMessager.addMessageError("O Funcionário não é Supervisor!");
      return null;
    }

    if (!empregado.getEquipeEmpregadoAtivo().getEquipe().equals(equipe)) {
      facesMessager.addMessageError("O Funcionário não é Supervisor da equipe!");
      return null;
    }

    SimpleDateFormat sdfAmericano = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfBrasileiro = new SimpleDateFormat("dd/MM/yyyy");

    try {
      dataInicioFormatadaStr = sdfBrasileiro.format(sdfAmericano.parse(dataInicioStr));
      dataFimFormatadaStr = sdfBrasileiro.format(sdfAmericano.parse(dataFimStr));
    } catch (ParseException e) {
    }

    return null;
  }

  public Equipe getEquipe() {
    return equipe;
  }

  public void setEquipe(Equipe equipe) {
    this.equipe = equipe;
  }

  public String getDataInicioStr() {
    return dataInicioStr;
  }

  public void setDataInicioStr(String dataInicioStr) {
    this.dataInicioStr = dataInicioStr;
  }

  public String getDataFimStr() {
    return dataFimStr;
  }

  public void setDataFimStr(String dataFimStr) {
    this.dataFimStr = dataFimStr;
  }

  public String getDataInicioFormatadaStr() {
    return dataInicioFormatadaStr;
  }

  public void setDataInicioFormatadaStr(String dataInicioFormatadaStr) {
    this.dataInicioFormatadaStr = dataInicioFormatadaStr;
  }

  public String getDataFimFormatadaStr() {
    return dataFimFormatadaStr;
  }

  public void setDataFimFormatadaStr(String dataFimFormatadaStr) {
    this.dataFimFormatadaStr = dataFimFormatadaStr;
  }

}
