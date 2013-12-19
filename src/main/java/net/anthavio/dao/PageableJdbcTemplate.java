/**
 * 
 */
package net.anthavio.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author vanek
 * 
 * OracleDB only!
 * 
 */
public class PageableJdbcTemplate extends JdbcTemplate {

	private static final List<Map<String, Object>> EMPTY = new ArrayList<Map<String, Object>>(0);

	public PageableJdbcTemplate() {
		super();
	}

	public PageableJdbcTemplate(DataSource dataSource, boolean lazyInit) {
		super(dataSource, lazyInit);
	}

	public PageableJdbcTemplate(DataSource dataSource) {
		super(dataSource);
	}

	public Page<Map<String, Object>> queryForList(Pageable pageable, String sql, Object... args) {
		int min = (pageable.getPageNumber() * pageable.getPageSize()) + 1;
		int max = (pageable.getPageNumber() + 1) * pageable.getPageSize();

		int idxFrom = sql.indexOf("FROM");
		int idxUntil = sql.indexOf("ORDER BY");
		if (idxUntil == -1) {
			idxUntil = sql.length();
		}
		String sqlCount = "SELECT COUNT(*) " + sql.substring(idxFrom, idxUntil);

		int total = queryForInt(sqlCount, args);

		Page<Map<String, Object>> page;

		if (total != 0) {
			List<Map<String, Object>> list = queryForList("SELECT * FROM (SELECT x.*, ROWNUM rx FROM(" + sql
					+ ") x WHERE ROWNUM <= " + max + ") WHERE rx  >= " + min, args);
			page = new PageImpl<Map<String, Object>>(list, pageable, total);
		} else {
			page = new PageImpl<Map<String, Object>>(EMPTY, pageable, total);
		}
		return page;
	}

	public List<Map<String, Object>> query(final String sql, Integer min, Integer max, Object... args) {
		if (min == null && max == null) {
			throw new IllegalArgumentException("Both min and max is null");
		}
		StringBuilder sb = new StringBuilder();
		if (max != null && max > 1) {
			if (min != null && min > 1) {
				if (min > max) {
					throw new IllegalArgumentException("min > max");
				} else if (min == max) {
					sb.append("SELECT * FROM(");
					sb.append(sql);
					sb.append(") WHERE ROWNUM = " + max);
				} else {
					sb.append("SELECT * FROM (");
					sb.append("SELECT x.*, ROWNUM rx FROM(");
					sb.append(sql);
					sb.append(") x WHERE ROWNUM <= " + max);
					sb.append(") WHERE rx >= " + min);
				}
			} else { // min == null
				sb.append("SELECT * FROM (");
				sb.append(sql);
				sb.append(") WHERE ROWNUM <= " + max);
			}

		} else { // max == null
			if (min != null && min > 1) {
				sb.append("SELECT * FROM (");
				sb.append(sql);
				sb.append(") WHERE ROWNUM >= " + min);
			} else { // min == null && max == null
				sb.append(sql);
			}
		}

		List<Map<String, Object>> list = queryForList(sb.toString(), args);
		return list;
	}
}
