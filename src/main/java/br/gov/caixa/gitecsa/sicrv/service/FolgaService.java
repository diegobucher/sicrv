package br.gov.caixa.gitecsa.sicrv.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.sicrv.dao.FolgaDAO;
import br.gov.caixa.gitecsa.sicrv.enumerator.SituacaoFolgaEnum;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeEmpregado;
import br.gov.caixa.gitecsa.sicrv.model.Escala;
import br.gov.caixa.gitecsa.sicrv.model.Folga;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.BancoFolgaDTO;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.EmpregadoFolgaDTO;

@Stateless
public class FolgaService extends AbstractService<Folga> {

  private static final String SALDO = "SALDO";

  private static final long serialVersionUID = -6044223883960124337L;

  public static final Integer PARAM_QTD_MAX_FOLGA_ACUMULADA = 25;

  public static final Integer PARAM_QTD_MIN_VOLTA_ESCALAR = 20;

  @Inject
  private FolgaDAO folgaDAO;
  
  @Inject
  private EquipeService equipeService;

  @Inject
  private EmpregadoService empregadoService;

  @Inject
  private EscalaService escalaService;

  @Override
  protected BaseDAO<Folga> getDAO() {
    return folgaDAO;
  }

  public List<Folga> buscarFolgasNoPeriodo(Empregado empregado, Date dataInicio, Date dataFim,
      List<SituacaoFolgaEnum> situacaoList) {
    return folgaDAO.buscarFolgasNoPeriodo(empregado, dataInicio, dataFim, situacaoList);
  }

  public List<Date> buscarDatasFolgasAPartir(Equipe equipe, Date dataInicio, Date dataFim, List<SituacaoFolgaEnum> situacaoList) {
    return folgaDAO.buscarDatasFolgasAPartir(equipe, dataInicio, dataFim, situacaoList);
  }

  public List<Folga> buscarFolgasEquipeNoPeriodo(Equipe equipe, Date dataInicio, Date dataFim, List<SituacaoFolgaEnum> situacaoList) {
    return folgaDAO.buscarFolgasEquipeNoPeriodo(equipe, dataInicio, dataFim, situacaoList);
  }
  
  public List<Folga> buscarFolgasUnidadeNoPeriodo(Unidade unidade, Date dataInicio, Date dataFim, List<SituacaoFolgaEnum> situacaoList) {
    return folgaDAO.buscarFolgasUnidadeNoPeriodo(unidade, dataInicio, dataFim, situacaoList);
  }

  public Long calcularSaldoFolga(Empregado empregado, Date dataLimite) {
    return folgaDAO.calcularSaldoFolga(empregado, dataLimite);
  }

  public void saveList(List<Folga> folgaList) throws Exception {
    
    for (Folga folga : folgaList) {
      Escala escala = escalaService.obterEscalaPorEmpregadoNoDia(folga.getEmpregado(), folga.getData());
      if(escala != null){
        escalaService.remove(escala);
      }
      folgaDAO.save(folga);
    }
  }

  @Override
  public List<Folga> consultar(Folga entidade) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void validaCamposObrigatorios(Folga entity) {
    // TODO Auto-generated method stub

  }

  @Override
  protected void validaRegras(Folga entity) {
    // TODO Auto-generated method stub

  }

  @Override
  protected void validaRegrasExcluir(Folga entity) {
    // TODO Auto-generated method stub

  }

  /*  Banco de Folgas com DTO específico*/
  public List<BancoFolgaDTO> buscarListaBancoFolgas(Date dataProjecao, Equipe equipe, Empregado empregado, Integer cgcUnidadeUsuario) {
    List<BancoFolgaDTO> listaBancoFolga = new ArrayList<BancoFolgaDTO>();
    BancoFolgaDTO bancoFolga;
    if (empregado != null && empregado.getId() != null){
      bancoFolga = obterBancoFolgaPorEquipe(dataProjecao, equipe, empregado, cgcUnidadeUsuario);
      if (cgcUnidadeUsuario.equals(bancoFolga.getEquipe().getUnidade().getCgc())){
        listaBancoFolga.add(bancoFolga); 
      }
      return listaBancoFolga;
    }
    if (equipe != null && equipe.getId() != null){
      bancoFolga = obterBancoFolgaPorEquipe(dataProjecao, equipe, null, cgcUnidadeUsuario);
      if (bancoFolga != null){
        listaBancoFolga.add(bancoFolga);      
      }
    } else {
      List<Equipe> listaCompletaEquipe = equipeService.obterEquipesAtivasPorUnidade(cgcUnidadeUsuario);
      for (Equipe item : listaCompletaEquipe) {
        bancoFolga = obterBancoFolgaPorEquipe(dataProjecao, item, null, cgcUnidadeUsuario);
        if (bancoFolga != null){
          listaBancoFolga.add(bancoFolga);      
        }
      }
    }    
    return listaBancoFolga;
  }

  private BancoFolgaDTO obterBancoFolgaPorEquipe(Date dataProjecao, Equipe equipe, Empregado empregado, Integer cgcUnidadeUsuario) {
    BancoFolgaDTO folga = null;
    List<EmpregadoFolgaDTO> listaEmpregadoFolgaDTO;
    Equipe equipeFetch = equipeService.findByIdFetch(equipe.getId()); 
    if (empregado == null){
      if (equipeFetch != null && !equipeFetch.getEquipeEmpregados().isEmpty()){
        folga = new BancoFolgaDTO();
        listaEmpregadoFolgaDTO = new ArrayList<EmpregadoFolgaDTO>();
        for (EquipeEmpregado empregadoDaEquipe : equipeFetch.getEquipeEmpregados()) {
          listaEmpregadoFolgaDTO.add(obterEmpregadoFolgaParaListaBancoFolga(empregadoDaEquipe.getEmpregado(),dataProjecao));
        }
        folga.setEquipe(equipeFetch);
        Collections.sort(listaEmpregadoFolgaDTO);
        List<EmpregadoFolgaDTO> listaOrdenadaComCabecalho = new ArrayList<EmpregadoFolgaDTO>();
        listaOrdenadaComCabecalho.add(cabecalhoSubTableBancoFolga());
        listaOrdenadaComCabecalho.addAll(listaEmpregadoFolgaDTO);
        listaOrdenadaComCabecalho.add(rodapeSubTableBancoFolga(listaEmpregadoFolgaDTO));        
        folga.setListEmpregadoFolgaDTO(listaOrdenadaComCabecalho);
      }
    } else {
      folga = new BancoFolgaDTO();
      empregado = empregadoService.findByIdFetch(empregado.getId());
      equipeFetch = equipeService.findByIdFetch(empregado.getEquipeEmpregadoAtivo().getEquipe().getId());
      listaEmpregadoFolgaDTO = new ArrayList<EmpregadoFolgaDTO>();
      listaEmpregadoFolgaDTO.add(cabecalhoSubTableBancoFolga());    
      listaEmpregadoFolgaDTO.add(obterEmpregadoFolgaParaListaBancoFolga(empregado,dataProjecao));
      listaEmpregadoFolgaDTO.add(rodapeSubTableBancoFolga(listaEmpregadoFolgaDTO));        
      folga.setEquipe(equipeFetch);
      folga.setListEmpregadoFolgaDTO(listaEmpregadoFolgaDTO);      
    }
    return folga;
  }
  
  private EmpregadoFolgaDTO obterEmpregadoFolgaParaListaBancoFolga(Empregado empregado, Date dataProjecao){
    EmpregadoFolgaDTO empregadoFolga = new EmpregadoFolgaDTO();
    empregadoFolga.setMatricula(empregado.getMatricula().toUpperCase());
    empregadoFolga.setNome(empregado.getNome().toUpperCase());
    empregadoFolga.setSaldo(calcularSaldoFolga(empregado,new Date()).toString());
    empregadoFolga.setProjecao(calcularSaldoFolga(empregado,dataProjecao).toString());
    return empregadoFolga;
  }
  
  private EmpregadoFolgaDTO cabecalhoSubTableBancoFolga(){
    EmpregadoFolgaDTO empregadoFolga = new EmpregadoFolgaDTO();
    empregadoFolga.setMatricula("MATRÍCULA");
    empregadoFolga.setNome("NOME");
    empregadoFolga.setSaldo(SALDO);
    empregadoFolga.setProjecao("PROJEÇÃO");
    return empregadoFolga;
  }
  private EmpregadoFolgaDTO rodapeSubTableBancoFolga(List<EmpregadoFolgaDTO> listaEmpregadoFolgaDTO){
    Integer saldo = 0;
    Integer projecao = 0;
    for (EmpregadoFolgaDTO empregadoFolgaDTO : listaEmpregadoFolgaDTO) {
      if (!SALDO.equals(empregadoFolgaDTO.getSaldo())){
        saldo += Integer.parseInt(empregadoFolgaDTO.getSaldo());
        projecao += Integer.parseInt(empregadoFolgaDTO.getProjecao());
      }
    }
    EmpregadoFolgaDTO empregadoFolga = new EmpregadoFolgaDTO();
    empregadoFolga.setMatricula("TOTAL:");
    empregadoFolga.setNome("");
    empregadoFolga.setSaldo(saldo.toString());
    empregadoFolga.setProjecao(projecao.toString());
    return empregadoFolga;
  }
}