package net.anthavio.dao.search.criteria;

import java.io.Serializable;

import com.mysema.query.types.expr.ComparableExpression;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.expr.StringExpression;

/**
 * @author vanek
 *
 * @param <T> value and column type
 * 
 * Querydsl Expressions for String and Number do not share common generic hierarchy, 
 * we need to usafely downcast without generic type information, 
 * or use specialized subclasses 
 * @see {@link StringColumnCriteria}, @see {@link NumberColumnCriteria}, @see {@link CompareColumnCriteria}
 */
//@Deprecated
@SuppressWarnings("rawtypes")
public class ColumnCriteria<T extends Serializable> extends ValueAndOperator<T> {

	private static final long serialVersionUID = 1L;

	public enum Type {
		NUMBER, STRING, COMPAR, SIMPLE;
	}

	protected final Type type;

	protected final SimpleExpression column;

	protected ColumnCriteria(StringExpression column, T... value) {
		this(column, Operator.EQ, value);
	}

	protected ColumnCriteria(StringExpression column, Operator operator, T... value) {
		super(operator, value);
		this.column = column;
		this.type = Type.STRING;
	}

	protected ColumnCriteria(NumberExpression column, T... value) {
		this(column, Operator.EQ, value);
	}

	protected ColumnCriteria(NumberExpression column, Operator operator, T... value) {
		super(operator, value);
		checkTypes(column, value);
		this.column = column;
		this.type = Type.NUMBER;
	}

	protected ColumnCriteria(ComparableExpression column, T... value) {
		this(column, Operator.EQ, value);
	}

	protected ColumnCriteria(ComparableExpression column, Operator operator, T... value) {
		super(operator, value);
		checkTypes(column, value);
		this.column = column;
		this.type = Type.COMPAR;
	}

	public ColumnCriteria(SimpleExpression column, T... value) {
		this(column, Operator.EQ, value);
	}

	public ColumnCriteria(SimpleExpression column, Operator operator, T... value) {
		super(operator, value);
		checkTypes(column, value);
		this.column = column;
		this.type = Type.SIMPLE;
	}

	public SimpleExpression getColumn() {
		return column;
	}

	public Type getType() {
		return type;
	}

	/**
	 * Without help of generic type info, we need to check manualy...
	 */
	private void checkTypes(SimpleExpression column, T... values) {
		if (values != null) {
			for (T value : values) {
				if (value != null && !column.getType().isAssignableFrom(value.getClass())) {
					throw new IllegalArgumentException("Column " + column + " is " + column.getType()
							+ " but value is " + value.getClass());
				}
			}
		}
	}

	@Override
	public String toString() {
		return "ColumnCriteria [" + column + " " + operator + " " + values + " " + type + "]";
	}

}
