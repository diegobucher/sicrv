package br.gov.caixa.gitecsa.sicrv.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.WordUtils;

/**
 * The persistent class for the crvtb01_empregado database table.
 * 
 */
@Entity
@Table(name = "crvtb01_empregado", schema = "crvsm001")
public class Empregado implements Serializable, Comparable<Empregado> {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nu_empregado", unique = true, nullable = false)
	private Long id;

	@Column(name = "co_matricula", length = 7)
	private String matricula;

	@Column(name = "ic_ativo")
	private Boolean ativo;

	@Column(name = "no_funcao", columnDefinition = "bpchar(100)")
	private String funcao;

  @Column(name = "ic_maximo_folga", insertable = false, nullable = false)
  private Boolean maximoFolga;

	@Column(name = "no_empregado", columnDefinition = "bpchar(300)")
	private String nome;

	// bi-directional many-to-one association to Ausencia
	@OneToMany(mappedBy = "empregado")
	private List<Ausencia> ausencias;
	
	// bi-directional many-to-one association to EquipeEmpregado
	@OneToMany(mappedBy = "empregado")
	private List<EquipeEmpregado> equipesEmpregado;
	
	public String getNomeAbreviado(){
	  String nomeAbreviado = "";
	  if(nome != null && nome.trim().length() > 0){
	    String [] tokens = nome.trim().split(" ");
	    String primeiroNome = "";
	    String segundoNome = "";
	    if(tokens != null && tokens.length > 0){
	      primeiroNome += tokens[0];
	      if(tokens.length > 1){
	        primeiroNome += " ";
	        segundoNome = tokens[1];
	        segundoNome = segundoNome.substring(0, 1);
	        segundoNome += ".";
	      }
	      nomeAbreviado = primeiroNome + segundoNome;
	    }
	  }
	  
	  return  WordUtils.capitalizeFully(nomeAbreviado);
	  
	}

	public Empregado() {
	}

	public List<Ausencia> getAusencias() {
		return this.ausencias;
	}

	public void setAusencias(List<Ausencia> ausencias) {
		this.ausencias = ausencias;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getFuncao() {
		return funcao;
	}

	public void setFuncao(String funcao) {
		this.funcao = funcao;
	}

	public Ausencia addAusencia(Ausencia ausencia) {
		getAusencias().add(ausencia);
		ausencia.setEmpregado(this);

		return ausencia;
	}

	public Ausencia removeAusencia(Ausencia ausencia) {
		getAusencias().remove(ausencia);
		ausencia.setEmpregado(null);

		return ausencia;
	}
	

  public EquipeEmpregado addEquipeEmpregado(EquipeEmpregado equipeEmpregado) {
    if(getEquipesEmpregado() == null){
      setEquipesEmpregado(new ArrayList<EquipeEmpregado>());
    }
    getEquipesEmpregado().add(equipeEmpregado);
    equipeEmpregado.setEmpregado(this);

    return equipeEmpregado;
  }

  public EquipeEmpregado removeEquipeEmpregado(EquipeEmpregado equipeEmpregado) {
    if(getEquipesEmpregado() == null){
      setEquipesEmpregado(new ArrayList<EquipeEmpregado>());
    }
    getEquipesEmpregado().remove(equipeEmpregado);
    equipeEmpregado.setEmpregado(null);
    
    return equipeEmpregado;
  }
	
	public List<EquipeEmpregado> getEquipesEmpregado() {
		return equipesEmpregado;
	}
	
	//TODO Adicionado para a geração da escala, mas quando refatorar na tela de Equipe, usar somente 1 equipe para o empregado
	public EquipeEmpregado getEquipeEmpregadoAtivo() {
	  
	  if(equipesEmpregado!=null && !equipesEmpregado.isEmpty()){
	    
	    for (EquipeEmpregado equipeEmpregado : equipesEmpregado) {
        if(equipeEmpregado.getAtivo() && equipeEmpregado.getEquipe().getAtivo()){
          return equipeEmpregado;
        }
      }
	  }
	  
	  return null;
	}

	public void setEquipesEmpregado(List<EquipeEmpregado> equipesEmpregado) {
		this.equipesEmpregado = equipesEmpregado;
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
		Empregado other = (Empregado) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

  public Boolean getMaximoFolga() {
    return maximoFolga;
  }

  public void setMaximoFolga(Boolean maximoFolga) {
    this.maximoFolga = maximoFolga;
  }

  @Override
  public int compareTo(Empregado emp2) {
    return this.nome.compareTo(emp2.getNome());
  }
}