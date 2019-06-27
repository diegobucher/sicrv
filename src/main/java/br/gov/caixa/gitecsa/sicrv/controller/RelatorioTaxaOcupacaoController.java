package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.CurvaPadrao;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeAtividade;
import br.gov.caixa.gitecsa.sicrv.model.dto.TaxaOcupacaoSemanaDTO;
import br.gov.caixa.gitecsa.sicrv.security.ControleAcesso;
import br.gov.caixa.gitecsa.sicrv.service.AtividadeService;
import br.gov.caixa.gitecsa.sicrv.service.CurvaPadraoService;
import br.gov.caixa.gitecsa.sicrv.service.EquipeService;
import br.gov.caixa.gitecsa.sicrv.service.RelatorioTaxaOcupacaoService;

@Named
@ViewScoped
public class RelatorioTaxaOcupacaoController extends BaseController implements Serializable {

    private static final long serialVersionUID = -1538115415925037677L;

    @Inject
    private RelatorioTaxaOcupacaoService relatorioTaxaOcupacaoService;

    @Inject
    private EquipeService equipeService;

    @Inject
    private ControleAcesso controleAcesso;

    @Inject
    private AtividadeService atividadeService;
    
    @Inject
    private CurvaPadraoService curvaPadraoService;

    private List<TaxaOcupacaoSemanaDTO> taxaOcupacaoSemanaDTOList;

    private Date dataInicio;
    private Date dataFim;
    private Equipe equipe;

    private List<Equipe> listEquipe;

    private List<Atividade> listAtividade;
    private Atividade atividade;

    @PostConstruct
    public void init() {
        equipe = new Equipe();
        atividade = new Atividade();
        listEquipe = equipeService.obterEquipesAtivasPorUnidade(controleAcesso.getUnidade().getCgc());
    }

    public void pesquisar() {
        listAtividade = new ArrayList<Atividade>();

        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
        if (validarCampos()) {
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
            buscarDados();
        }

        atividade = new Atividade();
        listEquipe = equipeService.obterEquipesAtivasPorUnidade(controleAcesso.getUnidade().getCgc());
    }

    public Boolean validarCampos() {
        // Verifica se nenhum campo da consulta foi preenchido
        if (dataInicio == null) {
            facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required", "Data Inicio"));
            return false;
        }
        if (dataFim == null) {
            facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required", "Data Fim"));
            return false;
        }

        if (equipe == null || equipe.getId() == null) {
            facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.required", "Equipe"));
            return false;
        }

        if (dataInicio != null && dataFim != null && dataInicio.after(dataFim)) {
            facesMessager.addMessageError(MensagemUtil.obterMensagem("relatorio.taxaOcupacao.mensagem.dataFimMenor"));
            return false;
        }

        if (!isCurvaPadraoValida()) {
            facesMessager.addMessageError("Existe Atividade sem Curva Padrão cadastrada");
        }

        return true;
    }
    
    private Boolean isCurvaPadraoValida(){
        boolean curvaPadraoValida = true;
        
        this.equipe = equipeService.findByIdFetch(this.equipe.getId());

        for (EquipeAtividade equipeAtividade : equipe.getEquipeAtividades()) {

            List<Atividade> atividadeList = new ArrayList<Atividade>();

            Atividade atividadeFetch = atividadeService.findByIdFetchAll(equipeAtividade.getAtividade().getId());
            // Se a atividade tem filhos
            if (atividadeFetch.getAtividadeList() != null
                    && !atividadeFetch.getAtividadeList().isEmpty()) {
                atividadeList.addAll(atividadeFetch.getAtividadeList());
            } else {
                atividadeList.add(atividadeFetch);
            }

            for (Atividade atividade : atividadeList) {
                if (atividade.getAtivo()) {
                    // Atividade atividade = equipeAtividade.getAtividade();
                    Collection<CurvaPadrao> curvaPadraoList = curvaPadraoService.buscarPorAtividadeSubAtividade(atividade);

                    if (curvaPadraoList == null || curvaPadraoList.isEmpty()) {
                        curvaPadraoValida = false;
                    }
                }
            }
        }
        return curvaPadraoValida;
    }

    public void buscarDados() {
        this.listAtividade = relatorioTaxaOcupacaoService.findEquipeAtividadeByEquipe(equipe);
    }

    public void detalharAtividade() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String nome = params.get("nomeAtividade");

        for (Atividade atividade : listAtividade) {
            if (nome.trim().equals(atividade.getNome().trim())) {
                this.atividade = atividade;
            }
        }
        taxaOcupacaoSemanaDTOList =
                relatorioTaxaOcupacaoService.buscarTaxaOcupacaoSemanal(dataInicio, dataFim, equipe.getId(), atividade.getId());
    }

    public String getNomeAtividade() {

        if (listAtividade != null) {
            for (Atividade atividade : listAtividade) {
                if (atividade.getId().equals(this.atividade.getId())) {
                    return atividade.getNome();
                }
            }
        }
        return null;
    }

    public List<TaxaOcupacaoSemanaDTO> getTaxaOcupacaoSemanaDTOList() {
        return taxaOcupacaoSemanaDTOList;
    }

    public void setTaxaOcupacaoSemanaDTOList(List<TaxaOcupacaoSemanaDTO> taxaOcupacaoSemanaDTOList) {
        this.taxaOcupacaoSemanaDTOList = taxaOcupacaoSemanaDTOList;
    }

    public TaxaOcupacaoSemanaDTO getSemana() {
        return taxaOcupacaoSemanaDTOList.iterator().next();
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

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public List<Equipe> getListEquipe() {
        return listEquipe;
    }

    public void setListEquipe(List<Equipe> listEquipe) {
        this.listEquipe = listEquipe;
    }

    public List<Atividade> getListAtividade() {
        return listAtividade;
    }

    public void setListAtividade(List<Atividade> listAtividade) {
        this.listAtividade = listAtividade;
    }

    public Atividade getAtividade() {
        return atividade;
    }

    public void setAtividade(Atividade atividade) {
        this.atividade = atividade;
    }
}
