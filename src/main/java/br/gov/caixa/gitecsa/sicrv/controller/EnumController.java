package br.gov.caixa.gitecsa.sicrv.controller;

import java.io.Serializable;

import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.sicrv.enumerator.PeriodicidadeEnum;

@Named
@ViewScoped
public class EnumController extends BaseController implements Serializable {
	private static final long serialVersionUID = 8580489832830749495L;
	
	public PeriodicidadeEnum[] getPeriodicidadeList() {
		return PeriodicidadeEnum.values();
	}
	
}
