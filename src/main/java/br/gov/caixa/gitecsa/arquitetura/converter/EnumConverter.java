package br.gov.caixa.gitecsa.arquitetura.converter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import br.gov.caixa.gitecsa.arquitetura.enumerator.EnumInterface;

@FacesConverter(value = EnumConverter.ENUM_CONVERTER)
public class EnumConverter implements Converter {

	public static final String ENUM_CONVERTER = "caixa.gitec.EnumConverter";
	
	private static final String ERRO_CONVERSAO_ENUM = "Não foi possivel converter Enum! Verifique se existe o atributo enumClass.";
	
	private static final String ENUM_CLASS = "enumClass";
	
	private static final String ERROR_NO_ENUM_TYPE = "Tipo informado '%s' não é um Enum.";
	
	private static final String ERROR_METHOD_VALUEOF = "Método valueOfDescricao não existe no Enum.";
	
	@SuppressWarnings("rawtypes")
        @Override
	public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
		if (modelValue == null) {
			return null;
		} else if (modelValue instanceof EnumInterface) {
			return ((EnumInterface) modelValue).getDescricao();
		} else {
			throw new ConverterException(String.format(ERROR_NO_ENUM_TYPE, modelValue.getClass()));
		}
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
		if (submittedValue == null) {
			return null;
		}

		try {
			String className = (String)component.getAttributes().get(ENUM_CLASS);
			
			Class<?> clazz = Class.forName(className);
			
			Method methods[] = clazz.getMethods();
			
			for (Method method : methods) {
				if (method.getName().equals(EnumInterface.METHOD_VALUEOF_DESCRICAO)){
					return method.invoke(clazz, submittedValue);
				}
			}
			
			throw new ConverterException(ERROR_METHOD_VALUEOF);
		} catch (IllegalArgumentException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
			throw new ConverterException(ERRO_CONVERSAO_ENUM);
		}
	}

}
