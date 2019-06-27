package br.gov.caixa.gitecsa.sicrv.model.dto.folga;

public class EventoFullCalendarDTO {
  
  /**
   * Id da Escala.
   */
  private String id;
  
  /**
   * DataHora Inicio.
   */
  private String start;

  /**
   * DataHora Fim.
   */
  private String end;
  
  public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

  public EventoFullCalendarDTO() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }
}