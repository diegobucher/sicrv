package br.gov.caixa.gitecsa.arquitetura.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.DateTimeConverter;
import javax.faces.convert.FacesConverter;
import javax.servlet.http.HttpSession;

import org.primefaces.component.calendar.Calendar;

import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.sicrv.util.Constantes;

@FacesConverter("converter.DataConverter")
public class DataConverter extends DateTimeConverter {

	public static final String MAP_DATA_INVALIDA = "MAP_DATA_INVALIDA";

	public DataConverter() {
		setPattern(Constantes.DATA_FORMATACAO);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		Map<String, Boolean> dataInvalida = getMapDataInvalidaSessao();

		Object retorno = null;

		try {
			//Caso a data não tenha os 10 digitos (XX/XX/XXXX)
			if (!Util.isNullOuVazio(value) && value.length() != 10){
				throw new ParseException("", 0);
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat(Constantes.DATA_FORMATACAO);
			dateFormat.setLenient(false);
			retorno = dateFormat.parse(value);
			preencherMapdataInvalida(component, dataInvalida, false);

		} catch (ParseException e) {
			
 			if(!value.equals(Constantes.DATA_FORMATACAO)){
				
				((Calendar) component).setValue(value);
				preencherMapdataInvalida(component, dataInvalida, true);
			}else{
				preencherMapdataInvalida(component, dataInvalida, false);	
			}
		}

		setMapDataInvalidaSessao(dataInvalida);

		return retorno;
	}

	public void preencherMapdataInvalida(UIComponent component,Map<String, Boolean> dataInvalida, Boolean pIsDataValida) {
		dataInvalida.put(component.getClientId().substring(component.getClientId().lastIndexOf(':') + 1), pIsDataValida);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Boolean> getMapDataInvalidaSessao() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		Map<String, Boolean> dataInvalida = (Map<String, Boolean>) session
				.getAttribute(DataConverter.MAP_DATA_INVALIDA);

		if (dataInvalida == null) {
			dataInvalida = new HashMap<String, Boolean>();
		}
		return dataInvalida;
	}

	public static void setMapDataInvalidaSessao(Map<String, Boolean> map) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);

		session.setAttribute(DataConverter.MAP_DATA_INVALIDA, map);
	}
}
