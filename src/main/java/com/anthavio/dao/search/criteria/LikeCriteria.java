package com.anthavio.dao.search.criteria;


/**
 * 
 * @author vanek
 *
 */
public class LikeCriteria extends ValueAndOperator<String> {

	private static final long serialVersionUID = 1L;

	private LikeSearchType style = LikeSearchType.BOTH;

	private boolean upper = false;

	public LikeCriteria() {
		//default for web
	}

	public LikeCriteria(String... value) {
		super(Operator.LIKE, (String) null);
		this.values = clearBlanks(value); //strings "" are bad too
	}

	public LikeCriteria(LikeSearchType style, String... value) {
		super(Operator.LIKE, (String) null);
		this.style = style;
		this.values = clearBlanks(value); //strings "" are bad too
	}

	public LikeSearchType getStyle() {
		return style;
	}

	public void setStyle(LikeSearchType style) {
		this.style = style;
	}

	public boolean getUpper() {
		return upper;
	}

	public void setUpper(boolean upper) {
		this.upper = upper;
	}

	@Override
	public String toString() {
		return "LikeCriteria [style=" + style + ", upper=" + upper + ", value=" + values + "]";
	}

}
