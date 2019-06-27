package br.gov.caixa.gitecsa.arquitetura.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import br.gov.caixa.gitecsa.arquitetura.exception.AppException;
import br.gov.caixa.gitecsa.arquitetura.repository.ConfigurationRepository;

public class Resources {

	public static final String LOG4J_PROPERTIES = "/log4j.properties";
	public static final String CONFIGURACAO_LOG = "configuracao.log.localizacao";
	public static final String PROPERTIES = "/configuracoes.properties";

	@Produces
	public Logger produceLog(InjectionPoint injectionPoint) {
		InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream(LOG4J_PROPERTIES);

		Properties p = new Properties();

		try {
			p.load(is);
		} catch (IOException e) {
			return null;
		}

		String caminhoArquivo =  p.getProperty(CONFIGURACAO_LOG);

		DOMConfigurator.configure(caminhoArquivo);

		return Logger.getLogger(injectionPoint.getMember().getDeclaringClass()
				.getName());
	}

	@Produces
	@ConfigurationRepository("")
	public Properties produceProperties(InjectionPoint ip) {
		Properties p = new Properties();

		String value = ip.getAnnotated()
				.getAnnotation(ConfigurationRepository.class).value();

		try {
			InputStream fis = this.getClass().getClassLoader().getResourceAsStream(value);

			p.load(fis);

			return p;
		} catch (IOException e) {
			new AppException(e.getMessage(), e);
		}

		return null;
	}
}