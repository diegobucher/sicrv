package br.gov.caixa.gitecsa.sicrv.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "crvtb13_solicitacao", schema = "crvsm001")
public class Solicitacao implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_solicitacao", unique = true, nullable = false)
  private Long id;

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="nu_escala", nullable = true)
  private Escala escala;
  
  @Column(name = "de_solicitacao", columnDefinition = "bpchar(300)", nullable = false)
  private String descricao;
    
  @Column(name = "ic_situacao", nullable = false)
  private Integer situacao;
  
  @Column(name = "co_matricula_supervisor",  columnDefinition = "bpchar(7)")
  private String matriculaSupervisor;

  public Solicitacao() {
  }

  public Long getId() {
	return id;
  }

  public void setId(Long id) {
	this.id = id;
  }

  public Escala getEscala() {
	return escala;
  }

  public void setEscala(Escala escala) {
	this.escala = escala;
  }

  public String getDescricao() {
	return descricao;
  }

  public void setDescricao(String descricao) {
	this.descricao = descricao;
  }

  public Integer getSituacao() {
	return situacao;
  }

  public void setSituacao(Integer situacao) {
	this.situacao = situacao;
  }

  public String getMatriculaSupervisor() {
	return matriculaSupervisor;
  }

  public void setMatriculaSupervisor(String matriculaSupervisor) {
	this.matriculaSupervisor = matriculaSupervisor;
  }
  
}