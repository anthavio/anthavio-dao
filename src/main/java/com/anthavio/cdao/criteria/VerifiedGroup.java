package com.anthavio.cdao.criteria;

public class VerifiedGroup extends Group {
	
	private Class<?> baseType;

	public VerifiedGroup () {
		
	}
	public VerifiedGroup(final Class<?> aBaseType, final Group group) {
		this.baseType = aBaseType;
		for(Criterion criterion : group){
			add(criterion);
		}
	}

	public Class<?> getBaseType() {
		return baseType;
	}

	public void setBaseType(final Class<?> aBaseType) {
		this.baseType = aBaseType;
	}
	
	public VerifiedGroup (final Class<?> type) {
		this.baseType = type;
	}

	public VerifiedGroup(Class<?> clazz, Junction and, Criterion[] criteria) {
		
		super(and, criteria);
		baseType = clazz;
	}
	@Override
	public String toString() {
		return "VG_" + super.toString();
	}


	@Override
	public Group add(Criterion criterion) {
		if (criterion instanceof Between) {
			Between between = (Between) criterion;
			super.add(new VerifiedBetween(baseType, between.getName(),
					between.getValue(), between.getValue2()));
		} else if (criterion instanceof Comparison) {
			Comparison comparison = (Comparison) criterion;
			super.add(new VerifiedComparison(baseType, comparison));
		} else if (criterion instanceof Group) {
			Group group = (Group) criterion;
			super.add(new VerifiedGroup (baseType, group));
		}
		return this;
	}

}
