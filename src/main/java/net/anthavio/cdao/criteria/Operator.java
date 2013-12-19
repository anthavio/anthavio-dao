package net.anthavio.cdao.criteria;

import java.io.Serializable;

public enum Operator implements Serializable{
	EQ, NE, LE, GE, GT, LT, BETWEEN, IN, LIKE, LIKE_START, LIKE_END, LIKE_CONTAINS, IS_NULL, IS_NOT_NULL;
	private static String [] operators  = 
	{"=", "<>", "<=", ">=", ">", "<", "BETWEEN", "IN", "LIKE", "LIKE", "LIKE", "LIKE", "IS NULL", "IS NOT NULL"};
	
	public String getOperator () {
		return operators [this.ordinal()]	;
	}
}
