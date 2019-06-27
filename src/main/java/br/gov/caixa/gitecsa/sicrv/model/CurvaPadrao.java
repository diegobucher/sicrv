package br.gov.caixa.gitecsa.sicrv.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import br.gov.caixa.gitecsa.sicrv.enumerator.DiaSemanaEnum;
import br.gov.caixa.gitecsa.sicrv.enumerator.HoraFixaEnum;
import br.gov.caixa.gitecsa.sicrv.enumerator.PeriodoEnum;

/**
 * The persistent class for the crvtb12_curva_padrao database table.
 * 
 */
@Entity
@Table(name = "crvtb12_curva_padrao", schema = "crvsm001")
public class CurvaPadrao implements Serializable {

	private static final long serialVersionUID = -2472264187982024825L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nu_curva_padrao")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nu_atividade")
	private Atividade atividade;

	@Column(name = "ic_periodo")
	@Type(type = EnumUserType.CLASS_NAME, parameters = { @Parameter(name = EnumUserType.ENUM_CLASS_NAME_PARAM, value = PeriodoEnum.NOME_ENUM) })
	private PeriodoEnum periodo;

	@Enumerated(EnumType.STRING)
	@Column(name = "no_dia_semana", columnDefinition = "bpchar(30)")
	private DiaSemanaEnum dia;

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_especifica")
	private Date dataEspecifica;

	@Column(name = "hh_inicial")
	@Type(type = EnumUserType.CLASS_NAME, parameters = { @Parameter(name = EnumUserType.ENUM_CLASS_NAME_PARAM, value = HoraFixaEnum.NOME_ENUM) })
	private HoraFixaEnum horaInicial;

	@Column(name = "hh_final")
	@Type(type = EnumUserType.CLASS_NAME, parameters = { @Parameter(name = EnumUserType.ENUM_CLASS_NAME_PARAM, value = HoraFixaEnum.NOME_ENUM) })
	private HoraFixaEnum horaFinal;

	@Column(name = "nu_quantidade_chamados")
	private Integer quantidadeChamados;

	@Column(name = "nu_tempo_atendimento")
	private Integer tempoAtendimento;

	@Column(name = "nu_meta")
	private Integer meta;

	@Column(name = "ic_ativo")
	private Boolean ic_ativo;
	
	public CurvaPadrao (){
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Atividade getAtividade() {
		return atividade;
	}

	public void setAtividade(Atividade atividade) {
		this.atividade = atividade;
	}

	public PeriodoEnum getPeriodo() {
		return periodo;
	}

	public void setPeriodo(PeriodoEnum periodo) {
		this.periodo = periodo;
	}

	public DiaSemanaEnum getDia() {
		return dia;
	}

	public void setDia(DiaSemanaEnum dia) {
		this.dia = dia;
	}

	public Date getDataEspecifica() {
		return dataEspecifica;
	}

	public void setDataEspecifica(Date dataEspecifica) {
		this.dataEspecifica = dataEspecifica;
	}

	public Integer getQuantidadeChamados() {
		return quantidadeChamados;
	}

	public void setQuantidadeChamados(Integer quantidadeChamados) {
		this.quantidadeChamados = quantidadeChamados;
	}

	public Integer getTempoAtendimento() {
		return tempoAtendimento;
	}

	public void setTempoAtendimento(Integer tempoAtendimento) {
		this.tempoAtendimento = tempoAtendimento;
	}

	public Integer getMeta() {
		return meta;
	}

	public void setMeta(Integer meta) {
		this.meta = meta;
	}

	public Boolean getIc_ativo() {
		return ic_ativo;
	}

	public void setIc_ativo(Boolean ic_ativo) {
		this.ic_ativo = ic_ativo;
	}
	
	public HoraFixaEnum getHoraInicial() {
		return horaInicial;
	}

	public void setHoraInicial(HoraFixaEnum horaInicial) {
		this.horaInicial = horaInicial;
	}

	public HoraFixaEnum getHoraFinal() {
		return horaFinal;
	}

	public void setHoraFinal(HoraFixaEnum horaFinal) {
		this.horaFinal = horaFinal;
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
		CurvaPadrao other = (CurvaPadrao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public Integer getQuantidadeHoras(){
	    Integer horaFinal = this.getHoraFinal().getValor();
	    
	    if(horaFinal == 0){
	        horaFinal = 24;
	    }
	    
	    return horaFinal - this.getHoraInicial().getValor();
	}
}