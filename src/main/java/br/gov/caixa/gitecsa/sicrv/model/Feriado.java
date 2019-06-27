package br.gov.caixa.gitecsa.sicrv.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "crvtb10_feriado", schema = "crvsm001")
public class Feriado implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_feriado", unique = true, nullable = false)
  private Long id;

  @Temporal(TemporalType.DATE)
  @Column(name = "dt_feriado", nullable = false)
  private Date data;

  @Column(name = "no_feriado", columnDefinition = "bpchar(300)", nullable = false)
  private String nome;

  public Feriado() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getData() {
    return data;
  }

  public void setData(Date data) {
    this.data = data;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }
}