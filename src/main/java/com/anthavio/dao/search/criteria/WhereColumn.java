package com.anthavio.dao.search.criteria;

import java.io.Serializable;

public class WhereColumn<D extends Serializable> implements Column<D> {

	private String name;

	@Override
	public String getName() {
		return name;
	}

}
