package br.gov.caixa.gitecsa.sicrv.model.dto.folga;

public class EmpregadoFolgaDTO implements Comparable<EmpregadoFolgaDTO> {

  private String matricula;
  private String nome;
  private String saldo;
  private String projecao;
  
  public String getMatricula() {
    return matricula;
  }
  public void setMatricula(String matricula) {
    this.matricula = matricula;
  }
  public String getNome() {
    return nome;
  }
  public void setNome(String nome) {
    this.nome = nome;
  }
  public String getSaldo() {
    return saldo;
  }
  public void setSaldo(String saldo) {
    this.saldo = saldo;
  }
  public String getProjecao() {
    return projecao;
  }
  public void setProjecao(String projecao) {
    this.projecao = projecao;
  }
  
  @Override
  public int compareTo(EmpregadoFolgaDTO o) {
    return this.nome.compareTo(o.getNome());
  }
}