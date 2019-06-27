package br.gov.caixa.gitecsa.sicrv.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "crvtb06_escala", schema = "crvsm001")
public class Escala implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_escala", unique = true, nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_equipe_empregado", nullable = false)
  private EquipeEmpregado equipeEmpregado;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_estacao_trabalho")
  private EstacaoTrabalho estacaoTrabalho;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "dh_inicio")
  private Date inicio;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "dh_fim")
  private Date fim;

  @Column(name = "no_dia_semana", columnDefinition = "bpchar(100)")
  private String diaSemana;

  @Column(name = "ic_ativo", insertable = false, nullable = false)
  private Boolean ativo;

  public Escala() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public EquipeEmpregado getEquipeEmpregado() {
    return equipeEmpregado;
  }

  public void setEquipeEmpregado(EquipeEmpregado equipeEmpregado) {
    this.equipeEmpregado = equipeEmpregado;
  }

  public EstacaoTrabalho getEstacaoTrabalho() {
    return estacaoTrabalho;
  }

  public void setEstacaoTrabalho(EstacaoTrabalho estacaoTrabalho) {
    this.estacaoTrabalho = estacaoTrabalho;
  }

  public Date getInicio() {
    return inicio;
  }

  public void setInicio(Date inicio) {
    this.inicio = inicio;
  }

  public Date getFim() {
    return fim;
  }

  public void setFim(Date fim) {
    this.fim = fim;
  }

  public String getDiaSemana() {
    return diaSemana;
  }

  public void setDiaSemana(String diaSemana) {
    this.diaSemana = diaSemana;
  }

  public Boolean getAtivo() {
    return ativo;
  }

  public void setAtivo(Boolean ativo) {
    this.ativo = ativo;
  }
  
  public String getHorarioEscalaExibidoNoturnoIncluso(String separador) {

    Calendar cal = Calendar.getInstance();
    cal.setTime(this.getInicio());

    Integer qtdHorasNoturnas = 0;
    final Integer DESCANSO_POR_HORA_NOTURNA = 8;

    while (!cal.getTime().equals(this.getFim())) {

      if (cal.get(Calendar.HOUR_OF_DAY) >= 22 || cal.get(Calendar.HOUR_OF_DAY) <= 6) {
        qtdHorasNoturnas++;
      }
      cal.add(Calendar.HOUR_OF_DAY, 1);
    }

    Integer qtdMinutosDescanso = qtdHorasNoturnas * DESCANSO_POR_HORA_NOTURNA;

    Calendar calHoraFim = Calendar.getInstance();
    calHoraFim.setTime(this.getFim());
    calHoraFim.add(Calendar.MINUTE, (-1) * qtdMinutosDescanso);

    StringBuilder sbHorario = new StringBuilder();
    SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
    sbHorario.append(sdfHora.format(this.getInicio()));
    if(separador == null){
      sbHorario.append(" às ");
    } else {
      sbHorario.append(" "+separador+" ");
    }
    sbHorario.append(sdfHora.format(calHoraFim.getTime()));

    return sbHorario.toString();
  }

}