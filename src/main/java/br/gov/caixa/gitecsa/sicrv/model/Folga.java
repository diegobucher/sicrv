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

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.caixa.gitecsa.arquitetura.hibernate.EnumUserType;
import br.gov.caixa.gitecsa.sicrv.enumerator.SituacaoFolgaEnum;

@Entity
@Table(name = "crvtb14_folga", schema = "crvsm001")
public class Folga implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_folga", unique = true, nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_empregado", nullable = true)
  private Empregado empregado;

  @Temporal(TemporalType.DATE)
  @Column(name = "dt_folga", nullable = false)
  private Date data;

  @Column(name = "ic_folga", columnDefinition = "integer")
  @Type(type = EnumUserType.CLASS_NAME, parameters = { @Parameter(name = EnumUserType.ENUM_CLASS_NAME_PARAM,
      value = SituacaoFolgaEnum.NOME_ENUM) })
  private SituacaoFolgaEnum situacao;

  public Folga() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Empregado getEmpregado() {
    return empregado;
  }

  public void setEmpregado(Empregado empregado) {
    this.empregado = empregado;
  }

  public Date getData() {
    return data;
  }

  public void setData(Date data) {
    this.data = data;
  }

  public SituacaoFolgaEnum getSituacao() {
    return situacao;
  }

  public void setSituacao(SituacaoFolgaEnum situacao) {
    this.situacao = situacao;
  }

}