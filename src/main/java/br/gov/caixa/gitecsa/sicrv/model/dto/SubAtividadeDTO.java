package br.gov.caixa.gitecsa.sicrv.model.dto;

public class SubAtividadeDTO implements Comparable<SubAtividadeDTO>{

  private Long id;
  private String nome;
  private Integer prioridade;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getPrioridade() {
    return prioridade;
  }

  public void setPrioridade(Integer prioridade) {
    this.prioridade = prioridade;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  @Override
  public int compareTo(SubAtividadeDTO subAtividadeDTO) {
    return this.getNome().compareTo(subAtividadeDTO.getNome());
  }


}
