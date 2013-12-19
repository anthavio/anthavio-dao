package net.anthavio.cdao;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class QueryHint<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private T value;

	public static QueryHint<Boolean> queryHintTrue(String name) {
		return new QueryHint<Boolean>(name, Boolean.TRUE);
	}

	public static QueryHint<Boolean> queryHintFalse(String name) {
		return new QueryHint<Boolean>(name, Boolean.FALSE);
	}

	public static QueryHint<String> queryHintValue(String name, String value) {
		return new QueryHint<String>(name, value);
	}

	public static List<QueryHint<?>> queryHintList(QueryHint<?>... hints) {
		return Arrays.asList(hints);
	}

	public QueryHint() {

	}

	public QueryHint(String name, T value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
