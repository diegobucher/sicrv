package br.gov.caixa.gitecsa.sicrv.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.caixa.gitecsa.arquitetura.hibernate.EnumUserType;
import br.gov.caixa.gitecsa.sicrv.enumerator.PeriodicidadeEnum;

@Entity
@Table(name = "crvtb02_equipe", schema = "crvsm001")
public class Equipe implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nu_equipe", unique = true, nullable = false)
	private Long id;

	@Column(name = "ic_periodicidade", columnDefinition = "integer")
	@Type(type = EnumUserType.CLASS_NAME, parameters = { @Parameter(name = EnumUserType.ENUM_CLASS_NAME_PARAM, value = PeriodicidadeEnum.NOME_ENUM) })
	private PeriodicidadeEnum periodicidade;

	@Column(name = "no_equipe", columnDefinition = "bpchar(300)")
	private String nome;

	// bi-directional many-to-one association to Unidade
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nu_unidade")
	private Unidade unidade;
	
	@Column(name = "ic_ativo", nullable = false)
	private Boolean ativo;
	
	// bi-directional many-to-one association to EquipeAtividade
	@OneToMany(mappedBy = "equipe")
	private Set<EquipeAtividade> equipeAtividades;
	
	// bi-directional many-to-one association to EquipeEmpregado
	@OneToMany(mappedBy = "equipe")
	private Set<EquipeEmpregado> equipeEmpregados;

	public Equipe() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PeriodicidadeEnum getPeriodicidade() {
		return periodicidade;
	}

	public void setPeriodicidade(PeriodicidadeEnum periodicidade) {
		this.periodicidade = periodicidade;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Unidade getUnidade() {
		return this.unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}
	
	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Set<EquipeAtividade> getEquipeAtividades() {
		return this.equipeAtividades;
	}
	
	public EquipeAtividade getEquipeAtividade() {
	  return this.equipeAtividades.iterator().next();
	}

	public void setEquipeAtividades(Set<EquipeAtividade> equipeAtividades) {
		this.equipeAtividades = equipeAtividades;
	}
	
	public Set<EquipeEmpregado> getEquipeEmpregados() {
		return equipeEmpregados;
	}

	public void setEquipeEmpregados(Set<EquipeEmpregado> equipeEmpregados) {
		this.equipeEmpregados = equipeEmpregados;
	}

	public EquipeAtividade addEquipeAtividade(EquipeAtividade equipeAtividade) {
		getEquipeAtividades().add(equipeAtividade);
		equipeAtividade.setEquipe(this);

		return equipeAtividade;
	}

	public EquipeAtividade removeEquipeAtividade(EquipeAtividade equipeAtividade) {
		getEquipeAtividades().remove(equipeAtividade);
		equipeAtividade.setEquipe(null);

		return equipeAtividade;
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
		Equipe other = (Equipe) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}