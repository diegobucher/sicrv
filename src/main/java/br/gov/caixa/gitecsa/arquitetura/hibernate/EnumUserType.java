package br.gov.caixa.gitecsa.arquitetura.hibernate;

import java.util.Properties;

import org.hibernate.HibernateException;

public class EnumUserType extends GenericEnumUserType {

  public static final String CLASS_NAME = "br.gov.caixa.gitecsa.arquitetura.hibernate.EnumUserType";

  public static final String ENUM_CLASS_NAME_PARAM = "enumClass";

  private static final String DOMINIO_NAO_ENCONTRADO = "Domínio não encontrado";

  protected void getEnumClassName(Properties properties) {
    String enumClassName = properties.getProperty(ENUM_CLASS_NAME_PARAM);

    try {
      enumClass = Class.forName(enumClassName.toString()).asSubclass(Enum.class);
    } catch (ClassNotFoundException cnfe) {
      throw new HibernateException(DOMINIO_NAO_ENCONTRADO, cnfe);
    }
  }

}
