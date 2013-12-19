package net.anthavio.dao.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import net.anthavio.NotSupportedException;
import net.anthavio.dao.search.criteria.ColumnCriteria;
import net.anthavio.dao.search.criteria.CompareColumnCriteria;
import net.anthavio.dao.search.criteria.DateCriteria;
import net.anthavio.dao.search.criteria.DateSearchType;
import net.anthavio.dao.search.criteria.LikeCriteria;
import net.anthavio.dao.search.criteria.NumberColumnCriteria;
import net.anthavio.dao.search.criteria.Operator;
import net.anthavio.dao.search.criteria.StringColumnCriteria;
import net.anthavio.dao.search.criteria.ValueAndOperator;
import net.anthavio.util.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.JoinExpression;
import com.mysema.query.JoinFlag;
import com.mysema.query.QueryModifiers;
import com.mysema.query.SearchResults;
import com.mysema.query.jpa.JPAQueryMixin;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.support.Expressions;
import com.mysema.query.types.CollectionExpression;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.Expression;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.ComparableExpression;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.expr.StringExpression;
import com.mysema.query.types.path.DatePath;
import com.mysema.query.types.path.EntityPathBase;

/**
 * 
 * @author vanek
 *
 */
public class QueryDslSearch {

	private static final Logger log = LoggerFactory.getLogger(QueryDslSearch.class);

	SimpleExpression<Object[]> ALL = Expressions.template(Object[].class, "*");

	//Expression<Object[]> ALL = Wildcard.all;

	private final JPAQuery jpaQuery;

	private boolean collectionFetch = false;

	private boolean entityFetch = false;

	private EntityPath<?> root;

	public QueryDslSearch(EntityManager em) {
		jpaQuery = new JPAQuery(em);
	}

	public QueryDslSearch(EntityManager em, EntityPath<?>... roots) {
		this(em);
		jpaQuery.from(roots);
		root = roots[0];
	}

	public QueryDslSearch(JPAQuery jpaQuery) {
		this.jpaQuery = jpaQuery;
	}

	public JPAQuery getJpaQuery() {
		return this.jpaQuery;
	}

	/*
	 * Only few most used methods are delegated. Use this.getJpaQuery() to access rest
	 */

	public QueryDslSearch from(EntityPath<?>... args) {
		jpaQuery.from(args);
		root = args[0];
		return this;
	}

	public QueryDslSearch where(Predicate o) {
		jpaQuery.where(o);
		return this;
	}

	public QueryDslSearch joinFetch(EntityPath<?> o) {
		jpaQuery.join(o).fetch();
		entityFetch = true;
		return this;
	}

	public <P> QueryDslSearch joinFetch(CollectionExpression<?, P> o) {
		jpaQuery.join(o).fetch();
		collectionFetch = true;
		return this;
	}

	public QueryDslSearch leftJoinFetch(EntityPath<?> o) {
		jpaQuery.leftJoin(o).fetch();
		entityFetch = true;
		return this;
	}

	public <P> QueryDslSearch leftJoinFetch(CollectionExpression<?, P> o) {
		jpaQuery.leftJoin(o).fetch();
		collectionFetch = true;
		return this;
	}

	public QueryDslSearch orderBy(OrderSpecifier<?> order) {
		jpaQuery.orderBy(order);
		return this;
	}

	public QueryDslSearch limit(Long limit) {
		jpaQuery.limit(limit);
		return this;
	}

	public QueryDslSearch restrict(Long start, Long count) {
		jpaQuery.restrict(new QueryModifiers(count, start));
		return this;
	}

	public Long count(SimpleExpression<?> property) {
		return jpaQuery.uniqueResult(property.count());
	}

	public Long countDistinct(SimpleExpression<?> property) {
		return jpaQuery.uniqueResult(property.countDistinct());
	}

	/**
	 * Uses Hibernate nonstandard SELECT COUNT(*) JpaQl
	 * Works on Oracle/Derby COUNT with composite pk
	 */
	public long countAll() {
		return jpaQuery.uniqueResult(ALL.count());
		//return jpaQuery.count();
	}

	/**
	 * Uses Hibernate nonstandard SELECT COUNT(*) JpaQl
	 * Works on Oracle/Derby COUNT with composite pk
	 */
	public long countAllDistinct() {
		return jpaQuery.uniqueResult(ALL.countDistinct());
		//return jpaQuery.countDistinct();
	}

	public List<?> list() {
		return jpaQuery.list(root);
	}

	public <RT> List<RT> list(Expression<RT> expr) {
		return jpaQuery.list(expr);
	}

	public <RT> List<RT> listDistinct(Expression<RT> expr) {
		jpaQuery.getMetadata().setDistinct(true);
		return jpaQuery.list(expr);
	}

	/**
	 * Easy one but unusable for entities with composite primary key on Oracle or Derby.
	 * Oracle does not support select count(pk_column1, pk_column2)
	 */
	public <RT> PagedResult<RT> listPaged(Expression<RT> expr, PagedCriteria criteria) {
		return listPaged(expr, criteria, false);
	}

	/**
	 * Easy one but unusable for entities with composite primary key on Oracle or Derby.
	 * Oracle does not support select count(pk_column1, pk_column2)
	 */
	public <RT> PagedResult<RT> listDistinctPaged(Expression<RT> expr, PagedCriteria criteria) {
		return listPaged(expr, criteria, true);
	}

	/**
	 * Easy one but unusable for entities with composite primary key on Oracle or Derby.
	 * Oracle does not support select count(pk_column1, pk_column2)
	 */
	public <RT> PagedResult<RT> listPaged(Expression<RT> expr, PagedCriteria criteria, boolean distinct) {

		if (criteria.getLimit() != null) {
			jpaQuery.limit(criteria.getLimit());
		}
		if (criteria.getOffset() != null) {
			jpaQuery.offset(criteria.getOffset());
		}

		jpaQuery.getMetadata().setDistinct(distinct);

		if (collectionFetch && distinct == false) {
			log.info("Collection FETCH JOIN without SELECT DISTINCT will multiply root in result");
		}

		long start = System.currentTimeMillis();
		SearchResults<RT> results = jpaQuery.listResults(expr);
		int millis = (int) (System.currentTimeMillis() - start);
		List<RT> list = results.getResults();
		long total = results.getTotal();

		return new PagedResult<RT>(list, millis, criteria.getLimit(), criteria.getOffset(), total);
	}

	/**
	 * Uses Hibernate nonstandard SELECT COUNT(*) JpaQl to get total count
	 * Works on Oracle/Derby COUNT with composite pk
	 */
	public <RT> PagedResult<RT> listAllPaged(Expression<RT> selectExpr, PagedCriteria criteria) {
		return listPaged(selectExpr, ALL, criteria, false);
	}

	/**
	 * Uses Hibernate nonstandard SELECT COUNT(DISTINCT *) JpaQl to get total count
	 * Works on Oracle/Derby COUNT with composite pk
	 */
	public <RT> PagedResult<RT> listAllDistinctPaged(Expression<RT> selectExpr, PagedCriteria criteria) {
		return listPaged(selectExpr, ALL, criteria, true);
	}

	/**
	 * Uses Hibernate nonstandard SELECT COUNT(DISTINCT *) JpaQl to get total count
	 * Works on Oracle/Derby COUNT with composite pk
	 */
	public <RT> PagedResult<RT> listPaged(Expression<RT> selectExpr, SimpleExpression<?> countExpr,
			PagedCriteria criteria, boolean distinct) {

		jpaQuery.getMetadata().setDistinct(distinct);

		if (collectionFetch && distinct == false) {
			log.info("Collection FETCH JOIN without SELECT DISTINCT will multiply root in result");
		}

		if (entityFetch && countExpr == ALL) {
			log.warn("COUNT(*) with Entity FETCH JOIN will not work");
		}

		//extract fetch flags before executing SELECT COUNT
		List<JoinExpression>[] fetchJoinGroups = extractFetchJoins();

		long start = System.currentTimeMillis();
		Long total = jpaQuery.uniqueResult(countExpr.count());

		List<RT> list;
		if (total > 0) {
			if (criteria.getLimit() != null) {
				jpaQuery.limit(criteria.getLimit());
			}
			if (criteria.getOffset() != null) {
				jpaQuery.offset(criteria.getOffset());
			}
			//return fetch flags before executing real query
			List<JoinExpression> fetchJoins = fetchJoinGroups[0];
			for (JoinExpression join : fetchJoins) {
				join.getFlags().add(JPAQueryMixin.FETCH); //FIXME since 3.x QueryDsl used ImmutableCollection -> add fails here
			}
			List<JoinExpression> fetchAllJoins = fetchJoinGroups[1];
			for (JoinExpression join : fetchAllJoins) {
				join.getFlags().add(JPAQueryMixin.FETCH_ALL_PROPERTIES); //FIXME since 3.x QueryDsl used ImmutableCollection -> add fails here
			}
			list = jpaQuery.list(selectExpr);
		} else {
			list = new ArrayList<RT>();
		}

		int millis = (int) (System.currentTimeMillis() - start);

		return new PagedResult<RT>(list, millis, criteria.getLimit(), criteria.getOffset(), total);
	}

	//private <RT> boolean isCompositePk(Expression<RT> expr) {

	//}
	//List<List<JoinExpression>>
	/**
	 * Clear FETCH and 'FETCH ALL PROPERTIES' flags in all joins
	 * and return this two types of joins in array for further processing
	 */
	@SuppressWarnings("unchecked")
	private List<JoinExpression>[] extractFetchJoins() {
		List<JoinExpression> fetchJoins = new ArrayList<JoinExpression>();
		List<JoinExpression> fetchAllJoins = new ArrayList<JoinExpression>();
		List<JoinExpression> joins = jpaQuery.getMetadata().getJoins();
		for (JoinExpression join : joins) {
			Set<JoinFlag> flags = join.getFlags();
			if (flags.contains(JPAQueryMixin.FETCH)) {
				flags.remove(JPAQueryMixin.FETCH); //FIXME since 3.x QueryDsl used ImmutableCollection -> remove fails here
				fetchJoins.add(join);
				//if (join.getTarget() instanceof Path<?>) {
				//}
			} else if (flags.contains(JPAQueryMixin.FETCH_ALL_PROPERTIES)) {
				flags.remove(JPAQueryMixin.FETCH_ALL_PROPERTIES); //FIXME since 3.x QueryDsl used ImmutableCollection -> remove fails here
				fetchAllJoins.add(join);
			}
		}
		return new List[] { fetchJoins, fetchAllJoins };
	}

	/*
		private boolean isFetchJoin() {
			List<JoinExpression> joins = jpaQuery.getMetadata().getJoins();
			for (JoinExpression join : joins) {
				Set<JoinFlag> flags = join.getFlags();
				if (flags.contains(HQLQueryMixin.FETCH) || flags.contains(HQLQueryMixin.FETCH_ALL_PROPERTIES)) {
					return true;
				}
			}
			return false;
		}
	 */

	/**
	 * Here comes typing magic
	 */
	//@SuppressWarnings({ "unchecked", "rawtypes" })
	public <D extends Serializable> QueryDslSearch where(ColumnCriteria<D> c) {
		switch (c.getType()) {
		case NUMBER:
			where((NumberExpression) c.getColumn(), (ValueAndOperator) c);
			break;
		case STRING:
			where((StringExpression) c.getColumn(), (ValueAndOperator) c);
			break;
		case COMPAR:
			where((ComparableExpression) c.getColumn(), (ValueAndOperator) c);
			break;
		case SIMPLE:
			where(c.getColumn(), (ValueAndOperator) c);
			break;
		default:
			throw new NotSupportedException("Unknown type " + c.getType());
		}
		return this;
	}

	public QueryDslSearch where(StringColumnCriteria c) {
		where(c.getColumn(), c);
		return this;
	}

	public <D extends Number & Comparable<D>> QueryDslSearch where(NumberColumnCriteria<D> c) {
		where(c.getColumn(), c);
		return this;
	}

	public <D extends Number & Comparable<D>> QueryDslSearch where(CompareColumnCriteria<D> c) {
		where(c.getColumn(), c);
		return this;
	}

	/**
	 * NumberExpression does not implement ComparableExpression :(
	 */
	public <D extends Number & Comparable<D>> QueryDslSearch where(NumberExpression<D> property,
			ValueAndOperator<D> condition) {

		if (condition != null && condition.getValues() != null) {
			switch (condition.getOperator()) {
			case EQ:
			case NEQ:
			case IN:
			case NOT_IN:
				where((SimpleExpression<D>) property, condition);
				break;
			case GT:
				jpaQuery.where(property.gt(condition.getValue()));
				break;
			case GOE:
				jpaQuery.where(property.goe(condition.getValue()));
				break;
			case LT:
				jpaQuery.where(property.lt(condition.getValue()));
				break;
			case LOE:
				jpaQuery.where(property.loe(condition.getValue()));
				break;
			default:
				throw new NotSupportedException("Unsupported operator " + condition.getOperator());
			}
		}
		return this;
	}

	/**
	 * StringExpression has special LIKE method. Otherwise it is ComparableExpression
	 */
	public QueryDslSearch where(StringExpression property, ValueAndOperator<String> condition) {

		if (condition != null && condition.getValue() != null) {
			if (condition.getOperator() == Operator.LIKE) {
				jpaQuery.where(property.like(condition.getValue()));
			} else {
				where((ComparableExpression<String>) property, condition);
			}
		}
		return this;
	}

	/**
	 * Here goes Dates etc...
	 */
	public <D extends Serializable & Comparable<D>> QueryDslSearch where(ComparableExpression<D> property,
			ValueAndOperator<D> condition) {

		if (condition != null && condition.getValue() != null) {
			switch (condition.getOperator()) {
			case EQ:
			case NEQ:
			case IN:
			case NOT_IN:
				where((SimpleExpression<D>) property, condition);
				break;
			case GT:
				jpaQuery.where(property.gt(condition.getValue()));
				break;
			case GOE:
				jpaQuery.where(property.goe(condition.getValue()));
				break;
			case LT:
				jpaQuery.where(property.lt(condition.getValue()));
				break;
			case LOE:
				jpaQuery.where(property.loe(condition.getValue()));
				break;
			default:
				throw new NotSupportedException("Unsupported operator " + condition.getOperator());
			}

		}
		return this;
	}

	/*
	//BeanPath is just SimpleExpression
	public <D extends Serializable> QueryDslSearch where(BeanPath<D> property,
			SimpleCriteria<D> condition) {
		where((SimpleExpression<D>) property, condition);
		return this;
	}
	 */

	/**
	 * SimpleCriteria knows only EQ, NE, IN
	 */
	public <D extends Serializable> QueryDslSearch where(SimpleExpression<D> property, ValueAndOperator<D> condition) {

		if (condition != null && condition.getValue() != null) {
			switch (condition.getOperator()) {
			case EQ:
			case IN:
				if (condition.getValues().size() == 1) {
					jpaQuery.where(property.eq(condition.getValue()));
				} else {
					jpaQuery.where(property.in(condition.getValues()));
				}
				break;
			case NEQ:
			case NOT_IN:
				if (condition.getValues().size() == 1) {
					jpaQuery.where(property.ne(condition.getValue()));
				} else {
					jpaQuery.where(property.notIn(condition.getValues()));
				}
				break;
			default:
				throw new NotSupportedException("Unsupported operator " + condition.getOperator() + " for expression "
						+ property);
			}
		}
		return this;
	}

	/**
	 * AND property == string
	 */
	public QueryDslSearch eq(StringExpression property, String... string) {
		if (string != null && string.length > 0) {
			if (string.length == 1 && string[0] != null) {
				jpaQuery.where(property.eq(string[0]));
			} else {
				List<String> list = new ArrayList<String>();
				for (String s : string) {
					if (s != null) {
						list.add(s);
					}
				}
				if (list.size() > 0) {
					jpaQuery.where(property.in(list));
				}
			}
		}
		return this;
	}

	/**
	 * Day percision comparision
	 * 
	 * AND property between (STARTOFDAY(date), ENDOFDAY(date))
	 */
	public QueryDslSearch eq(DatePath<java.util.Date> property, Date date) {
		if (date != null) {
			Date after = DateUtil.getStartOfDay(date);
			Date before = DateUtil.getEndOfDay(date);
			jpaQuery.where(property.between(after, before));
		}
		return this;
	}

	/**
	 * AND property != string
	 */
	public QueryDslSearch ne(StringExpression property, String... string) {
		if (string != null) {
			if (string.length == 1 && string[0] != null) {
				jpaQuery.where(property.ne(string[0]));
			} else {
				List<String> list = new ArrayList<String>();
				for (String s : string) {
					if (s != null) {
						list.add(s);
					}
				}
				if (list.size() > 0) {
					jpaQuery.where(property.notIn(list));
				}
			}
		}
		return this;
	}

	/**
	 * AND property LIKE string
	 */
	public QueryDslSearch like(StringExpression property, LikeCriteria like) {
		//TODO podpora pro vice hodnot
		if (like != null && StringUtils.isNotBlank(like.getValue())) {
			switch (like.getStyle()) {
			case LEFT:
				like(property, "%" + like.getValue());
				break;
			case RIGHT:
				like(property, like.getValue() + "%");
				break;
			case BOTH:
				like(property, "%" + like.getValue() + "%");
				break;
			default:
				throw new NotSupportedException(like.getStyle());
			}
		}
		return this;
	}

	/**
	 * Caller is responsibe for % placing
	 */
	public QueryDslSearch like(StringExpression property, String value) {
		if (value != null && StringUtils.isNotBlank(value) && value.trim().equals("%") == false) {
			jpaQuery.where(property.like(value));
		}
		return this;
	}

	/**
	 * AND (property == string1 || property == string2 || ... )
	 */
	public void orEq(StringExpression property, String... strings) {
		if (strings != null && strings.length > 0) {
			BooleanBuilder builder = new BooleanBuilder();
			for (String value : strings) {
				builder.or(property.eq(value));
			}
			jpaQuery.where(builder);
		}
	}

	/**
	 * AND property == number
	 */
	public <D extends Number & Comparable<?>> QueryDslSearch eq(NumberExpression<D> property, D... number) {
		if (number != null && number.length > 0) {
			if (number.length == 1 && number[0] != null) {
				jpaQuery.where(property.eq(number[0]));
			} else {
				List<D> list = new ArrayList<D>();
				for (D d : number) {
					if (d != null) {
						list.add(d);
					}
				}
				if (list.size() > 0) {
					jpaQuery.where(property.in(list));
				}
			}
		}
		return this;
	}

	/**
	 * AND property != number
	 */
	public <D extends Number & Comparable<?>> QueryDslSearch ne(NumberExpression<D> property, D... number) {
		if (number != null && number.length > 0) {
			if (number.length == 1 && number[0] != null) {
				jpaQuery.where(property.ne(number[0]));
			} else {
				List<D> list = new ArrayList<D>();
				for (D d : number) {
					if (d != null) {
						list.add(d);
					}
				}
				if (list.size() > 0) {
					jpaQuery.where(property.notIn(list));
				}
			}
		}
		return this;
	}

	/**
	 * AND (property == number1 ||  property == number2 || ... )
	 */
	public <D extends Number & Comparable<?>> void orEq(NumberExpression<D> property, D... numbers) {
		if (numbers != null && numbers.length > 0) {
			BooleanBuilder builder = new BooleanBuilder();
			for (D value : numbers) {
				builder.or(property.eq(value));
			}
			jpaQuery.where(builder);
		}
	}

	/**
	 * AND property == entity
	 */
	public <D> QueryDslSearch eq(EntityPathBase<D> property, D... entity) {
		if (entity != null && entity.length > 0) {
			if (entity.length == 1 && entity[0] != null) {
				jpaQuery.where(property.eq(entity[0]));
			} else {
				List<D> list = new ArrayList<D>();
				for (D e : entity) {
					if (e != null) {
						list.add(e);
					}
				}
				if (list.size() > 0) {
					jpaQuery.where(property.in(list));
				}
			}
		}
		return this;
	}

	/**
	 * AND property != entity
	 */
	public <D> QueryDslSearch ne(EntityPathBase<D> property, D... entity) {
		if (entity != null && entity.length > 0) {
			if (entity.length == 1 && entity[0] != null) {
				jpaQuery.where(property.ne(entity[0]));
			} else {
				List<D> list = new ArrayList<D>();
				for (D e : entity) {
					if (e != null) {
						list.add(e);
					}
				}
				if (list.size() > 0) {
					jpaQuery.where(property.notIn(list));
				}
			}
		}
		return this;
	}

	public QueryDslSearch range(DatePath<java.util.Date> property, DateCriteria criteria) {
		if (criteria != null && criteria.getType() != null) {
			BooleanExpression condition;
			if (criteria.getType() == DateSearchType.INSIDE) {
				if (criteria.getAfter() == null) {
					throw new NotSupportedException("After date must not be null");
				}
				if (criteria.getBefore() == null) {
					throw new NotSupportedException("Before date must not be null");
				}
				condition = property.between(criteria.getAfter(), criteria.getBefore());

			} else if (criteria.getType() == DateSearchType.OUTSIDE) {
				if (criteria.getAfter() == null) {
					throw new NotSupportedException("After date must not be null");
				}
				if (criteria.getBefore() == null) {
					throw new NotSupportedException("Before date must not be null");
				}
				condition = property.notBetween(criteria.getAfter(), criteria.getBefore());

			} else if (criteria.getType() == DateSearchType.AFTER) {
				if (criteria.getAfter() == null) {
					throw new NotSupportedException("After date must not be null");
				}
				condition = property.after(criteria.getAfter());

			} else if (criteria.getType() == DateSearchType.BEFORE) {
				if (criteria.getBefore() == null) {
					throw new NotSupportedException("Before date must not be null");
				}
				condition = property.before(criteria.getBefore());

			} else if (criteria.getType() == DateSearchType.EXACT) {
				//exact means inside day
				if (criteria.getExact() == null) {
					throw new NotSupportedException("Exact date must not be null");
				}
				Date after = DateUtil.getStartOfDay(criteria.getExact());
				Date before = DateUtil.getEndOfDay(criteria.getExact());
				condition = property.between(after, before);

			} else {
				throw new NotSupportedException(criteria.getType());
			}
			jpaQuery.where(condition);
		}
		return this;
	}

}
