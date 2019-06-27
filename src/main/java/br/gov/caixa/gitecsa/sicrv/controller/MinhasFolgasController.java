package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.time.DateUtils;
import org.omnifaces.cdi.ViewScoped;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.sicrv.comparator.AnoMesComparator;
import br.gov.caixa.gitecsa.sicrv.enumerator.SituacaoFolgaEnum;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Folga;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.DiaDTO;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.MesDTO;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.SemanaDTO;
import br.gov.caixa.gitecsa.sicrv.service.EmpregadoService;
import br.gov.caixa.gitecsa.sicrv.service.FolgaService;

@Named
@ViewScoped
public class MinhasFolgasController extends BaseController implements Serializable {

  private static final long serialVersionUID = -4438337556151771799L;

  private Date dataInicio;

  private Date dataFim;

  private List<MesDTO> mesDTOList;
  
  private Long saldoFolgaHoje;

  @Inject
  private FolgaService folgaService;

  @Inject
  private EmpregadoService empregadoService;
  
  private String idFolgaSugeridaExclusao;

  @PostConstruct
  private void init() {
    Empregado empregado = empregadoService.obterEmpregadoPorMatricula((String) JSFUtil.getSessionMapValue("loggedMatricula"));
    this.saldoFolgaHoje = folgaService.calcularSaldoFolga(empregado, new Date());
  }

  public String buscarFolgas() {
    
    this.mesDTOList = new ArrayList<MesDTO>();
    
    if(validarCampos()){
      carregarDadosCalendarioFolgas();
    }
    
    return null;
  }
  
  private boolean validarCampos(){
    
    if (Util.isNullOuVazio(dataInicio)) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("geral.label.data.inicio")));
      return false;
    }
    
    if (Util.isNullOuVazio(dataFim)) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required", MensagemUtil.obterMensagem("geral.label.data.fim")));
      return false;
    }

    if (!dataFim.after(dataInicio)) {
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.validation.data.incio.menorIgual.data.fim"));
      return false;
    }
    
    return true;
  }
  
  public String cancelarFolgaSugerida(){
    
    Folga folga = new Folga();
    folga.setId(Long.parseLong(idFolgaSugeridaExclusao));
    try {
      folgaService.remove(folga);
      carregarDadosCalendarioFolgas();
      facesMessager.addMessageInfo("Folga cancelada com sucesso!");
    } catch (Exception e) {
      facesMessager.addMessageError(e);
    }
    return null;
  }

  private void carregarDadosCalendarioFolgas() {
    this.mesDTOList = new ArrayList<MesDTO>();

    Empregado empregado = empregadoService.obterEmpregadoPorMatricula((String) JSFUtil.getSessionMapValue("loggedMatricula"));

    List<Folga> folgaList = new ArrayList<Folga>();
    Map<Date, Folga> dataFolgaMap = new HashMap<Date, Folga>();
    List<SituacaoFolgaEnum> situacaoFolgaList = new ArrayList<SituacaoFolgaEnum>();
    situacaoFolgaList.add(SituacaoFolgaEnum.AGENDADA);
    situacaoFolgaList.add(SituacaoFolgaEnum.ADQUIRIDA);
    situacaoFolgaList.add(SituacaoFolgaEnum.SUGERIDA);
    folgaList = folgaService.buscarFolgasNoPeriodo(empregado, dataInicio, dataFim, situacaoFolgaList);
    
    Long saldoInicialFolgas = folgaService.calcularSaldoFolga(empregado, dataInicio);
    Long saldoVariante = new Long(saldoInicialFolgas);

    for (Folga folga : folgaList) {
      dataFolgaMap.put(folga.getData(), folga);
    }

    Date dateHoje = new Date();
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
        calPercorreMesDTO = DateUtils.truncate(calPercorreMesDTO, Calendar.DATE);
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

          Folga folga = dataFolgaMap.get(calPercorreMesDTO.getTime());
          Boolean adicionarSaldo = null;
          if (folga == null) {
            diaDTO.setClasseCss("diaUtilTrabalhado");
          } else if (folga.getSituacao().equals(SituacaoFolgaEnum.ADQUIRIDA)) {
            diaDTO.setClasseCss("folgaAdquirida");
            adicionarSaldo = true;
          } else if (folga.getData().before(dateHoje)) {
            diaDTO.setClasseCss("folgaGozada");
            adicionarSaldo = false;
          } else if (folga.getSituacao().equals(SituacaoFolgaEnum.SUGERIDA)) {
            diaDTO.setClasseCss("folgaSugerida");
            adicionarSaldo = false;
          } else if (folga.getSituacao().equals(SituacaoFolgaEnum.AGENDADA)) {
            diaDTO.setClasseCss("folgaAgendada");
            adicionarSaldo = false;
          }
          
          if(adicionarSaldo != null){
            saldoVariante = saldoVariante + (adicionarSaldo ? +1 : -1);
            diaDTO.setSaldoFolga(saldoVariante.toString());
            diaDTO.setIdFolga(folga.getId());
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
          calPercorreMesDTO = DateUtils.truncate(calPercorreMesDTO, Calendar.DATE);
        }
      }

      mesDTO.setProjecao(saldoVariante.toString());
      this.mesDTOList.add(mesDTO);
    }

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

  public Long getSaldoFolga() {
    return saldoFolgaHoje;
  }

  public void setSaldoFolga(Long saldoFolga) {
    this.saldoFolgaHoje = saldoFolga;
  }

  public Long getSaldoFolgaHoje() {
    return saldoFolgaHoje;
  }

  public void setSaldoFolgaHoje(Long saldoFolgaHoje) {
    this.saldoFolgaHoje = saldoFolgaHoje;
  }

  public String getIdFolgaSugeridaExclusao() {
    return idFolgaSugeridaExclusao;
  }

  public void setIdFolgaSugeridaExclusao(String idFolgaSugeridaExclusao) {
    this.idFolgaSugeridaExclusao = idFolgaSugeridaExclusao;
  }

}
