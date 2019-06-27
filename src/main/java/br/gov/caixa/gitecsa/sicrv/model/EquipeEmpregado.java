package br.gov.caixa.gitecsa.sicrv.model;

import java.io.Serializable;
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
import javax.persistence.Transient;

import br.gov.caixa.gitecsa.sicrv.enumerator.HoraFixaEnum;

@Entity
@Table(name = "crvtb05_equipe_empregado", schema = "crvsm001")
public class EquipeEmpregado implements Serializable, Comparable<EquipeEmpregado> {
  private static final long serialVersionUID = 1216190707723207769L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_equipe_empregado", unique = true, nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_empregado", nullable = false)
  private Empregado empregado;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_equipe", nullable = false)
  private Equipe equipe;

  @Column(name = "ic_supervisor", nullable = false)
  private Boolean supervisor;

  @Column(name = "hh_fim")
  private String horarioFim;

  @Column(name = "hh_inicio")
  private String horarioInicio;
  
  @Column(name = "dt_desligamento")
  @Temporal(TemporalType.DATE)
  private Date dataDesligamento;

  @Column(name = "ic_ativo", nullable = false)
  private Boolean ativo;
  
  @Transient
  private boolean adicionado = false;

  public Empregado getEmpregado() {
    return empregado;
  }

  public void setEmpregado(Empregado empregado) {
    this.empregado = empregado;
  }

  public Equipe getEquipe() {
    return equipe;
  }

  public void setEquipe(Equipe equipe) {
    this.equipe = equipe;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Boolean getSupervisor() {
    return supervisor;
  }

  public void setSupervisor(Boolean supervisor) {
    this.supervisor = supervisor;
  }

  public String getHorarioFim() {
    return this.horarioFim;
  }

  public void setHorarioFim(String horarioFim) {
    this.horarioFim = horarioFim;
  }

  public String getHorarioInicio() {
    return this.horarioInicio;
  }

  public void setHorarioInicio(String horarioInicio) {
    this.horarioInicio = horarioInicio;
  }

  public Boolean getAtivo() {
    return ativo;
  }

  public void setAtivo(Boolean ativo) {
    this.ativo = ativo;
  }

  public HoraFixaEnum getHorarioInicioEnum() {
    return HoraFixaEnum.valueOfDescricao(this.horarioInicio);
  }

  public HoraFixaEnum getHorarioFimEnum() {
    return HoraFixaEnum.valueOfDescricao(this.horarioFim);
  }

  public void setHorarioInicioEnum(HoraFixaEnum horarioInicio) {
    this.horarioInicio = horarioInicio.getDescricao();
  }

  public void setHorarioFimEnum(HoraFixaEnum horarioFim) {
    this.horarioFim = horarioFim.getDescricao();
  }
  
  public String getHorarioCompleto(){
    return this.horarioInicio + " - " + this.horarioFim;
  }

  @Override
  public int compareTo(EquipeEmpregado equipeEmpregado) {
    return this.getEmpregado().getNome().toUpperCase().compareTo(equipeEmpregado.getEmpregado().getNome().toUpperCase());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof EquipeEmpregado)) {
      return false;
    }
    EquipeEmpregado other = (EquipeEmpregado) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

  public boolean isAdicionado() {
    return adicionado;
  }

  public void setAdicionado(boolean adicionado) {
    this.adicionado = adicionado;
  }

  public Date getDataDesligamento() {
    return dataDesligamento;
  }

  public void setDataDesligamento(Date dataDesligamento) {
    this.dataDesligamento = dataDesligamento;
  }
}