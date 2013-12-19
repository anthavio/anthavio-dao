package net.anthavio.cdao.criteria;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

public class VerifiedComparison extends Comparison {

	private Class<?> baseType;
	

	public VerifiedComparison () {
	}

	public VerifiedComparison (final Class<?> aBaseType, Comparison comparison) {
		super(comparison.getName(), comparison.getOperator(), comparison.getValue());
		this.baseType = aBaseType;
		verify();
		
	}
	

	public VerifiedComparison (final Class<?> baseType, final String aName, final Operator aOperator, final Object aValue) {
		super(aName, aOperator, aValue);
		this.baseType = baseType;
		verify();
	}
	
	void verify () {
		if (this.getName() == null || this.baseType == null) {
			return;
		} else {
			String[] props = this.getName().split("[.]");
			if (props.length == 1) {
				verifyProperty(baseType, this.getName());
			} else {
				verifyProperties(props);
			}
		}
	}

	private void verifyProperties(String [] properties) {
		Class<?> type = baseType;
		for (String propName : properties) {
			type = verifyProperty(type, propName);
		}		
	}


	private Class<?> verifyProperty(final Class<?> type, final String propertyName) {
		try {
			boolean found = false;
			BeanInfo beanInfo = Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for(PropertyDescriptor pd : propertyDescriptors) {
				if( propertyName.equals(pd.getName())){
					found = true;
					return pd.getPropertyType();
				}
			}
			if (!found) {
				throw new RuntimeException ("Bad property " + propertyName + " of class " + type + " baseType " + baseType);
			}
			return null;
		} catch (Exception ex) {
			throw new RuntimeException ("Bad property " + propertyName + " of class " + type + " baseType " + baseType, ex);
		}
		
	}


	public Class<?> getBaseType() {
		return baseType;
	}

	public void setBaseType(Class<?> baseType) {
		this.baseType = baseType;
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		verify();
	}
	
	public String toString () {
		return "V_" + super.toString();
	}

}
