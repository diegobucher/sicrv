package br.gov.caixa.gitecsa.arquitetura.listener;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

import org.omnifaces.context.OmniPartialViewContext;


public class AjaxListener implements PhaseListener {
	private static final String ON_RENDER_AJAX = "onRenderAjax();";

	private static final String PARTIAL_AJAX = "partial/ajax";
	
	private static final String FACES_REQUEST = "Faces-Request";
	
	private static final String OMNI_PARTIAL_VIEW_CONTEXT = "org.omnifaces.context.OmniPartialViewContext";

	private static final long serialVersionUID = 6L;

	public void afterPhase(PhaseEvent pEvento) {

	}
	
	public void beforePhase(PhaseEvent pEvento) {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		boolean ajaxRequest = PARTIAL_AJAX.equals(request.getHeader(FACES_REQUEST));
		if(ajaxRequest){
			OmniPartialViewContext requestContext = (OmniPartialViewContext) FacesContext.getCurrentInstance().getAttributes().get(OMNI_PARTIAL_VIEW_CONTEXT);
			requestContext.addCallbackScript(ON_RENDER_AJAX);
		}
	}

	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}
}
