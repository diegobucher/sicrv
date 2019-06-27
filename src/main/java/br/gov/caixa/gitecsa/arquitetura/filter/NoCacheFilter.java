package br.gov.caixa.gitecsa.arquitetura.filter;

import java.io.IOException;

import javax.faces.application.ResourceHandler;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoCacheFilter implements Filter {
	
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (!request.getRequestURI().startsWith(request.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) { 
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
            response.setHeader("Pragma", "no-cache"); 
            response.setDateHeader("Expires", 0); 
        }

        chain.doFilter(req, res);
    }

	public void destroy() {
		
	}

	public void init(FilterConfig arg0) throws ServletException {
	}
}