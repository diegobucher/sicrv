package br.gov.caixa.gitecsa.arquitetura.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ContextLocator {

	private static Context envCtx = null;

	private ContextLocator() {
		
	}

	public static Context getContext() throws NamingException {
		
		if (envCtx == null) {
			InitialContext contexto = new InitialContext();
			envCtx = (Context) contexto.lookup("java:comp/env");
		}

		return envCtx;
	}

}
