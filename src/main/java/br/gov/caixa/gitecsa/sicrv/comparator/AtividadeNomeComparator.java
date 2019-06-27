package br.gov.caixa.gitecsa.sicrv.comparator;

import java.util.Comparator;

import br.gov.caixa.gitecsa.sicrv.model.Atividade;

public class AtividadeNomeComparator implements Comparator<Atividade> {

  @Override
  public int compare(Atividade a1, Atividade a2) {
    return a1.getNome().compareTo(a2.getNome());
  }

}
