/**
 * 
 */
package net.anthavio.dao.search.criteria;

/**
 * @author vanek
 */
public enum Operator {

	EQ("=", Cardinality.ONE), NEQ("!=", Cardinality.ONE), //
	GT(">", Cardinality.ONE), LT("<", Cardinality.ONE), //
	GOE(">=", Cardinality.ONE), LOE("<=", Cardinality.ONE), //
	LIKE("LIKE", Cardinality.ONE), NOT_LIKE("NOT LIKE", Cardinality.ONE), //
	IN("IN", Cardinality.MULTI), NOT_IN("NOT IN", Cardinality.MULTI), // 
	BETWEEN("BETWEEN", Cardinality.MULTI), NOT_BETWEEN("NOT BETWEEN", Cardinality.MULTI), //
	IS_NULL("IS NULL", Cardinality.ZERO), IS_NOT_NULL("IS NOT NULL", Cardinality.ZERO);

	public enum Cardinality {
		ZERO, ONE, MULTI;
	}

	private final String ql;

	private final Cardinality caridinality;

	private Operator(String ql, Cardinality caridinality) {
		this.ql = ql;
		this.caridinality = caridinality;
	}

	public String getQl() {
		return ql;
	}

	public Cardinality getCaridinality() {
		return caridinality;
	}

}
