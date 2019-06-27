package br.gov.caixa.gitecsa.arquitetura.listener;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;

public class AuthorizationListener implements PhaseListener {

	private static final long serialVersionUID = 1L;
	
	@Override
	public void afterPhase(PhaseEvent arg0) {
		FacesContext facesContext = arg0.getFacesContext();
		String currentPage = facesContext.getViewRoot().getViewId();
		
		boolean isLoginPage = (currentPage.lastIndexOf("login.xhtml") > -1);
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);

		if (session == null ) {
			if (!isLoginPage) {
				NavigationHandler nh = facesContext.getApplication().getNavigationHandler();
				if (currentPage.lastIndexOf("login.xhtml") == -1) {
					JSFUtil.addErrorMessage(MensagemUtil.obterMensagem("geral.login.sessaoExpirada"));
				}
				nh.handleNavigation(facesContext, null, "loginPage");				
			}
			RequestContext.getCurrentInstance().execute("hideStatus();");
		} else {
			Object currentUser = session.getAttribute("loggedUser");		
			
			if (!isLoginPage && (currentUser == null || currentUser == "") && session.isNew()) {
				NavigationHandler nh = facesContext.getApplication().getNavigationHandler();
				if (currentPage.lastIndexOf("login.xhtml") == -1) {
					JSFUtil.addErrorMessage(MensagemUtil.obterMensagem("geral.login.sessaoExpirada"));
				}
				nh.handleNavigation(facesContext, null, "loginPage");
			} else if (!isLoginPage && (currentUser == null || currentUser == "")) {
				NavigationHandler nh = facesContext.getApplication().getNavigationHandler();
				nh.handleNavigation(facesContext, null, "loginPage");			
			}
		}
	}

	@Override
	public void beforePhase(PhaseEvent arg0) {
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

}
