package br.gov.caixa.gitecsa.sicrv.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "crvtb11_estacao_trabalho", schema = "crvsm001")
public class EstacaoTrabalho implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_estacao_trabalho", unique = true, nullable = false)
  private Long id;

  @Column(name = "no_estacao_trabalho", columnDefinition = "bpchar(50)")
  private String nome;

  @Column(name = "de_motivo", columnDefinition = "bpchar(300)", insertable = false)
  private String motivo;

  @Column(name = "ic_ativa", nullable = false, insertable = false)
  private Boolean ativa;

  public EstacaoTrabalho() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getMotivo() {
    return motivo;
  }
  
  public String getSituacao() {
    return ativa ? "Ativa" : "Inativa";
  }

  public void setMotivo(String motivo) {
    this.motivo = motivo;
  }

  public Boolean getAtiva() {
    return ativa;
  }

  public void setAtiva(Boolean ativa) {
    this.ativa = ativa;
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
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EstacaoTrabalho other = (EstacaoTrabalho) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

}