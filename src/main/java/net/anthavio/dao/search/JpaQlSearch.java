package net.anthavio.dao.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.anthavio.NotSupportedException;
import net.anthavio.dao.search.criteria.DateCriteria;
import net.anthavio.dao.search.criteria.DateSearchType;
import net.anthavio.dao.search.criteria.LikeCriteria;
import net.anthavio.dao.search.criteria.Operator;
import net.anthavio.util.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author vanek
 */
public class JpaQlSearch {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private String from;

	private boolean distinct = false;

	private final List<String> innerJoins = new ArrayList<String>();

	private final List<String> leftJoins = new ArrayList<String>();

	private final List<Object> parameters = new ArrayList<Object>();

	private final List<String> criterias = new ArrayList<String>();

	private final List<String> orderBys = new ArrayList<String>();

	/*
	public JpaQlSearch merge(JpaQlSearch from) {
		innerJoins.addAll(from.innerJoins);
		leftJoins.addAll(from.leftJoins);
		//XXX to bude slozitejsi pac je treba prepocitat cisla u parametru v kriteriich
		for (int i = 0; i < from.criterias.size(); ++i) {
			and(from.criterias.get(i), from.parameters.get(i));
		}
		return this;
	}
	*/

	public JpaQlSearch() {
	}

	public JpaQlSearch(String from) {
		from(from);
	}

	public List<Object> getParameters() {
		return new ArrayList<Object>(parameters); //defensive copy
	}

	public List<String> getCriterias() {
		return new ArrayList<String>(criterias); //defensive copy
	}

	public void clearCriterias() {
		this.criterias.clear();
		this.parameters.clear();
	}

	public JpaQlSearch from(String from) {
		this.from = from;
		return this;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public JpaQlSearch innerJoin(String join) {
		join = join.trim();
		if (join.startsWith("JOIN") || join.startsWith("join")) {
			this.innerJoins.add(join);
		} else {
			this.innerJoins.add("JOIN " + join);
		}
		return this;
	}

	public JpaQlSearch leftJoin(String join) {
		join = join.trim();
		if (join.startsWith("LEFT JOIN") || join.startsWith("left join")) {
			this.leftJoins.add(join);
		} else {
			this.leftJoins.add("LEFT JOIN " + join);
		}
		return this;
	}

	public String buildWherex() {
		String where = buildWhere();
		for (int i = parameters.size() - 1; i >= 0; i--) {
			Object value = parameters.get(i);
			if (value instanceof String) {
				value = "'" + value + "'";
			}
			if (value instanceof Date) {
				value = DateUtil.format((Date) value, DateUtil.D_M_YYYY_HH_MM_SS_SSS);
			}
			String repl = "?" + (i + 1);
			where = where.replace(repl, String.valueOf(value));
		}
		return where;
	}

	public List getResultList(EntityManager em) {
		return buildJpaQuery(em).getResultList();
	}

	public Long getTotalResults(EntityManager em) {
		return (Long) buildJpaQuery("SELECT COUNT(*)", em).getSingleResult();
	}

	public List<?> getResultList(String selectClause, EntityManager em) {
		return buildJpaQuery(selectClause, em).getResultList();
	}

	/**
	 * Predpoklada se ze selectCountFrom je SELECT COUNT(*) FROM Entity e
	 */
	public Long getTotalResults(String selectCount, EntityManager em) {
		return (Long) buildJpaQuery(selectCount, em).getSingleResult();
	}

	public Query buildJpaQuery(EntityManager em) {
		String selectClause = buildSelect();
		return buildJpaQuery(selectClause, em);
	}

	public Query buildJpaQuery(String selectClause, EntityManager em) {
		String jpaql = buildQl(selectClause);
		Query query = em.createQuery(jpaql);
		for (int i = 0; i < parameters.size(); i++) {
			Object param = parameters.get(i);
			if (param == null) {
				//sanity check - none should be null
				throw new IllegalStateException("Null parameter on position " + (i + 1));
			}
			query.setParameter(i + 1, param);
		}
		return query;
	}

	public String buildQl() {
		return buildQl(buildSelect());
	}

	/**
	 * Custom selectClause and havingClause
	 */
	public String buildQl(String selectClause, String havingClause) {
		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append(selectClause);
		sbQuery.append(buildFrom());
		sbQuery.append(buildWhere());
		sbQuery.append(buildOrderBy());
		sbQuery.append(" ");
		sbQuery.append(havingClause);
		return sbQuery.toString();
	}

	public String buildQl(String selectClause) {
		int jpaqlLength = selectClause.length() + (criterias.size() * 10) + (orderBys.size() * 5);
		StringBuilder sb = new StringBuilder(jpaqlLength);
		sb.append(selectClause);
		sb.append(buildFrom());
		sb.append(buildWhere());
		sb.append(buildOrderBy());
		return sb.toString();
	}

	/**
	 * @return 'SELECT (DISTINCT) o'
	 */
	public String buildSelect() {
		if (from == null) {
			throw new IllegalStateException("From is not specified");
		}
		StringBuilder sb = new StringBuilder();
		String selection;
		if (from.indexOf(" ") == -1) {
			from = from + " o";
			selection = "o";
		} else {
			selection = from.substring(from.indexOf(" ") + 1);
		}
		sb.append("SELECT ");
		if (distinct) {
			sb.append("DISTINCT ");
		}
		sb.append(selection);

		return sb.toString();
	}

	/**
	 * @return 'FROM Entity o (LEFT JOIN FETCH o.joinedEntity ...)'
	 */
	public String buildFrom() {
		if (from == null) {
			throw new IllegalStateException("From is not specified");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("\nFROM ");
		sb.append(from);
		for (int i = 0; i < innerJoins.size(); i++) {
			sb.append("\n");
			sb.append(innerJoins.get(i));
		}
		for (int i = 0; i < leftJoins.size(); i++) {
			sb.append("\n");
			sb.append(leftJoins.get(i));
		}
		return sb.toString();
	}

	/**
	 * @return 'WHERE criteria...'
	 */
	public String buildWhere() {
		if (criterias.size() > 0) {
			StringBuilder sb = new StringBuilder((criterias.size() * 10));

			sb.append("\nWHERE\n");
			for (int i = 0; i < criterias.size(); i++) {
				sb.append(criterias.get(i));
				if (i < criterias.size() - 1) {
					sb.append("\nAND ");
				}
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	/**
	 * @return 'ORDER BY orderBys...'
	 */
	public String buildOrderBy() {
		if (orderBys.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("\nORDER BY ");
			for (int i = 0; i < orderBys.size(); i++) {
				sb.append(orderBys.get(i));
				if (i < orderBys.size() - 1) {
					sb.append(",\n");
				}
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	public JpaQlSearch orderBy(List<String> properties) {
		if (properties != null) {
			for (String p : properties) {
				orderBys.add(p);
			}
		}
		return this;
	}

	public JpaQlSearch orderBy(String... property) {
		if (property != null && property.length != 0) {
			orderBy(Arrays.asList(property));
		}
		return this;
	}

	/**
	 * property == value
	 */
	public <T> JpaQlSearch eq(String property, T... value) {
		if (value != null && value.length != 0) {
			if (value.length == 1) {
				add(property, Operator.EQ, value[0]);
			} else {
				andIn(false, property, value);
			}
		}
		return this;
	}

	/**
	 * property == value
	 */
	public <T> JpaQlSearch eq(String property, List<T> values) {
		if (values != null && values.size() != 0) {
			if (values.size() == 1) {
				add(property, Operator.EQ, values.get(0));
			} else {
				andIn(false, property, values);
			}
		}
		return this;
	}

	/**
	 * property != value
	 */
	public <T> JpaQlSearch ne(String property, T... value) {
		if (value != null && value.length != 0) {
			if (value.length == 1) {
				where(property, Operator.NEQ, value[0]);
			} else {
				andIn(true, property, value);
			}
		}
		return this;
	}

	public <T> JpaQlSearch in(String property, List<T> values) {
		andIn(false, property, values);
		return this;
	}

	public <T> JpaQlSearch in(String property, T... values) {
		andIn(false, property, values);
		return this;
	}

	public <T> JpaQlSearch notIn(String property, T... values) {
		andIn(true, property, values);
		return this;
	}

	/**
	 * Propery and operator and value is one string
	 * You should not do that...
	 */
	public JpaQlSearch where(String jpql) {
		criterias.add(jpql);
		return this;
	}

	/**
	 * Most flexible public method to add condition  
	 */
	public JpaQlSearch where(String property, Operator operator, Object value) {
		add(property, operator, value);
		return this;
	}

	public JpaQlSearch isNull(String property, Boolean yesNoAny) {
		is(property, yesNoAny, " IS NOT NULL", " IS NULL");
		return this;
	}

	public JpaQlSearch is1or0(String property, Boolean yesNoAny) {
		is(property, yesNoAny, " = 1", " = 0");
		return this;
	}

	/**
	 * property LIKE string
	 */
	public JpaQlSearch like(String property, LikeCriteria like) {
		if (like != null && like.valuesCount() != 0) {
			if (like.getUpper()) {
				property = "UPPER(" + property + ")";
				List<String> values = like.getValues();
				for (int i = 0; i < values.size(); ++i) {
					values.set(i, values.get(i).toUpperCase());
				}
			}
			List<String> values = like.getValues();
			for (int i = 0; i < values.size(); ++i) {
				String value = values.get(i);
				switch (like.getStyle()) {
				case LEFT:
					value = "%" + value;
					break;
				case RIGHT:
					value = value + "%";
					break;
				case BOTH:
					value = "%" + value + "%";
					break;
				default:
					throw new NotSupportedException(like.getStyle());
				}
				values.set(i, value);
			}

			if (like.getValues().size() > 1) {
				or(property, Operator.LIKE, like.getValues());
			} else {
				like(property, like.getValue());
			}
		}
		return this;
	}

	/**
	 * UPPER(property) LIKE string
	 */
	public JpaQlSearch likeUpper(String property, String value) {
		if (value != null && StringUtils.isNotBlank(value) && value.trim().equals("%") == false
				&& value.trim().equals("%%") == false) {
			property = "UPPER(" + property + ")";
			like(property, (Object) value);
		}
		return this;
	}

	/**
	 * property LIKE value
	 */
	public JpaQlSearch like(String property, String value) {
		if (value != null && StringUtils.isNotBlank(value) && value.trim().equals("%") == false
				&& value.trim().equals("%%") == false) {
			like(property, (Object) value);
		}
		return this;
	}

	@Deprecated
	public JpaQlSearch like(String property, Object value) {
		if (value != null) {
			where(property, Operator.LIKE, value);
		}
		return this;
	}

	/**
	 * (property1 == value OR property2 == value)
	 */
	public void orEq(String property1, String property2, Object value) {
		or(property1, Operator.EQ, value, property2, Operator.EQ, value);
	}

	/**
	 * (property == 'value1' OR property == 'value2' OR ...) 
	 */
	public <T> void orEq(String property, T... values) {
		or(property, Operator.EQ, values);
	}

	/**
	 * (property operator 'value1' OR property operator 'value2' OR ...) 
	 */
	public <T> void or(String property, Operator operator, T... values) {
		List<T> asList = Arrays.asList(values);
		or(property, operator, asList);
	}

	/**
	 * (property operator value1 OR property operator value2 OR ...)
	 */
	public <T> void or(String property, Operator operator, List<T> values) {
		if (values != null && values.size() > 0) {
			boolean added = false;
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			for (int i = 0; i < values.size(); ++i) {
				if (values.get(i) != null) {
					parameters.add(values.get(i));
					if (i > 0 && added) {
						sb.append(" OR ");
					}
					sb.append(property);
					sb.append(" ");
					sb.append(operator.getQl());
					sb.append(" ");
					sb.append('?');
					sb.append(parameters.size());
					added = true;
				}
			}
			sb.append(")");
			if (added) {
				criterias.add(sb.toString());
			}
		}
	}

	/**
	 * (UPPER(x.city) LIKE %value% OR UPPER(x.street) LIKE %value%) 
	 */
	public void orLike(String property1, String property2, String value) {
		property1 = "UPPER(" + property1 + ")";
		property2 = "UPPER(" + property2 + ")";
		value = (value == null) ? null : "%" + value.toUpperCase() + "%";
		or(property1, property2, Operator.LIKE, value);
	}

	/**
	 * (property1 operator value OR property2 operator value)
	 * (x.width > 100 OR x.length > 100)
	 */
	public void or(String property1, String property2, Operator operator, Object value) {
		or(property1, operator, value, property2, operator, value);
	}

	/**
	 * (property1 operator1 value1 OR property2 operator2 value2)
	 * (x.width > 'value1' OR x.length < 'value2') 
	 */
	public void or(String property1, Operator operator1, Object value1, String property2,
			Operator operator2, Object value2) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(property1);
		sb.append(' ');
		sb.append(operator1.getQl());
		sb.append(' ');
		sb.append('?');
		parameters.add(value1);
		sb.append(parameters.size());
		sb.append(" OR ");
		sb.append(property2);
		sb.append(' ');
		sb.append(operator2.getQl());
		sb.append(' ');
		sb.append('?');
		parameters.add(value2);
		sb.append(parameters.size());
		sb.append(')');
		criterias.add(sb.toString());
	}

	/**
	 * Core trio: @param property @param operator @param value 
	 */
	private void add(String property, Operator operator, Object value) {
		if (value != null) {
			StringBuilder sb = new StringBuilder();
			parameters.add(value);
			sb.append(property);
			sb.append(' ');
			sb.append(operator.getQl());
			sb.append(' ');
			sb.append('?');
			sb.append(parameters.size());
			criterias.add(sb.toString());
		}
	}

	/**
	 * Propery and operator is one string
	 * You should not do that...
	 */
	@Deprecated
	public JpaQlSearch where(String propertyAndOperator, Object value) {
		if (value != null) {
			parameters.add(value);
			StringBuilder sb = new StringBuilder();
			sb.append(propertyAndOperator);
			sb.append(' ');
			sb.append('?');
			sb.append(parameters.size());
			criterias.add(sb.toString());
		}
		return this;
	}

	/**
	 * Support function for {@link #andIsNull} or {@link #andIs1or0} 
	 *  
	 * yesNoAny == true -> property ifYes
	 */
	private JpaQlSearch is(String property, Boolean yesNoAny, String ifYes, String ifNo) {
		if (yesNoAny != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(property);

			if (yesNoAny == true) {
				sb.append(ifYes);
			} else {
				sb.append(ifNo);
			}
			criterias.add(sb.toString());
		}
		return this;
	}

	private <T> void andIn(boolean notIn, String property, List<T> values) {
		boolean added = false;
		if (values != null && values.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(property);
			if (notIn) {
				sb.append(" NOT");
			}
			sb.append(" IN (");

			for (int i = 0; i < values.size(); ++i) {
				T value = values.get(i);
				if (value != null) {
					if (value instanceof String && StringUtils.isBlank((String) value)) {
						continue; //realy exclude empty strings ???
					}
					parameters.add(value);
					if (i > 0 && added) {
						sb.append(',');
					}
					sb.append('?');
					sb.append(parameters.size());
					added = true;
				}
			}

			sb.append(")");
			if (added) {
				//add only when some param is not null
				criterias.add(sb.toString());
			}
		}
	}

	private <T> void andIn(boolean notIn, String property, T... values) {
		andIn(notIn, property, Arrays.asList(values));
	}

	/**
	 * values are comma separated string of direct values
	 * You should not use this...
	 */
	@Deprecated
	public void andIn(String property, String values) {
		if (values != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(property);
			sb.append(" IN (");
			sb.append(values);
			sb.append(")");
			criterias.add(sb.toString());
		}
	}

	public void inRange(String property, DateCriteria range) {
		if (range != null && (range.getAfter() != null || range.getBefore() != null)) {
			StringBuilder sb = new StringBuilder();
			sb.append(property);

			if (range.getType() == DateSearchType.INSIDE) {
				if (range.getBefore() == null) {
					throw new NotSupportedException("Before date must not be null");
				}
				sb.append(" BETWEEN ?");
				parameters.add(range.getAfter());
				sb.append(parameters.size());
				sb.append(" AND ?");
				parameters.add(range.getBefore());
				sb.append(parameters.size());

			} else if (range.getType() == DateSearchType.OUTSIDE) {
				if (range.getBefore() == null) {
					throw new NotSupportedException("Before date must not be null");
				}
				sb.append(" NOT BETWEEN ?");
				parameters.add(range.getAfter());
				sb.append(parameters.size());
				sb.append(" AND ?");
				parameters.add(range.getBefore());
				sb.append(parameters.size());

			} else if (range.getType() == DateSearchType.AFTER) {
				parameters.add(range.getAfter());
				sb.append(" >= ");
				sb.append('?');
				sb.append(parameters.size());

			} else if (range.getType() == DateSearchType.BEFORE) {
				parameters.add(range.getBefore());
				sb.append(" <= ");
				sb.append('?');
				sb.append(parameters.size());

			} else if (range.getType() == DateSearchType.EXACT) {
				//presne znamena na den
				Date after = DateUtil.getStartOfDay(range.getAfter());
				Date before = DateUtil.getEndOfDay(range.getAfter());
				sb.append(" BETWEEN ?");
				parameters.add(after);
				sb.append(parameters.size());
				sb.append(" AND ?");
				parameters.add(before);
				sb.append(parameters.size());
			} else {
				throw new IllegalArgumentException("Unsupported " + range.getType());
			}
			criterias.add(sb.toString());
		}

	}

	/**
	 * property BETWEEN (lower, upper) if both @param lower and @param upper are NOT null
	 * property >= lower if @param lower is NOT and @param upper IS null
	 * property <= upper if @param lower IS null and @param upper is NOT null
	 */
	public void between(String property, Object lower, Object upper) {
		if (lower != null || upper != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(property);
			if (lower != null && upper != null) {
				sb.append(" BETWEEN ?");
				parameters.add(lower);
				sb.append(parameters.size());
				sb.append(" AND ?");
				parameters.add(upper);
				sb.append(parameters.size());
			} else if (lower != null) {
				parameters.add(lower);
				sb.append(" >= ");
				sb.append('?');
				sb.append(parameters.size());
			} else if (upper != null) {
				parameters.add(upper);
				sb.append(" <= ");
				sb.append('?');
				sb.append(parameters.size());
			}
			criterias.add(sb.toString());
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(buildSelect());
		sb.append(buildFrom());
		sb.append(buildWherex());
		sb.append(buildOrderBy());
		return sb.toString();
	}
}