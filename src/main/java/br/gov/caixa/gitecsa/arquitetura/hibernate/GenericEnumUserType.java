package br.gov.caixa.gitecsa.arquitetura.hibernate;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.TypeResolver;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

public class GenericEnumUserType implements UserType, ParameterizedType {

	private static final String DEFAULT_GET_VALUE_METHOD_NAME = "getValor";
	private static final String DEFAULT_VALUE_OF_METHOD_NAME = "valueOf";

	protected Class<?> enumClass;
	private Class<?> valueType;
	private Method getValueMethod;
	private Method valueOfMethod;
	private AbstractSingleColumnStandardBasicType<Object> type;
	private int[] sqlTypes;

	@SuppressWarnings("unchecked")
	@Override
	public void setParameterValues(Properties properties) {

		getEnumClassName(properties);

		String getValueMethodName = properties.getProperty("getValueMethod", DEFAULT_GET_VALUE_METHOD_NAME);
		try {
			getValueMethod = enumClass.getMethod(getValueMethodName, new Class[0]);
			valueType = getValueMethod.getReturnType();
		} catch (Exception e) {
			throw new HibernateException("Failed to obtain identifier method", e);
		}

		TypeResolver tr = new TypeResolver();
		type = (AbstractSingleColumnStandardBasicType<Object>) tr.basic(valueType.getName());
		if (type == null)
			throw new HibernateException("Unsupported identifier type " + valueType.getName());

		sqlTypes = new int[] { type.sqlType() };

		String valueOfMethodName = properties.getProperty("valueOfMethod", DEFAULT_VALUE_OF_METHOD_NAME);
		try {
			valueOfMethod = enumClass.getMethod(valueOfMethodName, new Class[] { valueType });
		} catch (Exception e) {
			throw new HibernateException("Failed to obtain valueOf method", e);
		}
	}

	protected void getEnumClassName(Properties properties) {
		String enumClassName = properties.getProperty("enumClass");
		try {
			enumClass = Class.forName(enumClassName).asSubclass(Enum.class);
		} catch (ClassNotFoundException cnfe) {
			throw new HibernateException("Enum class not found", cnfe);
		}
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return x == y;
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor owner, Object arg3) throws HibernateException, SQLException {

		Object identifier = type.get(rs, names[0], owner);
		if (identifier == null) {
			return null;
		}

		try {
			return valueOfMethod.invoke(enumClass, new Object[] { identifier });
		} catch (Exception e) {
			throw new HibernateException("Exception while invoking valueOf method '" + valueOfMethod.getName() + "' of enumeration class '"
					+ enumClass + "'", e);
		}
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor owner) throws HibernateException, SQLException {
		try {
			if (value == null) {
				st.setNull(index, type.sqlType());
			} else {
				Object identifier = null;
				if (value.getClass().equals(enumClass)) {
					identifier = getValueMethod.invoke(value, new Object[0]);
				} else {
					identifier = getValueMethod.invoke(valueOfMethod.invoke(enumClass.getEnumConstants()[0], Integer.parseInt(String.valueOf(value))), new Object[0]);
				}
				type.set(st, identifier, index, owner);
			}
		} catch (Exception e) {
			throw new HibernateException("Exception while invoking identifierMethod '" + getValueMethod.getName() + "' of enumeration class '"
					+ enumClass + "'", e);
		}
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

	@Override
	public Class<?> returnedClass() {
		return enumClass;
	}

	@Override
	public int[] sqlTypes() {
		return sqlTypes;
	}
	
}
