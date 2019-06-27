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
@Table(name="crvtb09_equipe_atividade", schema = "crvsm001")
public class EquipeAtividade implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="nu_equipe_atividade", unique=true, nullable=false)
	private Long id;

	//bi-directional many-to-one association to Equipe
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="nu_atividade")
	private Atividade atividade;

	//bi-directional many-to-one association to Equipe
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="nu_equipe")
	private Equipe equipe;

	public Atividade getAtividade() {
		return atividade;
	}

	public void setAtividade(Atividade atividade) {
		this.atividade = atividade;
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
    if (!(obj instanceof EquipeAtividade)) {
      return false;
    }
    EquipeAtividade other = (EquipeAtividade) obj;
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