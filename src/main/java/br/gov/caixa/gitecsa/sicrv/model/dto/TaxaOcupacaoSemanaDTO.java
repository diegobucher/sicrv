package br.gov.caixa.gitecsa.sicrv.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaxaOcupacaoSemanaDTO {
    
    private Date dataInicio;
    
    private Date dataFim;
    
    private List<RelatorioTaxaOcupacaoDetalheHoraDTO> detalhamentoSemanaList;
    
    
    public TaxaOcupacaoSemanaDTO() {
        detalhamentoSemanaList = new ArrayList<RelatorioTaxaOcupacaoDetalheHoraDTO>();
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public List<RelatorioTaxaOcupacaoDetalheHoraDTO> getDetalhamentoSemanaList() {
        return detalhamentoSemanaList;
    }

    public void setDetalhamentoSemanaList(List<RelatorioTaxaOcupacaoDetalheHoraDTO> detalhamentoSemanaList) {
        this.detalhamentoSemanaList = detalhamentoSemanaList;
    }

}
