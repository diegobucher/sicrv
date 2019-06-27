package br.gov.caixa.gitecsa.sicrv.model.dto;

import java.util.List;

import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.Equipe;
import br.gov.caixa.gitecsa.sicrv.model.EquipeEmpregado;

public class EquipeDTO {
	
	private Equipe equipe;
	private List<Atividade> listAtividadeSelecionadas;
	private List<EquipeEmpregado> listEquipeEmpregado;
	
	public Equipe getEquipe() {
		return equipe;
	}
	public void setEquipe(Equipe equipe) {
		this.equipe = equipe;
	}
	public List<Atividade> getListAtividadeSelecionadas() {
		return listAtividadeSelecionadas;
	}
	public void setListAtividadeSelecionadas(
			List<Atividade> listAtividadeSelecionadas) {
		this.listAtividadeSelecionadas = listAtividadeSelecionadas;
	}
	public List<EquipeEmpregado> getListEquipeEmpregado() {
		return listEquipeEmpregado;
	}
	public void setListEquipeEmpregado(List<EquipeEmpregado> listEquipeEmpregado) {
		this.listEquipeEmpregado = listEquipeEmpregado;
	}
}
