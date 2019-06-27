package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.time.DateUtils;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.sicrv.comparator.AnoMesComparator;
import br.gov.caixa.gitecsa.sicrv.enumerator.SituacaoFolgaEnum;
import br.gov.caixa.gitecsa.sicrv.model.Ausencia;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeEmpregado;
import br.gov.caixa.gitecsa.sicrv.model.Escala;
import br.gov.caixa.gitecsa.sicrv.model.Feriado;
import br.gov.caixa.gitecsa.sicrv.model.Folga;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.DiaDTO;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.MesDTO;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.SemanaDTO;
import br.gov.caixa.gitecsa.sicrv.service.AusenciaEmpregadoService;
import br.gov.caixa.gitecsa.sicrv.service.EmpregadoService;
import br.gov.caixa.gitecsa.sicrv.service.EquipeService;
import br.gov.caixa.gitecsa.sicrv.service.EscalaService;
import br.gov.caixa.gitecsa.sicrv.service.FeriadoService;
import br.gov.caixa.gitecsa.sicrv.service.FolgaService;

@Named
@ViewScoped
public class EquipeFolgasController extends BaseController implements Serializable {

  private static final long serialVersionUID = -4438337556151771799L;

  private Date dataInicio;

  private Date dataFim;

  private List<MesDTO> mesDTOList;

  @Inject
  private FolgaService folgaService;

  @Inject
  private EmpregadoService empregadoService;

  @Inject
  private AusenciaEmpregadoService ausenciaEmpregadoService;

  @Inject
  private EquipeService equipeService;

  @Inject
  private FeriadoService feriadoService;

  @Inject
  private EscalaService escalaService;

  private Equipe equipe;

  private Date diaDetalhe;

  private HashMap<Empregado, Folga> empregadoFolgaMapa;

  private List<Empregado> empregadosAusenteList;

  private HashMap<Empregado, String> empregadoAusenciaMapa;

  private HashMap<Empregado, String> empregadoHorarioMapa;

  private Empregado empregadoSelecionado;

  private List<Folga> folgaExclusaoList;
  
  private Boolean isDiaDetalheDiaUtil;

  @PostConstruct
  private void init() {
    Empregado empregado = empregadoService.obterEmpregadoPorMatricula((String) JSFUtil.getSessionMapValue("loggedMatricula"));

    empregado = empregadoService.findByIdFetch(empregado.getId());

    equipe = empregado.getEquipeEmpregadoAtivo().getEquipe();

  }

  public void salvar() throws BusinessException, Exception {
    List<Folga> folgasList = new ArrayList<Folga>();

    for (Entry<Empregado, Folga> entry : empregadoFolgaMapa.entrySet()) {
      if (entry.getValue() != null && entry.getValue().getId() == null) {
        folgasList.add(entry.getValue());
      }
    }

    if (folgaExclusaoList != null) {
      for (Folga folga : folgaExclusaoList) {
        folgaService.remove(folga);
        if (escalaService.existeEscalasPorEquipePorPeriodo(equipe.getId(), getSegundaFeiraDaSemana(), getDomingoDaSemana())) {
          Escala escala = escalaService.criarObjetoEscala(folga.getEmpregado().getEquipeEmpregadoAtivo(), diaDetalhe);
          escalaService.save(escala);
        }
      }
    }

    folgaService.saveList(folgasList);

    buscarFolgas();

    RequestContext.getCurrentInstance().execute("hideModal()");

  }

  private Date getSegundaFeiraDaSemana() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(diaDetalhe);

    while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
      cal.add(Calendar.DAY_OF_WEEK, -1);
    }

    return cal.getTime();
  }

  private Date getDomingoDaSemana() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(diaDetalhe);

    while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
      cal.add(Calendar.DAY_OF_WEEK, 1);
    }

    return cal.getTime();
  }

  public void adicionarFolga() throws ParseException {

    if (empregadoSelecionado == null || empregadoSelecionado.getId() == null) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required","Empregado"));
      return;
    }

    Folga folga = new Folga();
    folga.setData(diaDetalhe);
    folga.setEmpregado(empregadoSelecionado);
    folga.setSituacao(SituacaoFolgaEnum.AGENDADA);

    empregadoFolgaMapa.put(empregadoSelecionado, folga);
  }

  public void removerFolga(Empregado empregado) throws ParseException {

    Folga folgaAExcluir = empregadoFolgaMapa.get(empregado);

    if (folgaAExcluir.getId() != null) {

      if (folgaAExcluir.getSituacao().equals(SituacaoFolgaEnum.SUGERIDA)) {

        if (!isDiaUtil(diaDetalhe)) {
          facesMessager.addMessageError("Não é possível remover uma Folga Sugerida de um final de semana ou feriado.");
          return;
        }

      }

      folgaExclusaoList.add(folgaAExcluir);
    }
    empregadoFolgaMapa.put(empregado, null);

  }
  
  private Boolean isDiaUtil(Date diaDetalhe){
    Feriado feriado = feriadoService.buscarFeriadoNaData(diaDetalhe);
    Calendar cal = Calendar.getInstance();
    cal.setTime(diaDetalhe);
    
    if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        || feriado != null) {
      return false;
    } else {
      return true;
    }
  }

  public void detalharDia() throws ParseException {

    isDiaDetalheDiaUtil = true;
    folgaExclusaoList = new ArrayList<Folga>();
    empregadoSelecionado = null;

    String diaParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("dia");

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    this.diaDetalhe = sdf.parse(diaParam);
    
    if(!isDiaUtil(diaDetalhe)){
      isDiaDetalheDiaUtil = false;
    }

    Equipe equipe = equipeService.findByIdFetch(this.equipe.getId());

    empregadoHorarioMapa = new HashMap<Empregado, String>();

    List<Empregado> empregadoList = new ArrayList<Empregado>();
    for (EquipeEmpregado equipEmp : equipe.getEquipeEmpregados()) {
      empregadoHorarioMapa.put(equipEmp.getEmpregado(), equipEmp.getHorarioCompleto());
      if (Boolean.FALSE.equals(equipEmp.getSupervisor())) {
        empregadoList.add(equipEmp.getEmpregado());
      }
    }

    // Adiciona no Map de EmpregadoAusenciaMapa os empregados que tem Ausencia
    List<Ausencia> ausenciaList = ausenciaEmpregadoService.buscarAusenciasEquipeNoDia(equipe, diaDetalhe);
    empregadoAusenciaMapa = new HashMap<Empregado, String>();
    empregadosAusenteList = new ArrayList<Empregado>();
    for (Ausencia ausencia : ausenciaList) {
      empregadoAusenciaMapa.put(ausencia.getEmpregado(), ausencia.getMotivo().getDescricao());
      empregadosAusenteList.add(ausencia.getEmpregado());
      empregadoList.remove(ausencia.getEmpregado());
    }

    List<SituacaoFolgaEnum> situacaoRepousoRemuneradoList = new ArrayList<SituacaoFolgaEnum>();
    situacaoRepousoRemuneradoList.add(SituacaoFolgaEnum.REPOUSO_REMUNERADO);
    List<Folga> folgaDescandoRemuneradoList =
        folgaService.buscarFolgasEquipeNoPeriodo(equipe, diaDetalhe, diaDetalhe, situacaoRepousoRemuneradoList);
    for (Folga folga : folgaDescandoRemuneradoList) {
      empregadoAusenciaMapa.put(folga.getEmpregado(), folga.getSituacao().getDescricao());
      empregadosAusenteList.add(folga.getEmpregado());
      empregadoList.remove(folga.getEmpregado());
    }

    // Adiciona no Map de EmpregadoFolga os empregados que tem folga
    List<SituacaoFolgaEnum> situacaoFolgaList = new ArrayList<SituacaoFolgaEnum>();
    situacaoFolgaList.add(SituacaoFolgaEnum.AGENDADA);
    situacaoFolgaList.add(SituacaoFolgaEnum.SUGERIDA);
    List<Folga> folgaList = folgaService.buscarFolgasEquipeNoPeriodo(equipe, diaDetalhe, diaDetalhe, situacaoFolgaList);
    empregadoFolgaMapa = new HashMap<Empregado, Folga>();
    for (Folga folga : folgaList) {
      empregadoFolgaMapa.put(folga.getEmpregado(), folga);
      empregadoList.remove(folga.getEmpregado());
    }

    // O Resto dos empregados coloca folga null
    for (Empregado empregado : empregadoList) {
      empregadoFolgaMapa.put(empregado, null);
    }
  }

  public List<Empregado> getEmpregadosSemFolgas() {
    List<Empregado> empregadoList = new ArrayList<Empregado>();

    if (empregadoFolgaMapa != null) {
      for (Entry<Empregado, Folga> entry : empregadoFolgaMapa.entrySet()) {
        if (entry.getValue() == null) {
          empregadoList.add(entry.getKey());
        }
      }

      Collections.sort(empregadoList);
    }
    return empregadoList;
  }

  public List<Empregado> getEmpregadosComFolgas() {
    List<Empregado> empregadoList = new ArrayList<Empregado>();

    if (empregadoFolgaMapa != null) {

      for (Entry<Empregado, Folga> entry : empregadoFolgaMapa.entrySet()) {
        if (entry.getValue() != null) {
          empregadoList.add(entry.getKey());
        }
      }
      Collections.sort(empregadoList);
    }
    return empregadoList;
  }

  public String buscarFolgas() {

    this.mesDTOList = new ArrayList<MesDTO>();

    if (validarCampos()) {
      carregarDadosCalendarioFolgas();
    }

    return null;
  }

  private void carregarDadosCalendarioFolgas() {
    this.mesDTOList = new ArrayList<MesDTO>();

    List<SituacaoFolgaEnum> situacaoFolgaList = new ArrayList<SituacaoFolgaEnum>();
    situacaoFolgaList.add(SituacaoFolgaEnum.AGENDADA);
    situacaoFolgaList.add(SituacaoFolgaEnum.SUGERIDA);

    List<Date> listaDatasComFolga = new ArrayList<Date>();
    List<Date> listaDatasComFolgaBD = folgaService.buscarDatasFolgasAPartir(equipe, dataInicio, dataFim, situacaoFolgaList);
    
    //Truncar
    for (Date date : listaDatasComFolgaBD) {
      listaDatasComFolga.add(DateUtils.truncate(date, Calendar.DATE));
    }

    // Pegar os meses entre as datas
    Calendar calInicio = Calendar.getInstance();
    Calendar calFim = Calendar.getInstance();

    calInicio.setTime(DateUtils.truncate(dataInicio, Calendar.DATE));
    calFim.setTime(DateUtils.truncate(dataFim, Calendar.DATE));

    // GERAR OS MESES ENTRE AS DATAS DA TELA
    Set<String> anoMesSet = new HashSet<String>();

    Calendar calMeses = Calendar.getInstance();
    calMeses.setTime(calInicio.getTime());
    do {
      anoMesSet.add(calMeses.get(Calendar.YEAR) + "/" + calMeses.get(Calendar.MONTH));
      calMeses.add(Calendar.MONTH, 1);
    } while (calMeses.getTime().before(calFim.getTime()));
    anoMesSet.add(calFim.get(Calendar.YEAR) + "/" + calFim.get(Calendar.MONTH));

    List<String> anoMesList = new ArrayList<String>(anoMesSet);
    Collections.sort(anoMesList, new AnoMesComparator());

    // EM CADA MES VAI GERAR AS SEMANAS
    for (String string : anoMesList) {
      MesDTO mesDTO = new MesDTO();
      Integer ano = Integer.parseInt(string.split("/")[0]);
      Integer mes = Integer.parseInt(string.split("/")[1]);

      mesDTO.setAnoInt(ano);
      mesDTO.setMesInt(mes);

      Calendar calPercorreMesDTO = Calendar.getInstance();
      calPercorreMesDTO.set(ano, mes, calPercorreMesDTO.getActualMinimum(Calendar.DAY_OF_MONTH));
      calPercorreMesDTO = DateUtils.truncate(calPercorreMesDTO, Calendar.DATE);

      Calendar calInicioMesDTO = Calendar.getInstance();
      calInicioMesDTO.setTime(calPercorreMesDTO.getTime());

      Calendar calFimMesDTO = Calendar.getInstance();
      calFimMesDTO.setTime(calPercorreMesDTO.getTime());
      calFimMesDTO.set(Calendar.DAY_OF_MONTH, calFimMesDTO.getActualMaximum(Calendar.DAY_OF_MONTH));

      // AGORA TEM QUE VOLTAR ATÉ ACHAR A PRIMEIRA SEGUNDA FEIRA ANTERIOR
      while (calPercorreMesDTO.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
        calPercorreMesDTO.add(Calendar.DAY_OF_MONTH, -1);
      }

      SemanaDTO semanaDTO = new SemanaDTO();

      while (calPercorreMesDTO.getTime().before(calFimMesDTO.getTime())
          || calPercorreMesDTO.getTime().equals(calFimMesDTO.getTime())) {

        if (calPercorreMesDTO.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
          semanaDTO = new SemanaDTO();
          mesDTO.getSemanaDTOList().add(semanaDTO);
        }

        // Dias Anteriores a dataInicio
        DiaDTO diaDTO = new DiaDTO();
        if (!calPercorreMesDTO.before(calInicioMesDTO) && !calPercorreMesDTO.before(calInicio)
            && !calPercorreMesDTO.after(calFim)) {
          diaDTO.setNumeroDia(String.valueOf(calPercorreMesDTO.get(Calendar.DAY_OF_MONTH)));
          diaDTO.setData(calPercorreMesDTO.getTime());

          diaDTO.setClasseCss("diaUtilTrabalhado");
          if (listaDatasComFolga.contains(calPercorreMesDTO.getTime())) {
            diaDTO.setClasseCss("folgaEquipe");
          }

        }

        semanaDTO.getDiaDTOList().add(diaDTO);

        calPercorreMesDTO.add(Calendar.DAY_OF_MONTH, 1);
        calPercorreMesDTO = DateUtils.truncate(calPercorreMesDTO, Calendar.DATE);

      }

      if (calPercorreMesDTO.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {

        // APOS PREENCHER OS DIAS NORMAIS, CONTINUA ATE ACHAR UM DOMINGO
        while (calPercorreMesDTO.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
          DiaDTO diaDTO = new DiaDTO();
          semanaDTO.getDiaDTOList().add(diaDTO);
          calPercorreMesDTO.add(Calendar.DAY_OF_MONTH, 1);
        }
      }

      this.mesDTOList.add(mesDTO);
    }

  }

  private boolean validarCampos() {

    if (Util.isNullOuVazio(dataInicio)) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required",
          MensagemUtil.obterMensagem("geral.label.data.inicio")));
      return false;
    }

    if (Util.isNullOuVazio(dataFim)) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required",
          MensagemUtil.obterMensagem("geral.label.data.fim")));
      return false;
    }

    if (!dataFim.after(dataInicio)) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.validation.data.inicio.menor.data.fim"));
      return false;
    }

    return true;
  }

  public String cancelarFolgaSugerida() {
    return null;
  }

  public Date getDataInicio() {
    return dataInicio;
  }

  public void setDataInicio(Date dataInicio) {
    this.dataInicio = dataInicio;
  }

  public Date getDataFim() {
    return dataFim;
  }

  public void setDataFim(Date dataFim) {
    this.dataFim = dataFim;
  }

  public List<MesDTO> getMesDTOList() {
    return mesDTOList;
  }

  public void setMesDTOList(List<MesDTO> mesDTOList) {
    this.mesDTOList = mesDTOList;
  }

  public Date getDiaDetalhe() {
    return diaDetalhe;
  }

  public void setDiaDetalhe(Date diaDetalhe) {
    this.diaDetalhe = diaDetalhe;
  }

  public List<Empregado> getEmpregadosAusenteList() {
    return empregadosAusenteList;
  }

  public void setEmpregadosAusenteList(List<Empregado> empregadosAusenteList) {
    this.empregadosAusenteList = empregadosAusenteList;
  }

  public Empregado getEmpregadoSelecionado() {
    return empregadoSelecionado;
  }

  public void setEmpregadoSelecionado(Empregado empregadoSelecionado) {
    this.empregadoSelecionado = empregadoSelecionado;
  }

  public List<Folga> getFolgaExclusaoList() {
    return folgaExclusaoList;
  }

  public void setFolgaExclusaoList(List<Folga> folgaExclusaoList) {
    this.folgaExclusaoList = folgaExclusaoList;
  }

  public HashMap<Empregado, String> getEmpregadoAusenciaMapa() {
    return empregadoAusenciaMapa;
  }

  public void setEmpregadoAusenciaMapa(HashMap<Empregado, String> empregadoAusenciaMapa) {
    this.empregadoAusenciaMapa = empregadoAusenciaMapa;
  }

  public HashMap<Empregado, String> getEmpregadoHorarioMapa() {
    return empregadoHorarioMapa;
  }

  public void setEmpregadoHorarioMapa(HashMap<Empregado, String> empregadoHorarioMapa) {
    this.empregadoHorarioMapa = empregadoHorarioMapa;
  }

  public Boolean getIsDiaDetalheDiaUtil() {
    return isDiaDetalheDiaUtil;
  }

  public void setIsDiaDetalheDiaUtil(Boolean isDiaDetalheDiaUtil) {
    this.isDiaDetalheDiaUtil = isDiaDetalheDiaUtil;
  }

}
