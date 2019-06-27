package br.gov.caixa.gitecsa.sicrv.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
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
import br.gov.caixa.gitecsa.sicrv.enumerator.HoraFixaEnum;
import br.gov.caixa.gitecsa.sicrv.enumerator.PeriodicidadeEnum;

/**
 * The persistent class for the crvtb03_atividade database table.
 * 
 */
@Entity
@Table(name = "crvtb03_atividade", schema = "crvsm001")
public class Atividade implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nu_atividade")
    private Long id;

    @Column(name = "de_atividade")
    private String descricao;

    @Column(name = "ic_ativo")
    private Boolean ativo;

    @Column(name = "ic_periodicidade", columnDefinition = "bpchar(2)")
    @Type(type = EnumUserType.CLASS_NAME, parameters = { @Parameter(name = EnumUserType.ENUM_CLASS_NAME_PARAM,
            value = PeriodicidadeEnum.NOME_ENUM) })
    private PeriodicidadeEnum periodicidade;

    @Column(name = "no_atividade")
    private String nome;

    @Column(name = "hh_fim")
    private String horarioFim;

    @Column(name = "hh_inicio")
    private String horarioInicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nu_atividade_pai")
    private Atividade atividadePai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nu_unidade")
    private Unidade unidade;

    @OneToMany(mappedBy = "atividadePai")
    private List<Atividade> atividadeList;

    @Column(name = "nu_prioridade")
    private Integer prioridade;

    // bi-directional many-to-one association to EquipeAtividade
    @OneToMany(mappedBy = "atividade", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    private Set<EquipeAtividade> equipeAtividades;

    @OneToMany(mappedBy = "atividade")
    private Set<CurvaPadrao> curvasPadrao;

    public Atividade() {
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getHorarioFim() {
        return this.horarioFim;
    }

    public void setHorarioFim(String horarioFim) {
        this.horarioFim = horarioFim;
    }

    public String getHorarioInicio() {
        return this.horarioInicio;
    }

    public void setHorarioInicio(String horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public Integer getPrioridade() {
        return this.prioridade;
    }

    public void setPrioridade(Integer prioridade) {
        this.prioridade = prioridade;
    }

    public List<Atividade> getAtividadeList() {
        return atividadeList;
    }

    public void setAtividadeList(List<Atividade> atividadeList) {
        this.atividadeList = atividadeList;
    }

    public void setAtividadePai(Atividade atividadePai) {
        this.atividadePai = atividadePai;
    }

    public PeriodicidadeEnum getPeriodicidade() {
        return periodicidade;
    }

    public void setPeriodicidade(PeriodicidadeEnum periodicidade) {
        this.periodicidade = periodicidade;
    }

    public Atividade getAtividadePai() {
        return atividadePai;
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Atividade other = (Atividade) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }

    public Set<EquipeAtividade> getEquipeAtividades() {
        return equipeAtividades;
    }

    public void setEquipeAtividades(Set<EquipeAtividade> equipeAtividades) {
        this.equipeAtividades = equipeAtividades;
    }

    public Set<CurvaPadrao> getCurvasPadrao() {
        return curvasPadrao;
    }

    public void setCurvasPadrao(Set<CurvaPadrao> curvasPadrao) {
        this.curvasPadrao = curvasPadrao;
    }

    public HoraFixaEnum getHorarioInicioEnum() {
        return HoraFixaEnum.valueOfDescricao(this.horarioInicio);
    }

    public HoraFixaEnum getHorarioFimEnum() {
        return HoraFixaEnum.valueOfDescricao(this.horarioFim);
    }

    public void setHorarioInicioEnum(HoraFixaEnum horarioInicio) {
        this.horarioInicio = horarioInicio.getDescricao();
    }

    public void setHorarioFimEnum(HoraFixaEnum horarioFim) {
        this.horarioFim = horarioFim.getDescricao();
    }

}
