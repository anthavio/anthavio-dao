package com.anthavio.dao.search.criteria;

import com.mysema.query.types.expr.StringExpression;

/**
 * @author vanek
 *
 * @param <T> value and column type is String
 * 
 */
public class StringColumnCriteria extends ColumnCriteria<String> {

	private static final long serialVersionUID = 1L;

	public StringColumnCriteria(StringExpression column, String... value) {
		this(column, Operator.EQ, value);
	}

	public StringColumnCriteria(StringExpression column, Operator operator, String... value) {
		super(column, operator, value);
	}

	@Override
	public StringExpression getColumn() {
		return (StringExpression) super.getColumn();
	}

	@Override
	public String toString() {
		return "StringColumnCriteria [" + getColumn() + " " + operator + " " + values + "]";
	}
}
