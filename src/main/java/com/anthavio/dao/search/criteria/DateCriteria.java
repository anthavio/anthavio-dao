package com.anthavio.dao.search.criteria;

import java.io.Serializable;
import java.util.Date;

import com.anthavio.NotSupportedException;

/**
 * Trida slouzici pri vyhledavani podle datumu
 * 
 * @author vanek
 */
public class DateCriteria implements Serializable {

	private static final long serialVersionUID = 1L;

	private DateSearchType type;

	private Date after;

	private Date before;

	private Date exact;

	public DateCriteria() {
		//default for web
	}

	public DateCriteria(Date after, Date before, DateSearchType type) {
		this.type = type;
		this.after = after;
		this.before = before;
	}

	public DateCriteria(Date exact) {
		this(exact, DateSearchType.EXACT);
	}

	public DateCriteria(Date date, DateSearchType type) {
		if (date == null) {
			throw new IllegalArgumentException("date is null");
		}
		if (type == null) {
			throw new IllegalArgumentException("type is null");
		}
		this.type = type;
		switch (type) {
		case EXACT:
			this.exact = date;
			break;
		case AFTER:
			this.after = date;
			break;
		case BEFORE:
			this.before = date;
			break;
		}
	}

	public DateCriteria(Date after, Date before) {
		this.after = after;
		this.before = before;
		this.type = calculateType(null, after, before);
	}

	public Date getAfter() {
		return after;
	}

	public void setAfter(Date after) {
		this.after = after;
	}

	public DateSearchType getType() {
		if (this.type == null) {
			this.type = calculateType(this.exact, this.after, this.before);
		}
		return this.type;
	}

	public void setType(DateSearchType type) {
		this.type = type;
	}

	public Date getBefore() {
		return before;
	}

	public void setBefore(Date before) {
		this.before = before;
	}

	public Date getExact() {
		return exact;
	}

	public void setExact(Date exact) {
		this.exact = exact;
	}

	private DateSearchType calculateType(Date exact, Date after, Date before) {
		if (exact != null) {
			return DateSearchType.EXACT;
		} else if (after != null) {
			if (before != null) {
				//both after and before != null
				return DateSearchType.INSIDE;
			} else {
				//only after != null
				return DateSearchType.AFTER;
			}
		} else { //after == null
			if (before != null) {
				//only before != null
				return DateSearchType.BEFORE;
			} else {
				throw new NotSupportedException("Both dates are null");
			}
		}
	}

	@Override
	public String toString() {
		return "DateCriteria [after=" + after + ", before=" + before + ", exact=" + exact + ", type=" + type + "]";
	}

}