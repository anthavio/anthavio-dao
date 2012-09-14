package com.anthavio.dao.search.criteria;

/**
 * 
 * @author vanek
 *
 */
public enum DateSearchType {

	/** exactly one day (00:00:00,000 - 23:59:59,999) */
	DAY,
	/** precisely **/
	EXACT,
	/** before from date */
	BEFORE,
	/** after from date */
	AFTER,
	/** between from and until date */
	INSIDE,
	/** not between from and until date*/
	OUTSIDE;
}