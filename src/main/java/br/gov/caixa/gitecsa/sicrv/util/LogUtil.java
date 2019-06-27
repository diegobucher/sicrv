package br.gov.caixa.gitecsa.sicrv.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Calendar;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.Util;

public class LogUtil {

	private static String cabecalhoRodape = "\n************************************************************************************";
	private static String nomeFuncionalidade = "NOME DA FUNCIONALIDADE: ";
	private static String descricaoEvento = "DESCRIÇÃO DO EVENTO: ";
	private static String dataHora = "DATA HORA: ";
	private static String enderecoLogico = "ENDEREÇO LÓGICO: ";
	private static String usuario = "USUÁRIO: ";
	private static String descricaoErro = "DESCRIÇÃO: ";

	public static String getMensagemPadraoLog(String message, String nomeFuncionalidadeDescricao, String descricaoEventoDescricao) {

		return cabecalhoRodape + "\n " + nomeFuncionalidade + (nomeFuncionalidadeDescricao != null 
				? nomeFuncionalidadeDescricao : "") + "\n " + descricaoEvento + (descricaoEventoDescricao != null ? descricaoEventoDescricao : "") 
				+ "\n " + dataHora + Util.formatData(Calendar.getInstance().getTime(), "dd/MM/yyyy - HH:mm:ss") + "\n " 
				+ enderecoLogico + getRemoteAddr() + " \n " 
				+ usuario + (JSFUtil.getSessionMapValue("loggedMatricula") != null ? JSFUtil.getSessionMapValue("loggedMatricula") : "") + "\n " 
				+ descricaoErro + (message != null ? message : "") + cabecalhoRodape;
	}
	
	public static String getMensagemPadraoLog(String message, String nomeFuncionalidadeDescricao, String descricaoEventoDescricao, String login) {

		return cabecalhoRodape + "\n " + nomeFuncionalidade + (nomeFuncionalidadeDescricao != null 
				? nomeFuncionalidadeDescricao : "") + "\n " + descricaoEvento + (descricaoEventoDescricao != null ? descricaoEventoDescricao : "") 
				+ "\n " + dataHora + Util.formatData(Calendar.getInstance().getTime(), "dd/MM/yyyy - HH:mm:ss") + "\n " 
				+ enderecoLogico + getRemoteAddr() + " \n " 
				+ usuario + (login != null ? login : "") + "\n " + descricaoErro + (message != null ? message : "") + cabecalhoRodape;
	}
	
	public static String getDescricaoPadraoPilhaExcecaoLog(Exception e) {
		String mensagemException = "";
		String stackTrace = "";
		String mensagemFinal = "";
		try {
			mensagemException = e.getMessage() != null ? "CLASSE: " + e.getClass().toString() + " MENSAGEM: " + e.getMessage().trim() + "\n"  : "CLASSE: " + e.getClass().toString() + "\n";
			stackTrace = e.getStackTrace().toString() != null ? "PILHA: " + Arrays.toString(e.getStackTrace()).replaceAll(", ", "\n") : "";
		} catch (Exception e2) {
			
		}
		mensagemFinal = mensagemException + stackTrace + "\n";
		Throwable anterior = e;
		Throwable cause = e.getCause();
		while ( cause != null && (!cause.equals(anterior)) ) {
			try {
				mensagemException = mensagemException + cause.getMessage() != null ? "CLASSE: " + cause.getClass().toString() + " MENSAGEM: " + cause.getMessage().trim() + "\n"  : "CLASSE: " + cause.getClass().toString() + "\n";
				stackTrace = cause.getStackTrace().toString() != null ? "PILHA: " + Arrays.toString(cause.getStackTrace()).replaceAll(", ", "\n") : "";
			} catch (Exception e2) {

			}
			mensagemFinal = mensagemFinal + mensagemException + stackTrace + "\n";
			anterior = cause;
			cause = cause.getCause();
		}
		
		return mensagemFinal;
	}
	
	public static String getDescricaoPadraoPilhaExcecaoLog(Throwable e) {
		String mensagemException = "";
		String stackTrace = "";
		String mensagemFinal = "";
		try {
			mensagemException = e.getMessage() != null ? "CLASSE: " + e.getClass().toString() + " MENSAGEM: " + e.getMessage().trim() + "\n"  : "CLASSE: " + e.getClass().toString() + "\n";
			stackTrace = e.getStackTrace().toString() != null ? "PILHA: " + Arrays.toString(e.getStackTrace()).replaceAll(", ", "\n") : "";
		} catch (Exception e2) {
			
		}
		mensagemFinal = mensagemException + stackTrace + "\n";
		Throwable anterior = e;
		Throwable cause = e.getCause();
		while ( cause != null && (!cause.equals(anterior)) ) {
			try {
				mensagemException = mensagemException + cause.getMessage() != null ? "CLASSE: " + cause.getClass().toString() + " MENSAGEM: " + cause.getMessage().trim() + "\n"  : "CLASSE: " + cause.getClass().toString() + "\n";
				stackTrace = cause.getStackTrace().toString() != null ? "PILHA: " + Arrays.toString(cause.getStackTrace()).replaceAll(", ", "\n") : "";
			} catch (Exception e2) {

			}
			mensagemFinal = mensagemFinal + mensagemException + stackTrace + "\n";
			anterior = cause;
			cause = cause.getCause();
		}
		
		return mensagemFinal;
	}


	public static String getMensagemPadraoLogManutencao(String nomeFuncionalidadeDescricao, String descricaoEventoDescricao) {

		return cabecalhoRodape + "\n " + nomeFuncionalidade + (nomeFuncionalidadeDescricao != null ? nomeFuncionalidadeDescricao : "") + 
				"\n " + descricaoEvento + (descricaoEventoDescricao != null ? descricaoEventoDescricao : "") + "\n " + dataHora +
				Util.formatData(Calendar.getInstance().getTime(), "dd/MM/yyyy - HH:mm:ss") + "\n " + enderecoLogico + getRemoteAddr() + 
				" \n " + usuario + (JSFUtil.getSessionMapValue("loggedMatricula") != null ? JSFUtil.getSessionMapValue("loggedMatricula") : "") + "\n " + cabecalhoRodape;
	}

	public static String getNomeFuncionalidade(String nomeFuncionalidade) {
		return nomeFuncionalidade.replace("Service", "");

	}

	public static String getDescricaoOperacao(String nomeFuncionalidade) {
		return (
					( nomeFuncionalidade.startsWith("save")||  nomeFuncionalidade.startsWith("salvar"))? "Inserir" 
							: (nomeFuncionalidade.equalsIgnoreCase("delete") ? "Excluir" 
									: (nomeFuncionalidade.equalsIgnoreCase("update") ? "Atualizar" 
											: (nomeFuncionalidade.equalsIgnoreCase("saveOrUpdate") ? "Inserir/Atualizar" : nomeFuncionalidade))));

	}

	public static String getRemoteAddr() {

		String hostname = null;

		try {
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

			InetAddress add = Inet4Address.getByName(request.getRemoteHost());

			hostname = add.getHostName();

		} catch (UnknownHostException e) {
			return "Host não Obtido";
		} catch (Exception e) {
			return "Host não Obtido";
		}

		if (hostname != null && hostname.length() > 11) {
			hostname = hostname.substring(0, 11);
		}

		return hostname;

	}

}
