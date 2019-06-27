package br.gov.caixa.gitecsa.arquitetura.enumerator;

import java.io.Serializable;

public interface EnumInterface<T extends Serializable> {
	
	public final String METHOD_VALUEOF_DESCRICAO = "valueOfDescricao";
	
	/**
	 * Retorna o código da instância do domínio
	 * @return
	 */
	public abstract T getValor();
	
	
	/**
	 * Retorna a descrição longa da instância do domínio caso exista, se não existir, retorna a descrição 
	 * @return
	 */
	public abstract String getDescricao();

	
	
	/**
	 * Representação em String do valor e descrição da instância do domínio
	 * @return
	 */
	public abstract String toString();
		
}