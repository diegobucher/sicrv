package br.gov.caixa.gitecsa.sicrv.model.dto;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MesEscalaDTO {

  private Date dataInicio;

  private Date dataFim;

  public MesEscalaDTO(Date dataInicio, Date dataFim) {
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

    if(dataInicio != null && dataFim != null){
      return "Mês de "+getNomeMes()+":" + getPeriodo();
    }else {
      return "";
    }
  }
  
  public String getNomeMes(){
    String nomeMeses[] = new DateFormatSymbols(new Locale("pt", "BR")).getMonths();
    
    if(dataInicio != null && dataFim != null){
      Calendar cal = Calendar.getInstance();
      cal.setTime(dataInicio);
      return nomeMeses[cal.get(Calendar.MONTH)];
    }
    
    return "";
  }
  
  public String getPeriodo(){
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    if(dataInicio != null && dataFim != null){
      return sdf.format(dataInicio) + " a " + sdf.format(dataFim);
    }else {
      return "";
    }
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
