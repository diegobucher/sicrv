package br.gov.caixa.gitecsa.sicrv.model.dto;

import java.util.ArrayList;
import java.util.List;

public class RelatorioTaxaOcupacaoDTO {

    private List<String> nomeAtividadesList;

    private List<Float> ocupacaoDataSet;

    private List<Float> curvaPadraoDataSet;
    
    public RelatorioTaxaOcupacaoDTO() {
        this.nomeAtividadesList = new ArrayList<String>();
        this.ocupacaoDataSet = new ArrayList<Float>();
        this.curvaPadraoDataSet = new ArrayList<Float>();
    }

    public List<String> getNomeAtividadesList() {
        return nomeAtividadesList;
    }

    public void setNomeAtividadesList(List<String> nomeAtividadesList) {
        this.nomeAtividadesList = nomeAtividadesList;
    }

    public List<Float> getOcupacaoDataSet() {
        return ocupacaoDataSet;
    }

    public void setOcupacaoDataSet(List<Float> ocupacaoDataSet) {
        this.ocupacaoDataSet = ocupacaoDataSet;
    }

    public List<Float> getCurvaPadraoDataSet() {
        return curvaPadraoDataSet;
    }

    public void setCurvaPadraoDataSet(List<Float> curvaPadraoDataSet) {
        this.curvaPadraoDataSet = curvaPadraoDataSet;
    }

}
