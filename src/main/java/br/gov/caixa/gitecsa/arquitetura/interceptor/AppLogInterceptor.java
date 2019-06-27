package br.gov.caixa.gitecsa.arquitetura.interceptor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import javax.interceptor.InvocationContext;

import org.apache.log4j.Logger;

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;

public class AppLogInterceptor {

	@Inject
	private transient Logger logger = Logger.getLogger(AppLogInterceptor.class);

	@AroundInvoke
	public Object intercept(InvocationContext context) throws Exception {
		context.getParameters();
		
		Object returnObject = null;

		try {
			returnObject = context.proceed();
		} catch (BusinessException e) {
			throw e;
		} catch (RequiredException e) {
			throw e;
		} catch (Exception e) {
			logger.error("ERRO");
			
			List<String> error = new ArrayList<String>();
			
			error.add(MensagemUtil.obterMensagem("geral.message.erro.desconhecido"));
			
			throw new BusinessException(error);
		}

		String nomeFuncao = context.getMethod().getName();
		
		if (nomeFuncao.startsWith("save") || nomeFuncao.startsWith("salvar") || nomeFuncao.startsWith("update") || nomeFuncao.startsWith("delete") || nomeFuncao.startsWith("saveOrUpdate")) {
			logger.info("");
		}

		return returnObject;
	}
	
	@AroundTimeout
	public Object interceptTimeout(InvocationContext context) throws Exception {
		context.getParameters();
		Object returnObject = null;

		try {
			returnObject = context.proceed();
		} catch (BusinessException e) {
			throw e;
		} catch (RequiredException e) {
			throw e;
		} catch (RuntimeException re) {
			logger.error("ERRO");
		} catch (Exception e) {
			logger.error("ERRO");
			
			List<String> error = new ArrayList<String>();
			
			error.add(MensagemUtil.obterMensagem("geral.message.erro.desconhecido"));
			
			throw new BusinessException(error);
		}
		
		return returnObject;
	}

}
