/**
 * 
 */
package net.anthavio.dao.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.anthavio.NotSupportedException;
import net.anthavio.dao.search.criteria.DateCriteria;
import net.anthavio.dao.search.criteria.DateSearchType;
import net.anthavio.dao.search.criteria.LikeCriteria;
import net.anthavio.dao.search.criteria.Operator;
import net.anthavio.dao.search.criteria.Operator.Cardinality;
import net.anthavio.util.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author vanek
 *
 */
public class SqlSearch implements Serializable {

	private static final long serialVersionUID = 1L;

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private boolean numArgs = false;

	private String from;

	private boolean distinct = false;

	private final List<String> innerJoins = new ArrayList<String>();

	private final List<String> leftJoins = new ArrayList<String>();

	protected final List<Object> parameters = new ArrayList<Object>();

	private final List<SqlCriterion> criterias = new ArrayList<SqlCriterion>();

	private final List<String> orderBys = new ArrayList<String>();

	/*
	private String alias;

	public SqlSearch(String alias) {
		if (alias != null && alias.length() > 0 && !alias.endsWith(".")) {
			this.alias = alias + ".";
		} else {
			this.alias = "";
		}
	}
	*/
	public SqlSearch() {

	}

	public SqlSearch(String from) {
		from(from);
	}

	public SqlSearch from(String from) {
		this.from = from;
		return this;
	}

	public List<Object> getParameters() {
		return new ArrayList<Object>(parameters); //defensive copy
	}

	public Object[] getParametersArray() {
		return parameters.toArray(new Object[parameters.size()]);
	}

	public List<SqlCriterion> getCriterias() {
		return new ArrayList<SqlCriterion>(criterias); //defensive copy
	}

	public void clearCriterias() {
		this.criterias.clear();
		this.parameters.clear();
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * @return where clause with parmeters inlined into sentence
	 */
	public String buildFullWhere() {
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
				printQl(criterias.get(i), sb);
				if (i < criterias.size() - 1) {
					sb.append("\nAND ");
				}
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	private void printQl(SqlCriterion sqlc, StringBuilder sb) {
		if (sqlc.orCriteria != null) {
			sb.append('(');
			for (SqlCriterion criterion : sqlc.orCriteria) {
				printQl(criterion, sb);
				sb.append(" OR ");
			}
			sb.delete(sb.length() - 4, sb.length() - 1);
			sb.append(')');

		} else {
			sb.append(sqlc.property);
			sb.append(' ');
			sb.append(sqlc.operator.getQl());
			sb.append(' ');
			if (sqlc.operator.getCaridinality() == Cardinality.ZERO) {
				//nothing
			} else if (sqlc.operator == Operator.BETWEEN) {
				parameters.add(sqlc.values[0]);
				sb.append('?');
				if (numArgs) {
					sb.append(parameters.size());
				}
				sb.append(" AND ");
				parameters.add(sqlc.values[0]);
				sb.append('?');
				if (numArgs) {
					sb.append(parameters.size());
				}

			} else {
				if (sqlc.operator == Operator.IN || sqlc.operator == Operator.NOT_IN) {
					sb.append('(');
				}
				for (int i = 0; i < sqlc.values.length; ++i) {
					parameters.add(sqlc.values[i]);
					sb.append('?');
					if (numArgs) {
						sb.append(parameters.size());
					}
					sb.append(',');
				}
				sb.deleteCharAt(sb.length() - 1);
				if (sqlc.operator == Operator.IN || sqlc.operator == Operator.NOT_IN) {
					sb.append(')');
				}
			}
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

	public SqlSearch innerJoin(String join) {
		join = join.trim();
		if (join.startsWith("JOIN") || join.startsWith("join")) {
			this.innerJoins.add(join);
		} else {
			this.innerJoins.add("JOIN " + join);
		}
		return this;
	}

	public SqlSearch leftJoin(String join) {
		join = join.trim();
		if (join.startsWith("LEFT JOIN") || join.startsWith("left join")) {
			this.leftJoins.add(join);
		} else {
			this.leftJoins.add("LEFT JOIN " + join);
		}
		return this;
	}

	public SqlSearch orderBy(List<String> properties) {
		if (properties != null) {
			for (String p : properties) {
				orderBys.add(p);
			}
		}
		return this;
	}

	public SqlSearch orderBy(String... property) {
		if (property != null && property.length != 0) {
			orderBy(Arrays.asList(property));
		}
		return this;
	}

	public static class SqlCriterion {

		public final String property;
		public Operator operator;
		public Object[] values;

		private final SqlCriterion[] orCriteria;

		public SqlCriterion(SqlCriterion... orCriteria) {
			this.property = null;
			if (orCriteria == null || orCriteria.length == 0) {
				throw new IllegalArgumentException("Empty OR criteria");
			}
			this.orCriteria = orCriteria;

		}

		public SqlCriterion(String property, Operator operator) {
			this.property = property;
			this.operator = operator;
			if (operator.getCaridinality() != Cardinality.ZERO) {
				throw new IllegalArgumentException("Operator requires value: " + operator);
			}
			this.orCriteria = null;
		}

		public SqlCriterion(String property, Operator operator, Object... values) {
			if (StringUtils.isBlank(property)) {
				throw new IllegalArgumentException("Blank property for operator " + operator);
			}
			this.property = property;

			if (operator == null) {
				throw new IllegalArgumentException("Null operator for property " + property);
			}
			this.operator = operator;

			if (values == null || values.length == 0) {
				throw new IllegalArgumentException("Null values for proeprty " + property);
			}
			for (Object value : values) {
				if (value == null) {
					throw new IllegalArgumentException("Null value found for property " + property);
				}
			}
			this.values = values;
			this.orCriteria = null;
		}

		public int getParamCount() {
			switch (operator.getCaridinality()) {
			case ZERO:
				return 0;
			case ONE:
				return 1;
			case MULTI:
				return values.length;
			default:
				throw new IllegalStateException("Unknown operator cardinality " + operator);
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(property);
			sb.append(' ');
			sb.append(operator.getQl());
			for (int i = 0; i < values.length; ++i) {
				sb.append(values[i]);
				sb.append(", ");
			}
			sb.deleteCharAt(sb.length() - 2);
			return sb.toString();
		}

	}

	/**
	 * property == value
	 */
	public <T> SqlCriterion eq(String property, T... values) {
		SqlCriterion criterion = null;
		if (values != null && values.length != 0) {
			criterion = build(property, Operator.EQ, values);
			if (criterion != null) {
				criterias.add(criterion);
			}
		}
		return criterion;
	}

	public <T> SqlCriterion in(String property, T... values) {
		return eq(property, values);
	}

	/**
	 * property == value
	 */
	public <T> SqlCriterion eq(String property, List<T> values) {
		SqlCriterion criterion = null;
		if (values != null && values.size() != 0) {
			criterion = build(property, Operator.EQ, values.toArray(new Object[values.size()]));
			if (criterion != null) {
				criterias.add(criterion);
			}
		}
		return criterion;
	}

	public <T> SqlCriterion in(String property, List<T> values) {
		return eq(property, values);
	}

	/**
	 * property != value
	 */
	public <T> SqlCriterion ne(String property, T... values) {
		SqlCriterion criterion = null;
		if (values != null && values.length != 0) {
			criterion = build(property, Operator.NEQ, values);
			if (criterion != null) {
				criterias.add(criterion);
			}
		}
		return criterion;
	}

	public <T> SqlCriterion notIn(String property, T... values) {
		return ne(property, values);
	}

	/**
	 * Most flexible public method to add condition  
	 */
	public SqlCriterion where(String property, Operator operator, Object value) {
		SqlCriterion criterion = build(property, operator, value);
		if (criterion != null) {
			criterias.add(criterion);
		}
		return criterion;
	}

	public SqlCriterion isNull(String property, Boolean yesNoAny) {
		SqlCriterion criterion = null;
		if (yesNoAny != null) {
			criterion = build(property, Operator.IS_NULL, yesNoAny);
			if (criterion != null) {
				criterias.add(criterion);
			}
		}
		return criterion;
	}

	public SqlCriterion isNotNull(String property, Boolean yesNoAny) {
		SqlCriterion criterion = null;
		if (yesNoAny != null) {
			criterion = build(property, Operator.IS_NOT_NULL, yesNoAny);
			if (criterion != null) {
				criterias.add(criterion);
			}
		}
		return criterion;
	}

	public SqlCriterion is1or0(String property, Boolean yesNoAny) {
		SqlCriterion criterion = null;
		if (yesNoAny != null) {
			if (yesNoAny) {
				criterion = build(property, Operator.EQ, 1);
			} else {
				criterion = build(property, Operator.EQ, 0);
			}
		}
		if (criterion != null) {
			criterias.add(criterion);
		}
		return criterion;
	}

	/**
	 * property LIKE string
	 */
	public SqlCriterion like(String property, LikeCriteria like) {
		SqlCriterion criterion = null;
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
				criterion = or(property, Operator.LIKE, like.getValues());
			} else {
				criterion = like(property, like.getValue());
			}
		}
		return criterion;
	}

	/**
	 * UPPER(property) LIKE string
	 */
	public SqlCriterion likeUpper(String property, String value) {
		if (value != null && StringUtils.isNotBlank(value) && value.trim().equals("%") == false
				&& value.trim().equals("%%") == false) {
			property = "UPPER(" + property + ")";
			return where(property, Operator.LIKE, value);
		}
		return null;
	}

	/**
	 * property LIKE value
	 */
	public SqlCriterion like(String property, String value) {
		if (value != null && StringUtils.isNotBlank(value) && value.trim().equals("%") == false
				&& value.trim().equals("%%") == false) {
			return where(property, Operator.LIKE, value);
		}
		return null;
	}

	@Deprecated
	public SqlCriterion like(String property, Object value) {
		if (value != null) {
			return where(property, Operator.LIKE, value);
		}
		return null;
	}

	/**
	 * (property == 'value1' OR property == 'value2' OR ...) 
	 */
	public <T> SqlCriterion orEq(String property, T... values) {
		return in(property, Operator.EQ, values);
	}

	/**
	 * (property1 == value OR property2 == value)
	 */
	public SqlCriterion orEq(String property1, String property2, Object value) {
		return or(property1, Operator.EQ, value, property2, Operator.EQ, value);
	}

	/**
	 * (property operator 'value1' OR property operator 'value2' OR ...) 
	 */
	public SqlCriterion or(String property, Operator operator, Object... values) {
		SqlCriterion criterion = null;
		if (values != null && values.length > 0) {
			List<SqlCriterion> tmp = new ArrayList<SqlCriterion>();
			for (int i = 0; i < values.length; ++i) {
				SqlCriterion sqlc = build(property, operator, values[i]);
				if (sqlc != null) {
					tmp.add(sqlc);
				}
			}
			if (tmp.size() == 1) {
				criterion = tmp.get(0);
				criterias.add(criterion);
			} else if (tmp.size() > 1) {
				criterion = new SqlCriterion(tmp.toArray(new SqlCriterion[tmp.size()]));
				criterias.add(criterion);
			}
		}
		return criterion;
	}

	/**
	* (property operator value1 OR property operator value2 OR ...)
	*/
	public SqlCriterion or(String property, Operator operator, List<Object> values) {
		SqlCriterion criterion = null;
		if (values != null && values.size() > 0) {
			criterion = or(property, operator, values.toArray(new Object[values.size()]));
		}
		return criterion;
	}

	/**
	 * (UPPER(x.city) LIKE %value% OR UPPER(x.street) LIKE %value%) 
	 */
	public SqlCriterion orLike(String property1, String property2, String value) {
		property1 = "UPPER(" + property1 + ")";
		property2 = "UPPER(" + property2 + ")";
		value = (value == null) ? null : "%" + value.toUpperCase() + "%";
		return or(property1, property2, Operator.LIKE, value);
	}

	/**
	 * (property1 operator value OR property2 operator value)
	 * (x.width > 100 OR x.length > 100)
	 */
	public SqlCriterion or(String property1, String property2, Operator operator, Object value) {
		return or(property1, operator, value, property2, operator, value);
	}

	public SqlCriterion or(SqlCriterion... items) {
		SqlCriterion criterion = null;
		if (items != null && items.length != 0) {
			List<SqlCriterion> tmp = new ArrayList<SqlSearch.SqlCriterion>();
			for (SqlCriterion item : items) {
				if (item != null) {
					tmp.add(item);
				}
			}
			if (tmp.size() == 1) {
				criterion = tmp.get(0);
				criterias.add(criterion);
			} else if (tmp.size() > 1) {
				criterion = new SqlCriterion(tmp.toArray(new SqlCriterion[tmp.size()]));
				criterias.add(criterion);
			}
		}
		return criterion;
	}

	/**
	 * (property1 operator1 value1 OR property2 operator2 value2)
	 * (x.width > 'value1' OR x.length < 'value2') 
	 */
	public SqlCriterion or(String property1, Operator operator1, Object value1, String property2, Operator operator2,
			Object value2) {
		SqlCriterion sqlc1 = build(property1, operator1, value1);
		SqlCriterion sqlc2 = build(property2, operator2, value2);
		return or(sqlc1, sqlc2);
	}

	public static SqlCriterion build(String property, Operator operator, Object... values) {
		SqlCriterion criterion = null;
		values = collect(values);
		if (values.length == 1) {
			switch (operator.getCaridinality()) {
			case ZERO:
				Boolean trueValue = boolise(values[0]);
				if (trueValue == null) {
					throw new IllegalArgumentException("Property " + property + " operator " + operator
							+ " needs boolable value: " + values[0]);
				} else if (trueValue) {
					criterion = new SqlCriterion(property, operator);
				} else {
					//value is explicitly set to false -> negate
					if (operator == Operator.IS_NULL) {
						criterion = new SqlCriterion(property, Operator.IS_NOT_NULL);
					} else if (operator == Operator.IS_NOT_NULL) {
						criterion = new SqlCriterion(property, Operator.IS_NULL);
					}
				}
				break;
			case ONE:
				criterion = new SqlCriterion(property, operator, values);
				break;
			case MULTI:
				if (operator == Operator.IN) {
					operator = Operator.EQ;
				} else if (operator == Operator.NOT_IN) {
					operator = Operator.NEQ;
				}
				criterion = new SqlCriterion(property, operator, values);
				break;
			default:
				throw new IllegalStateException("Unsupported cardinality " + operator);
			}
		} else if (values.length > 1) {
			switch (operator.getCaridinality()) {
			case ZERO:
				throw new IllegalArgumentException("Property " + property + " operator " + operator
						+ " disallows multiple values");
			case ONE:
				if (operator == Operator.EQ) {
					criterion = new SqlCriterion(property, Operator.IN, values);
				} else if (operator == Operator.NEQ) {
					criterion = new SqlCriterion(property, Operator.NOT_IN, values);
				} else {
					//or create more SqlCriterion for others?
					throw new IllegalArgumentException("Property " + property + " operator " + operator
							+ " disallows multiple values");
				}
				break;
			case MULTI:
				criterion = new SqlCriterion(property, operator, values);
				break;
			default:
				throw new IllegalStateException("Unsupported cardinality " + operator);
			}
		}
		return criterion;
	}

	private static Boolean boolise(Object value) {
		if (value instanceof Boolean) {
			return (Boolean) value;
		} else if (value instanceof String) {
			String string = ((String) value).trim().toLowerCase();
			if (string.equals("true") || string.equals("yes")) {
				return true;
			} else if (string.equals("false") || string.equals("no")) {
				return false;
			}
		}
		return null;
	}

	private static final Object[] EMTPY = new Object[0];

	private static Object[] collect(Object... values) {
		Object[] retval;
		if (values != null && values.length != 0) {
			List<Object> temp = new ArrayList<Object>();
			for (Object value : values) {
				if (value != null) {
					if (value instanceof String && StringUtils.isNotEmpty((String) value)) {
						temp.add(value);
					} else {
						temp.add(value);
					}
				}
			}
			if (temp.size() != 0) {
				retval = temp.toArray(new Object[temp.size()]);
			} else {
				retval = EMTPY;
			}
		} else {
			retval = EMTPY;
		}
		return retval;
	}

	/**
	 * Propery and operator is one string
	 * You should not do that...
	 */
	/*
	@Deprecated
	public int where(String propertyAndOperator, Object value) {
		int cnt = 0;
		if (value != null) {
			parameters.add(value);
			StringBuilder sb = new StringBuilder();
			sb.append(propertyAndOperator);
			sb.append(' ');
			sb.append('?');
			if (numArgs) {
				sb.append(parameters.size());
			}
			criterias.add(sb.toString());
			cnt = 1;
		}
		return cnt;
	}
	*/
	/*
	private <T> int andIn(boolean not, String property, List<T> values) {
		int cnt = 0;
		if (values != null && values.size() > 0) {

			StringBuilder sb = new StringBuilder();
			sb.append(property);
			if (not) {
				sb.append(" NOT");
			}
			sb.append(" IN (");
			for (int i = 0; i < values.size(); ++i) {
				T value = values.get(i);
				if (value != null) {
					if (value instanceof String && StringUtils.isBlank((String) value)) {
						continue; //really exclude empty strings ???
					}

					if (i > 0 && cnt > 0) {
						sb.append(',');
					}
					parameters.add(value);
					sb.append('?');
					if (numArgs) {
						sb.append(parameters.size());
					}
					++cnt;
				}
			}
			sb.append(")");

			if (cnt == 1) {
				if (not) {
					ne(property, parameters.remove(parameters.size() - 1));
				} else {
					eq(property, parameters.remove(parameters.size() - 1));
				}
			} else if (cnt > 1) {
				criterias.add(sb.toString());
			}
		}
		return cnt;
	}
	*/
	/**
	 * values are comma separated string of direct values
	 * You should not use this...
	 */
	/*
	@Deprecated
	public int andIn(String property, String values) {
		int cnt = 0;
		if (values != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(property);
			sb.append(" IN (");
			sb.append(values);
			sb.append(")");
			criterias.add(sb.toString());
			cnt = values.split(",").length;
		}
		return cnt;
	}
	*/
	public SqlCriterion inRange(String property, DateCriteria range) {
		SqlCriterion criterion = null;
		if (range != null && (range.getAfter() != null || range.getBefore() != null)) {
			StringBuilder sb = new StringBuilder();
			sb.append(property);

			if (range.getType() == DateSearchType.INSIDE) {
				if (range.getBefore() == null) {
					throw new NotSupportedException("Before date must not be null");
				}
				sb.append(" BETWEEN ?");
				parameters.add(range.getAfter());
				if (numArgs) {
					sb.append(parameters.size());
				}
				sb.append(" AND ?");
				parameters.add(range.getBefore());
				if (numArgs) {
					sb.append(parameters.size());
				}
				criterion = new SqlCriterion(property, Operator.BETWEEN, range.getAfter(), range.getBefore());
				criterias.add(criterion);

			} else if (range.getType() == DateSearchType.OUTSIDE) {
				if (range.getBefore() == null) {
					throw new NotSupportedException("Before date must not be null");
				}
				sb.append(" NOT BETWEEN ?");
				parameters.add(range.getAfter());
				if (numArgs) {
					sb.append(parameters.size());
				}
				sb.append(" AND ?");
				parameters.add(range.getBefore());
				if (numArgs) {
					sb.append(parameters.size());
				}
				//XXX negace between
				criterion = new SqlCriterion(property, Operator.NOT_BETWEEN, range.getAfter(), range.getBefore());
				criterias.add(criterion);

			} else if (range.getType() == DateSearchType.AFTER) {
				sb.append(" >= ");
				parameters.add(range.getAfter());
				sb.append('?');
				if (numArgs) {
					sb.append(parameters.size());
				}
				criterion = new SqlCriterion(property, Operator.GOE, range.getAfter());
				criterias.add(criterion);

			} else if (range.getType() == DateSearchType.BEFORE) {
				sb.append(" <= ");
				parameters.add(range.getBefore());
				sb.append('?');
				if (numArgs) {
					sb.append(parameters.size());
				}
				criterion = new SqlCriterion(property, Operator.LOE, range.getBefore());
				criterias.add(criterion);

			} else if (range.getType() == DateSearchType.DAY) {
				//between 00:00:00 and 23:59:999
				Date after = DateUtil.getStartOfDay(range.getExact());
				Date before = DateUtil.getEndOfDay(range.getExact());
				sb.append(" BETWEEN ?");
				parameters.add(after);
				if (numArgs) {
					sb.append(parameters.size());
				}
				sb.append(" AND ?");
				parameters.add(before);
				if (numArgs) {
					sb.append(parameters.size());
				}
				criterion = new SqlCriterion(property, Operator.BETWEEN, after, before);
				criterias.add(criterion);

			} else if (range.getType() == DateSearchType.EXACT) {
				criterion = new SqlCriterion(property, Operator.EQ, range.getExact());
				criterias.add(criterion);

			} else {
				throw new IllegalArgumentException("Unsupported " + range.getType());
			}
		}
		return criterion;
	}

	/**
	 * property >= lower if @param lower is NOT null
	 * property <= upper if @param upper is NOT null
	 */
	public SqlCriterion between(String property, Object lower, Object upper) {
		SqlCriterion criterion = null;
		if (lower != null || upper != null) {
			if (lower != null) {
				criterion = new SqlCriterion(property, Operator.GOE, lower);
				criterias.add(criterion);
			} else if (upper != null) {
				criterion = new SqlCriterion(property, Operator.LOE, upper);
				criterias.add(criterion);
			}
		}
		return criterion;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(buildSelect());
		sb.append(buildFrom());
		sb.append(buildFullWhere());
		sb.append(buildOrderBy());
		return sb.toString();
	}

	public static void main(String[] args) {
		SqlSearch search = new SqlSearch("xxx");
		search.in("prop1", null, "c", null);
		SqlCriterion c2 = SqlSearch.build("prop2", Operator.EQ, null, "y");
		SqlCriterion c3 = SqlSearch.build("prop3", Operator.NEQ, "a", null, "x");
		search.or(null, c2, c3, null);
		search.between("prop4", 1, 2);
		search.isNotNull("prop5", true);
		String select = search.buildWhere();
		System.out.println(select);
	}

}
