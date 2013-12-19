package net.anthavio.cdao.criteria;

public class VerifiedBetween extends VerifiedComparison{

	private Object value2;

	public VerifiedBetween () {
		this.setOperator(Operator.BETWEEN);
	}
	public VerifiedBetween(Class<?> aBaseType, String aName, Object aValue, Object aValue2) {
		super(aBaseType, aName, Operator.BETWEEN, aValue);
		this.value2 = aValue2;
	}
	
	public Object getValue2() {
		return value2;
	}

	public void setValue2(Object value2) {
		this.value2 = value2;
	}

	@Override
	public String toString() {
		return super.toString() + "_" + value2;
	}
	
}
