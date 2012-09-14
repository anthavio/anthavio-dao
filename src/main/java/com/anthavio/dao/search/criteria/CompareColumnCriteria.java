package com.anthavio.dao.search.criteria;

import java.io.Serializable;

import com.mysema.query.types.expr.ComparableExpression;

/**
 * @author vanek
 *
 * @param <T> value and column type is Comparable<T> - java.util.Date, ...
 * 
 */
public class CompareColumnCriteria<T extends Serializable & Comparable<T>> extends
		ColumnCriteria<T> {

	private static final long serialVersionUID = 1L;

	public CompareColumnCriteria(ComparableExpression<T> column, T... value) {
		this(column, Operator.EQ, value);
	}

	public CompareColumnCriteria(ComparableExpression<T> column, Operator operator, T... value) {
		super(column, operator, value);
	}

	@Override
	public ComparableExpression<T> getColumn() {
		return (ComparableExpression<T>) column;
	}

	@Override
	public String toString() {
		return "CompareColumnCriteria [" + column + " " + operator + " " + values + "]";
	}

}
