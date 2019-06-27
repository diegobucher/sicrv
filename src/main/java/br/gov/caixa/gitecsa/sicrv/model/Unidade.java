package br.gov.caixa.gitecsa.sicrv.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the crvtb07_unidade database table.
 * 
 */
@Entity
@Table(name = "crvtb07_unidade", schema = "crvsm001")
public class Unidade implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nu_unidade", unique = true, nullable = false)
	private Long id;

	@Column(name = "de_administrador", nullable = false, length = 300)
	private String administrador;

	@Column(name = "no_unidade", nullable = false, length = 300)
	private String nome;

	@Column(name = "nu_cgc", nullable = false)
	private Integer cgc;

	// bi-directional many-to-one association to Equipe
	@OneToMany(mappedBy = "unidade")
	private List<Equipe> equipes;

	public Unidade() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAdministrador() {
		return administrador;
	}

	public void setAdministrador(String administrador) {
		this.administrador = administrador;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getCgc() {
		return cgc;
	}

	public void setCgc(Integer cgc) {
		this.cgc = cgc;
	}

	public List<Equipe> getEquipes() {
		return this.equipes;
	}

	public void setEquipes(List<Equipe> equipes) {
		this.equipes = equipes;
	}

	public Equipe addEquipe(Equipe equipe) {
		getEquipes().add(equipe);
		equipe.setUnidade(this);

		return equipe;
	}

	public Equipe removeEquipe(Equipe equipe) {
		getEquipes().remove(equipe);
		equipe.setUnidade(null);

		return equipe;
	}
	
	public String getCodigoNome(){
		return getCgc() + " - " + getNome();
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
		Unidade other = (Unidade) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}