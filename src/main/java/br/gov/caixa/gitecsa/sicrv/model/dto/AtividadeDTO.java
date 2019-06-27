package br.gov.caixa.gitecsa.sicrv.model.dto;

import java.util.List;

public class AtividadeDTO implements Comparable<AtividadeDTO> {

  private Long id;
  private String nome;
  private String periodicidade;
  private Boolean temFilhos;
  private List<SubAtividadeDTO> listaSubAtividade;

  public String getPeriodicidade() {
    return periodicidade;
  }

  public void setPeriodicidade(String periodicidade) {
    this.periodicidade = periodicidade;
  }

  public List<SubAtividadeDTO> getListaSubAtividade() {
    return listaSubAtividade;
  }

  public void setListaSubAtividade(List<SubAtividadeDTO> listaSubAtividade) {
    this.listaSubAtividade = listaSubAtividade;
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  @Override
  public int compareTo(AtividadeDTO atividade) {
    return this.getNome().compareTo(atividade.getNome());
  }

  public Boolean getTemFilhos() {
    return temFilhos;
  }

  public void setTemFilhos(Boolean temFilhos) {
    this.temFilhos = temFilhos;
  }

}
