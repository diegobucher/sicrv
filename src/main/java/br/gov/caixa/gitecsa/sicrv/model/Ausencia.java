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

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.caixa.gitecsa.arquitetura.hibernate.EnumUserType;
import br.gov.caixa.gitecsa.sicrv.enumerator.MotivoAusenciaEmpregadoEnum;

@Entity
@Table(name = "crvtb04_ausencia", schema = "crvsm001")
public class Ausencia implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_ausencia", unique = true, nullable = false)
  private Long id;

  @Column(name = "ic_motivo", columnDefinition = "integer")
  @Type(type = EnumUserType.CLASS_NAME, parameters = { @Parameter(name = EnumUserType.ENUM_CLASS_NAME_PARAM,
      value = MotivoAusenciaEmpregadoEnum.NOME_ENUM) })
  private MotivoAusenciaEmpregadoEnum motivo;

  @Column(name = "dh_inicio", nullable = false)
  private Date dataInicio;

  @Column(name = "dh_fim", nullable = false)
  private Date dataFim;

  // bi-directional many-to-one association to Empregado
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_empregado")
  private Empregado empregado;

  public Ausencia() {
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public MotivoAusenciaEmpregadoEnum getMotivo() {
    return motivo;
  }

  public void setMotivo(MotivoAusenciaEmpregadoEnum motivo) {
    this.motivo = motivo;
  }

  public Date getDataFim() {
    return this.dataFim;
  }

  public void setDataFim(Date dataFim) {
    this.dataFim = dataFim;
  }

  public Date getDataInicio() {
    return this.dataInicio;
  }

  public void setDataInicio(Date dataInicio) {
    this.dataInicio = dataInicio;
  }

  public Empregado getEmpregado() {
    return this.empregado;
  }

  public void setEmpregado(Empregado empregado) {
    this.empregado = empregado;
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
    if (!(obj instanceof Ausencia)) {
      return false;
    }
    Ausencia other = (Ausencia) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

}