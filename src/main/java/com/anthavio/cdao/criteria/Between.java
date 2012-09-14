package com.anthavio.cdao.criteria;

public class Between extends Comparison{

	private Object value2;

	public Between () {
		this.setOperator(Operator.BETWEEN);
	}
	public Between(String aName, Object aValue, Object aValue2) {
		super(aName, Operator.BETWEEN, aValue);
		this.value2 = aValue2;
	}
	
	public Between(String aName, Object aValue, Object aValue2, boolean alias) {
		super(aName, Operator.BETWEEN, aValue, alias);
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
