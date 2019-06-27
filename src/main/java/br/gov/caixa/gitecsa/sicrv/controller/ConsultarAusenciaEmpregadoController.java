package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.controller.ContextoController;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.sicrv.model.Ausencia;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.dto.AusenciaDTO;
import br.gov.caixa.gitecsa.sicrv.security.ControleAcesso;
import br.gov.caixa.gitecsa.sicrv.service.AusenciaEmpregadoService;
import br.gov.caixa.gitecsa.sicrv.service.EmpregadoService;

@Named
@ViewScoped
public class ConsultarAusenciaEmpregadoController extends BaseController implements Serializable {

    private static final long serialVersionUID = 4940610294969962830L;
    private static final String TELA_ORIGEM = "CONSULTA_AUSENCIA_EMPREGADO";

    @Inject
    private ContextoController contextoController;

    @Inject
    private AusenciaEmpregadoService ausenciaEmpregadoService;

//    @Inject
//    private EscalaService escalaService;

    // ------------------------- Inicio do modal de empregado
    @Inject
    private ControleAcesso controleAcesso;
    @Inject
    private EmpregadoService empregadoService;
    private List<Empregado> listEmpregado;
    private Empregado empregadoSelecionado;
    // ----------------------Final modal empregado

    private Long idAusenciaEmpregado;
    private Boolean pesquisaAtiva;

    private String filtro;
    private Empregado empregado;
    private Ausencia ausencia;

    private List<Ausencia> listaAusenciaEmpregados;
    private List<Ausencia> listFiltroAusenciaEmpregados;

    @PostConstruct
    public void init() {
        limpar();
        if (TELA_ORIGEM.equals(contextoController.getTelaOrigem())) {
            if (!Util.isNullOuVazio(contextoController.getCrudMessage())) {
                facesMessager.addMessageInfo(contextoController.getCrudMessage());
            }
            if (!Util.isNullOuVazio(contextoController.getObjectFilter())) {
                AusenciaDTO ausenciaDTO = (AusenciaDTO) contextoController.getObjectFilter();
                this.empregado = ausenciaDTO.getEmpregado();
                this.ausencia = ausenciaDTO.getAusencia();
                consultar();
            }
        }
        contextoController.limpar();
        listEmpregado = new ArrayList<Empregado>();
        listEmpregado.addAll(empregadoService.obterEmpregadosPorUnidade(controleAcesso.getUnidade()));
        setEmpregadoSelecionado(new Empregado());
    }

    public void consultar() {
        if (empregado != null && !Util.isNullOuVazio(empregado.getMatricula())) {
            obterEmpregado();
        }

        if (validarConsulta()) {
            listaAusenciaEmpregados = ausenciaEmpregadoService.consultar(empregado.getMatricula(), getAusencia());
            this.pesquisaAtiva = Boolean.TRUE;
        }
    }

    public Boolean validarConsulta() {
        // Verifica se nenhum campo da consulta foi preenchido
        if (Util.isNullOuVazio(empregado.getMatricula()) && getAusencia().getDataInicio() == null
                && getAusencia().getDataFim() == null && getAusencia().getMotivo() == null) {
            facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.validation.prencha.campo.consultar"));
            return false;
        }

        if (getAusencia().getDataInicio() != null && getAusencia().getDataFim() != null
                && getAusencia().getDataInicio().after(getAusencia().getDataFim())) {
            facesMessager.addMessageError(MensagemUtil.obterMensagem("ausenciaEmpregado.mensagem.dataFimMenor"));
            return false;
        }

        return true;
    }

    public void obterEmpregado() {
        if (empregado != null && empregado.getMatricula() != null) {
            Empregado empregadoInformado =
                    ausenciaEmpregadoService.obterEmpregadoPorMatriculaUnidade(empregado.getMatricula(),
                            controleAcesso.getUnidade());
            if (empregadoInformado != null) {
                empregado = empregadoInformado;
            } else {
                facesMessager.addMessageError(MensagemUtil.obterMensagem("ausenciaEmpregado.mensagem.empregadoNaoEncontrado"));
                empregado.setNome("");
            }
        }
    }

    public void carregarEmpregado() {
        setEmpregado(getEmpregadoSelecionado());
    }

    public String editar() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("idAusenciaEmpregado", idAusenciaEmpregado);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("acao", ManterAusenciaEmpregadoController.EDITAR);
        filtro = this.empregado.getMatricula();
        contextoController.setTelaOrigem(TELA_ORIGEM);

        AusenciaDTO ausenciaDTO = new AusenciaDTO();
        ausenciaDTO.setEmpregado(this.empregado);
        ausenciaDTO.setAusencia(this.ausencia);
        contextoController.setObjectFilter(ausenciaDTO);

        return "/pages/ausenciaEmpregado/manter.xhtml?faces-redirect=true";
    }

    private void limpar() {
        this.pesquisaAtiva = Boolean.FALSE;
        this.listaAusenciaEmpregados = null;
        this.empregado = new Empregado();
        this.ausencia = new Ausencia();
    }

    public String redirectTelaManter() {
        contextoController.setTelaOrigem(TELA_ORIGEM);
        AusenciaDTO ausenciaDTO = new AusenciaDTO();
        ausenciaDTO.setEmpregado(this.empregado);
        ausenciaDTO.setAusencia(this.ausencia);
        contextoController.setObjectFilter(ausenciaDTO);
        return "/pages/ausenciaEmpregado/manter.xhtml?faces-redirect=true";
    }

    public String excluir() throws BusinessException, ParseException {
        Ausencia ausencia = new Ausencia();
        ausencia = ausenciaEmpregadoService.findById(idAusenciaEmpregado);
        if (validarDataExclusao(ausencia)) {
            ausenciaEmpregadoService.delete(ausencia);
            ausenciaEmpregadoService.verificarNecessidadeAdicionarEscala(ausencia.getDataInicio(), ausencia.getDataFim(), this.empregado);
            consultar();
            facesMessager.addMessageInfo(MensagemUtil.obterMensagem("ausenciaEmpregado.mensagem.empregadoExcluidoSucesso"));
        }
        return "";
    }

    private boolean validarDataExclusao(Ausencia ausencia) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date dataAtual = calendar.getTime();

        if (ausencia.getDataInicio().before(dataAtual)) {
            facesMessager.addMessageInfo(MensagemUtil.obterMensagem("ausenciaEmpregado.mensagem.dataExclusaoInvalida"));
            return false;
        }
        return true;
    }

    public int sortTextIgnoreCase(final Object msg1, final Object msg2) {
        return ((String) msg1).compareToIgnoreCase(((String) msg2));
    }

    public void prepareToResult() throws Exception {
    }

    /** Getters and Setters */
    public Long getIdAusenciaEmpregado() {
        return idAusenciaEmpregado;
    }

    public void setIdAusenciaEmpregado(Long idAusenciaEmpregado) {
        this.idAusenciaEmpregado = idAusenciaEmpregado;
    }

    public Empregado getEmpregado() {
        return empregado;
    }

    public void setEmpregado(Empregado empregado) {
        this.empregado = empregado;
    }

    public Ausencia getAusencia() {
        return ausencia;
    }

    public void setAusencia(Ausencia ausencia) {
        this.ausencia = ausencia;
    }

    public List<Ausencia> getListaAusenciaEmpregados() {
        return listaAusenciaEmpregados;
    }

    public void setListaAusenciaEmpregados(List<Ausencia> listaAusenciaEmpregados) {
        this.listaAusenciaEmpregados = listaAusenciaEmpregados;
    }

    public List<Ausencia> getListFiltroAusenciaEmpregados() {
        return listFiltroAusenciaEmpregados;
    }

    public void setListFiltroAusenciaEmpregados(List<Ausencia> listFiltroAusenciaEmpregados) {
        this.listFiltroAusenciaEmpregados = listFiltroAusenciaEmpregados;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public Boolean getPesquisaAtiva() {
        return pesquisaAtiva;
    }

    public void setPesquisaAtiva(Boolean pesquisaAtiva) {
        this.pesquisaAtiva = pesquisaAtiva;
    }

    public List<Empregado> getListEmpregado() {
        return listEmpregado;
    }

    public void setListEmpregado(List<Empregado> listEmpregado) {
        this.listEmpregado = listEmpregado;
    }

    public Empregado getEmpregadoSelecionado() {
        return empregadoSelecionado;
    }

    public void setEmpregadoSelecionado(Empregado empregadoSelecionado) {
        this.empregadoSelecionado = empregadoSelecionado;
    }
}
