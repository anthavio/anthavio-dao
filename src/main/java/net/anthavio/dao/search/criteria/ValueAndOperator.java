package net.anthavio.dao.search.criteria;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * @author vanek
 *
 * @param <T> type of value
 * 
 * Web input oriented class
 *  & Comparable<?>
 *  
 * Operators {@link Operator#EQ} and {@link Operator#IN} or {@link Operator#NEQ} and {@link Operator#NIN} 
 * are merely interchangeable and evaluated by actual number of values
 */
public class ValueAndOperator<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;

	protected List<T> values;

	protected Operator operator = Operator.EQ;

	public ValueAndOperator() {
		//default for web
	}

	public ValueAndOperator(T... values) {
		this(Operator.EQ, values);
	}

	public ValueAndOperator(Operator operator, T... values) {
		this.operator = operator;
		if (values != null && values.length != 0) {
			setValues(Arrays.asList(values));
		}
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public T getValue() {
		if (this.values == null) {
			return null;
		} else if (this.values.size() == 0) {
			return null;
		} else {
			return this.values.get(0);
		}
	}

	public void setValue(T value) {
		if (this.values == null) {
			this.values = new ArrayList<T>(0);
		} else {
			this.values.clear();
		}
		this.values.add(value);
	}

	public List<T> getValues() {
		return values;
	}

	public void setValues(List<T> values) {
		if (values != null && values.size() > 0) {
			this.values = new ArrayList<T>();
			for (T t : values) {
				if (t != null) {
					this.values.add(t);
				}
			}
		}
		if (this.values != null && this.values.size() == 0) {
			this.values = null;
		}
	}

	public int valuesCount() {
		if (this.values == null) {
			return 0;
		} else {
			return this.values.size();
		}
	}

	public static List<String> clearBlanks(String[] array) {
		List<String> list = new ArrayList<String>();
		for (String s : array) {
			if (s != null && StringUtils.isNotBlank(s)) {
				list.add(s);
			}
		}
		return list;
	}

	public static <T> List<T> clearNulls(T[] array) {
		List<T> list = new ArrayList<T>();
		for (T t : array) {
			if (t != null) {
				list.add(t);
			}
		}
		return list;
	}

	@Override
	public String toString() {
		return "CriteriaValue [operator=" + operator + ", values=" + values + "]";
	}

}
