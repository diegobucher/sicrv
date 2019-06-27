package br.gov.caixa.gitecsa.sicrv.model.dto;

import br.gov.caixa.gitecsa.sicrv.enumerator.HoraFixaEnum;

public class RelatorioTaxaOcupacaoDetalheHoraDTO implements Comparable<RelatorioTaxaOcupacaoDetalheHoraDTO>{

    private Integer horaInicial;

    private Integer segundaCurvaEsperada;
    private Integer segundaOcupacao;

    private Integer tercaCurvaEsperada;
    private Integer tercaOcupacao;

    private Integer quartaCurvaEsperada;
    private Integer quartaOcupacao;

    private Integer quintaCurvaEsperada;
    private Integer quintaOcupacao;

    private Integer sextaCurvaEsperada;
    private Integer sextaOcupacao;

    private Integer sabadoCurvaEsperada;
    private Integer sabadoOcupacao;

    private Integer domingoCurvaEsperada;
    private Integer domingoOcupacao;

    public Integer getHoraInicial() {
        return horaInicial;
    }
    
    public String getHorarioPeriodo(){
        StringBuilder sb = new StringBuilder();
        sb.append(HoraFixaEnum.valueOf(horaInicial).getDescricao());
        sb.append(" - ");
        Integer horaFim = horaInicial;
        if(horaFim == 23){
            horaFim = 0;
        } else {
            horaFim++;
        }
        sb.append(HoraFixaEnum.valueOf(horaFim).getDescricao());
        return sb.toString();
    }

    public void setHoraInicial(Integer horaInicial) {
        this.horaInicial = horaInicial;
    }

    public Integer getSegundaCurvaEsperada() {
        return segundaCurvaEsperada;
    }

    public void setSegundaCurvaEsperada(Integer segundaCurvaEsperada) {
        this.segundaCurvaEsperada = segundaCurvaEsperada;
    }

    public Integer getSegundaOcupacao() {
        return segundaOcupacao;
    }

    public void setSegundaOcupacao(Integer segundaOcupacao) {
        this.segundaOcupacao = segundaOcupacao;
    }
    
    public String getSegundaComparativoStr(){
        if(this.segundaCurvaEsperada == null){
            return "-";
        } else {
            return this.segundaCurvaEsperada + " - " + this.segundaOcupacao;
        }
    }

    public Integer getTercaCurvaEsperada() {
        return tercaCurvaEsperada;
    }

    public void setTercaCurvaEsperada(Integer tercaCurvaEsperada) {
        this.tercaCurvaEsperada = tercaCurvaEsperada;
    }

    public Integer getTercaOcupacao() {
        return tercaOcupacao;
    }

    public void setTercaOcupacao(Integer tercaOcupacao) {
        this.tercaOcupacao = tercaOcupacao;
    }
    
    public String getTercaComparativoStr(){
        if(this.tercaCurvaEsperada == null){
            return "-";
        } else {
            return this.tercaCurvaEsperada + " - " + this.tercaOcupacao;
        }
    }

    public Integer getQuartaCurvaEsperada() {
        return quartaCurvaEsperada;
    }

    public void setQuartaCurvaEsperada(Integer quartaCurvaEsperada) {
        this.quartaCurvaEsperada = quartaCurvaEsperada;
    }

    public Integer getQuartaOcupacao() {
        return quartaOcupacao;
    }

    public void setQuartaOcupacao(Integer quartaOcupacao) {
        this.quartaOcupacao = quartaOcupacao;
    }
    
    public String getQuartaComparativoStr(){
        if(this.quartaCurvaEsperada == null){
            return "-";
        } else {
            return this.quartaCurvaEsperada + " - " + this.quartaOcupacao;
        }
    }

    public Integer getQuintaCurvaEsperada() {
        return quintaCurvaEsperada;
    }

    public void setQuintaCurvaEsperada(Integer quintaCurvaEsperada) {
        this.quintaCurvaEsperada = quintaCurvaEsperada;
    }

    public Integer getQuintaOcupacao() {
        return quintaOcupacao;
    }

    public void setQuintaOcupacao(Integer quintaOcupacao) {
        this.quintaOcupacao = quintaOcupacao;
    }
    
    public String getQuintaComparativoStr(){
        if(this.quintaCurvaEsperada == null){
            return "-";
        } else {
            return this.quintaCurvaEsperada + " - " + this.quintaOcupacao;
        }
    }

    public Integer getSextaCurvaEsperada() {
        return sextaCurvaEsperada;
    }

    public void setSextaCurvaEsperada(Integer sextaCurvaEsperada) {
        this.sextaCurvaEsperada = sextaCurvaEsperada;
    }

    public Integer getSextaOcupacao() {
        return sextaOcupacao;
    }

    public void setSextaOcupacao(Integer sextaOcupacao) {
        this.sextaOcupacao = sextaOcupacao;
    }
    
    public String getSextaComparativoStr(){
        if(this.sextaCurvaEsperada == null){
            return "-";
        } else {
            return this.sextaCurvaEsperada + " - " + this.sextaOcupacao;
        }
    }

    public Integer getSabadoCurvaEsperada() {
        return sabadoCurvaEsperada;
    }

    public void setSabadoCurvaEsperada(Integer sabadoCurvaEsperada) {
        this.sabadoCurvaEsperada = sabadoCurvaEsperada;
    }

    public Integer getSabadoOcupacao() {
        return sabadoOcupacao;
    }

    public void setSabadoOcupacao(Integer sabadoOcupacao) {
        this.sabadoOcupacao = sabadoOcupacao;
    }

    public String getSabadoComparativoStr(){
        if(this.sabadoCurvaEsperada == null){
            return "-";
        } else {
            return this.sabadoCurvaEsperada + " - " + this.sabadoOcupacao;
        }
    }
    
    public Integer getDomingoCurvaEsperada() {
        return domingoCurvaEsperada;
    }

    public void setDomingoCurvaEsperada(Integer domingoCurvaEsperada) {
        this.domingoCurvaEsperada = domingoCurvaEsperada;
    }

    public Integer getDomingoOcupacao() {
        return domingoOcupacao;
    }

    public void setDomingoOcupacao(Integer domingoOcupacao) {
        this.domingoOcupacao = domingoOcupacao;
    }
    
    public String getDomingoComparativoStr(){
        if(this.domingoCurvaEsperada == null){
            return "-";
        } else {
            return this.domingoCurvaEsperada + " - " + this.domingoOcupacao;
        }
    }

    @Override
    public int compareTo(RelatorioTaxaOcupacaoDetalheHoraDTO relatorio) {
        return this.horaInicial.compareTo(relatorio.getHoraInicial());
    }

}
