package br.gov.caixa.gitecsa.arquitetura.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Classe que inicializa o contexto  
 * @author jsouzaa
 *
 */
public class MyServletContextListener implements ServletContextListener {

    public void contextDestroyed(ServletContextEvent sce) {

    }

    public void contextInitialized(ServletContextEvent sce) {
        System.getProperties().put("org.apache.el.parser.COERCE_TO_ZERO", "false");
    }
}
