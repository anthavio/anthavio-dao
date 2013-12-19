package net.anthavio.dao.search;

/**
 * @author vanek
 *
 */
public class PagedCriteria {

	private Integer offset;

	private Integer limit;

	public PagedCriteria() {
		//default
	}

	public PagedCriteria(Integer offset, Integer limit) {
		this.offset = offset;
		this.limit = limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	@Override
	public String toString() {
		return "PagedCriteria [offset=" + offset + ", limit=" + limit + "]";
	}

}
