package br.gov.caixa.gitecsa.sicrv.model.dto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EscalaSemanaDTO {

  private Date dataInicio;

  private Date dataFim;

  public EscalaSemanaDTO(Date dataInicio, Date dataFim) {
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
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

  @Override
  public String toString() {

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    return "Semana DTO de :" + sdf.format(dataInicio) + " a " + sdf.format(dataFim);
  }

  public Date getDataNaSemana(int calendarInt) {

    Calendar cal = Calendar.getInstance();
    cal.setTime(dataInicio);

    do {
      int diaDaSemana = cal.get(Calendar.DAY_OF_WEEK);

      if (diaDaSemana == calendarInt) {
        return cal.getTime();
      }

      cal.add(Calendar.DATE, 1);
    } while (!cal.getTime().after(dataFim));

    return null;
  }

  public List<Date> getListaDataPorListaInt(List<Integer> calendarIntList) {
    List<Date> dataList = new ArrayList<Date>();

    for (Integer diaInt : calendarIntList) {
      Date data = getDataNaSemana(diaInt);
      dataList.add(data);
    }
    
    return dataList;
  }
}
