package br.gov.caixa.gitecsa.sicrv.comparator;

import java.util.Comparator;

public class AnoMesComparator implements Comparator<String> {

  @Override
  public int compare(String anoMes1, String anoMes2) {
    
    Integer ano1 = Integer.parseInt(anoMes1.split("/")[0]);
    Integer mes1 = Integer.parseInt(anoMes1.split("/")[1]);
    
    Integer ano2= Integer.parseInt(anoMes2.split("/")[0]);
    Integer mes2 = Integer.parseInt(anoMes2.split("/")[1]);
    
    if(!ano1.equals(ano2)){
      return ano1.compareTo(ano2);
    }
    
    return mes1.compareTo(mes2);
  }


}
