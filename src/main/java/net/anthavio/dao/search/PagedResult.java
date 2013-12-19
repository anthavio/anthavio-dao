package net.anthavio.dao.search;

import java.util.List;

import net.anthavio.log.ToStringBuilder;

import org.apache.commons.lang.builder.ToStringStyle;


/**
 * @author vanek
 *
 */
public class PagedResult<T> {

	private final List<T> results;

	private final int millis;

	private final Integer limit;

	private final Integer offset;

	private final Long total;

	public PagedResult(List<T> results, int millis) {
		this(results, millis, null);
	}

	public PagedResult(List<T> results, int millis, Long total) {
		this(results, millis, null, null, total);
	}

	public PagedResult(List<T> results, int millis, Integer limit, Integer offset) {
		this(results, millis, limit, offset, null);
	}

	public PagedResult(List<T> results, int millis, Integer limit, Integer offset, Long total) {
		this.results = results;
		this.millis = millis;
		this.limit = limit;
		this.offset = offset;
		this.total = total;
	}

	public List<T> getResults() {
		return results;
	}

	public Integer getLimit() {
		return limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public Long getTotal() {
		return total;
	}

	public int getMillis() {
		return millis;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
