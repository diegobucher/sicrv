package br.gov.caixa.gitecsa.arquitetura.listener;

import java.util.Calendar;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import br.gov.caixa.gitecsa.arquitetura.util.Util;

/**
 * Classe que atualiza a hora do servidor
 * @author jsouzaa
 *
 */
public class TimeListener implements PhaseListener {

	private static final long serialVersionUID = 1L;

	/**
     * Metodo que verifica se o usuario logado e o mesmo do login
     * @param arg0 
     */
	public void afterPhase(PhaseEvent arg0) {
		/**TODO *///JsfUtil.setSessionMapValue("dataDiaExtenso", getDiaDaSemana());
	}
	
	/**
	 * Método obtém a data corrente com horas e minutos
	 */
	@SuppressWarnings("unused")
	private String getDiaDaSemana() {
		return Util.formatData(Calendar.getInstance().getTime(), "EEEE, dd/MM/yyyy HH:mm");
	}

	public void beforePhase(PhaseEvent arg0) {
	}

	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

}
