package net.anthavio.dao.search.criteria;

import com.mysema.query.types.expr.NumberExpression;

/**
 * @author vanek
 *
 * @param <T> value and column type is Number and Comparable<?> - Integer, Long, Byte, Float, Double ...
 * 
 */
public class NumberColumnCriteria<T extends Number & Comparable<?>> extends ColumnCriteria<T> {

	private static final long serialVersionUID = 1L;

	public NumberColumnCriteria(NumberExpression<T> column, T... value) {
		this(column, Operator.EQ, value);
	}

	public NumberColumnCriteria(NumberExpression<T> column, Operator operator, T... value) {
		super(column, operator, value);
	}

	@Override
	public NumberExpression<T> getColumn() {
		return (NumberExpression<T>) super.getColumn();
	}

	@Override
	public String toString() {
		return "NumberColumnCriteria [" + getColumn() + " " + operator + " " + values + "]";
	}

}
