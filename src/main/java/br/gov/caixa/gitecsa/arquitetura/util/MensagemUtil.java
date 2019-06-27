package br.gov.caixa.gitecsa.arquitetura.util;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

 
public final class MensagemUtil {
	
	private static Properties mensagens;

	private MensagemUtil() {
	}

	private static Properties getMensagens() {
		inicializaProperties();
		return mensagens;
	}

	private static synchronized void inicializaProperties() {
		if (mensagens == null || mensagens.isEmpty()) {
			mensagens = new Properties();
			try {
				InputStream arquivoProperties = 
					Thread.currentThread().getContextClassLoader().getResourceAsStream("bundle.properties");
				
				mensagens.load(arquivoProperties);
			} catch (Exception e) { }
		}
	}
	
	public static String obterMensagem(String codigoMensagem, Object ... parametros) {
		String mensagem = getMensagens().getProperty(codigoMensagem);
		if (mensagem != null) {
			return MessageFormat.format(mensagem, transformaListaMensagem(parametros));
		}
		
		return codigoMensagem;
	}
	
	public static String obterMensagem(String codigoMensagem) {
		return obterMensagem(codigoMensagem, new Object[]{});
	}
	
	public static Object[] transformaListaMensagem(Object[] parametros){
		if (!Util.isNullOuVazio(parametros)) {
			Object[] novoArray = new Object[parametros.length];
			for (int i = 0; i < parametros.length; i++) {
				Object codigo = parametros[i];
				String mensagem = getMensagens().getProperty((String)codigo);
				if (mensagem != null) {
					novoArray[i] = mensagem;
				} else { 
					novoArray[i] = (String)codigo;
				}
			}
			return novoArray;
		}
		return parametros;
	}
	
}
