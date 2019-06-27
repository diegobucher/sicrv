package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.omnifaces.cdi.ViewScoped;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.sicrv.comparator.AnoMesComparator;
import br.gov.caixa.gitecsa.sicrv.enumerator.MotivoAusenciaEmpregadoEnum;
import br.gov.caixa.gitecsa.sicrv.enumerator.SituacaoFolgaEnum;
import br.gov.caixa.gitecsa.sicrv.model.Ausencia;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Escala;
import br.gov.caixa.gitecsa.sicrv.model.Folga;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.DiaDTO;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.MesDTO;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.SemanaDTO;
import br.gov.caixa.gitecsa.sicrv.service.AusenciaEmpregadoService;
import br.gov.caixa.gitecsa.sicrv.service.EmpregadoService;
import br.gov.caixa.gitecsa.sicrv.service.EscalaService;
import br.gov.caixa.gitecsa.sicrv.service.FolgaService;

@Named
@ViewScoped
public class MinhaEscalaController extends BaseController implements Serializable {

  private static final long serialVersionUID = -4438337556151771799L;

  private Date dataInicio;

  private Date dataFim;

  private List<MesDTO> mesDTOList;

  private Long saldoFolgaHoje;

  @Inject
  private FolgaService folgaService;

  @Inject
  private EmpregadoService empregadoService;

  @Inject
  private AusenciaEmpregadoService ausenciaEmpregadoService;

  @Inject
  private EscalaService escalaService;

  private String idFolgaSugeridaExclusao;

  private Empregado empregado;

  @PostConstruct
  private void init() {
    this.empregado = empregadoService.obterEmpregadoPorMatricula((String) JSFUtil.getSessionMapValue("loggedMatricula"));
    this.saldoFolgaHoje = folgaService.calcularSaldoFolga(empregado, new Date());
  }

  public String buscarEscala() throws ParseException {

    this.mesDTOList = new ArrayList<MesDTO>();

    if (validarCampos()) {
      carregarDadosCalendarioEscala();
    }

    return null;
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
      facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.validation.data.incio.menorIgual.data.fim"));
      return false;
    }

    return true;
  }

  private void carregarDadosCalendarioEscala() throws ParseException {
    this.mesDTOList = new ArrayList<MesDTO>();

    List<SituacaoFolgaEnum> situacaoFolgaList = new ArrayList<SituacaoFolgaEnum>();
    situacaoFolgaList.add(SituacaoFolgaEnum.REPOUSO_REMUNERADO);
    List<Ausencia> ausenciaList = ausenciaEmpregadoService.buscarAusenciasNoPeriodo(empregado, dataInicio, dataFim);
    List<Escala> escalaList = escalaService.obterEscalasPorEmpregadoEPeriodo(empregado, dataInicio, dataFim);
    List<Folga> repousoRemuneradoList = folgaService.buscarFolgasNoPeriodo(empregado, dataInicio, dataFim, situacaoFolgaList);

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

          Escala escala = buscarEscala(escalaList, calPercorreMesDTO.getTime());
          Ausencia ausencia = null;
          Folga repousoRemunerado = null;

          if (escala == null) {
            ausencia = buscarAusencia(ausenciaList, calPercorreMesDTO.getTime());

            if (ausencia == null) {
              repousoRemunerado = buscarRepousoRemunerado(repousoRemuneradoList, calPercorreMesDTO.getTime());
            }
          }

          String classeCss = "diaUtilTrabalhado";
          if (escala != null) {
            // SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
            // StringBuilder sbHorario = new StringBuilder();
            // sbHorario.append(sdfHora.format(escala.getInicio()));
            // sbHorario.append(" às ");
            // sbHorario.append(sdfHora.format(escala.getFim()));
            // diaDTO.setHorario(sbHorario.toString());

            diaDTO.setHorario(escala.getHorarioEscalaExibidoNoturnoIncluso(null));
          } else if (ausencia != null) {
            if (ausencia.getMotivo().equals(MotivoAusenciaEmpregadoEnum.ANIVERSARIO)) {
              classeCss = "aniversario";
            } else if (ausencia.getMotivo().equals(MotivoAusenciaEmpregadoEnum.ATESTADO)) {
              classeCss = "atestado";
            } else if (ausencia.getMotivo().equals(MotivoAusenciaEmpregadoEnum.CONVOCACAO_JURI_POPULAR)) {
              classeCss = "juriPolular";
            } else if (ausencia.getMotivo().equals(MotivoAusenciaEmpregadoEnum.DESTACAMENTO)) {
              classeCss = "destacamento";
            } else if (ausencia.getMotivo().equals(MotivoAusenciaEmpregadoEnum.FERIAS)) {
              classeCss = "ferias";
            } else if (ausencia.getMotivo().equals(MotivoAusenciaEmpregadoEnum.LICENCA_OUTROS_MOTIVOS)) {
              classeCss = "licencaOutrosMotivos";
            } else if (ausencia.getMotivo().equals(MotivoAusenciaEmpregadoEnum.LICENCA_PREMIO)) {
              classeCss = "licencaPremio";
            } else if (ausencia.getMotivo().equals(MotivoAusenciaEmpregadoEnum.SUBSTITUICAO)) {
              classeCss = "substituicao";
            }
            diaDTO.setAusencia(ausencia.getMotivo().getDescricao());
          } else if (repousoRemunerado != null) {
            classeCss = "repousoRemunerado";
            diaDTO.setAusencia(SituacaoFolgaEnum.REPOUSO_REMUNERADO.getDescricao());
          }
          if (StringUtils.isNotBlank(diaDTO.getAusencia())) {
            diaDTO.setAusencia(diaDTO.getAusencia().split(" ")[0]);
          }
          diaDTO.setClasseCss(classeCss);
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

      this.mesDTOList.add(mesDTO);
    }

  }

  private Folga buscarRepousoRemunerado(List<Folga> repousoRemuneradoList, Date dataBuscada) throws ParseException {

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Date dataBuscadaFmt = sdf.parse(sdf.format(dataBuscada));

    for (Folga repouso : repousoRemuneradoList) {

      Date descandoFmt = sdf.parse(sdf.format(repouso.getData()));

      if (descandoFmt.equals(dataBuscadaFmt)) {
        return repouso;
      }
    }

    return null;
  }

  private Ausencia buscarAusencia(List<Ausencia> ausenciaList, Date dataBuscada) throws ParseException {

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Date dataBuscadaFmt = sdf.parse(sdf.format(dataBuscada));

    for (Ausencia ausencia : ausenciaList) {

      Date ausenciaInicioFmt = sdf.parse(sdf.format(ausencia.getDataInicio()));
      Date ausenciaFimFmt = sdf.parse(sdf.format(ausencia.getDataFim()));

      if ((ausenciaInicioFmt.before(dataBuscadaFmt) || ausenciaInicioFmt.equals(dataBuscadaFmt))
          && (ausenciaFimFmt.after(dataBuscadaFmt) || ausenciaFimFmt.equals(dataBuscadaFmt))) {
        return ausencia;
      }
    }

    return null;
  }

  private Escala buscarEscala(List<Escala> escalaList, Date dataBuscada) throws ParseException {

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    for (Escala escala : escalaList) {
      if (sdf.format(escala.getInicio()).equals(sdf.format(dataBuscada))) {
        return escala;
      }
    }

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
