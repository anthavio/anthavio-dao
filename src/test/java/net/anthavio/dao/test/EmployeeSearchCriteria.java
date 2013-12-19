package net.anthavio.dao.test;

import java.util.Date;

import net.anthavio.dao.search.QueryDslSearch;
import net.anthavio.dao.search.criteria.Column;
import net.anthavio.dao.search.criteria.ComplexCriteria;
import net.anthavio.dao.search.criteria.ValueAndOperator;
import net.anthavio.dao.test.entity.QEmployee;

import com.mysema.query.QueryMetadata;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.expr.StringExpression;
import com.mysema.query.types.path.DateTimePath;

public class EmployeeSearchCriteria extends ComplexCriteria<EmployeeSearchCriteria.XColumns> {

	private static final long serialVersionUID = 1L;

	public class XColumns implements Column<String> {

		XColumns firstName = new XColumns(QEmployee.employee.firstName);
		/*
		, //
		addressCity(QEmployee.employee.address().city), //
		yearsOfService(QEmployee.employee.yearsOfService), //
		startDate(QEmployee.employee.period().startDate), // 
		endDate(QEmployee.employee.period().endDate);
		*/
		private final SimpleExpression<?> column;

		private final boolean order;
		private final boolean select;
		private final boolean where;

		private XColumns(StringExpression column) {
			this(column, true, true, true);
		}

		private XColumns(NumberExpression<Integer> column) {
			this(column, true, true, true);
		}

		private XColumns(DateTimePath<Date> column) {
			this(column, true, true, true);
		}

		private XColumns(SimpleExpression<?> column, boolean order, boolean select, boolean where) {
			this.column = column;
			this.order = order;
			this.select = select;
			this.where = where;
		}

		public boolean isOrder() {
			return order;
		}

		public boolean isSelect() {
			return select;
		}

		public boolean isWhere() {
			return where;
		}

		public SimpleExpression<?> getColumn() {
			return column;
		}

		@Override
		public String getName() {
			//return column.;
			return null;
		}

	}

	public QueryMetadata toQuerydsl() {
		QueryDslSearch search = new QueryDslSearch(new JPAQuery());
		search.from(getRoot());
		search.where(QEmployee.employee.firstName, firstName);
		//TODO ostatni kriteria
		return search.getJpaQuery().getMetadata();
	}

	private ValueAndOperator<String> firstName;

	private ValueAndOperator<String> addressCity;

	private ValueAndOperator<Integer> yearsOfService;

	private ValueAndOperator<Date> startDate;

	private ValueAndOperator<Date> endDate;

	public void setFirstName(ValueAndOperator<String> firstName) {
		this.firstName = firstName;

	}

	public ValueAndOperator<String> getAddressCity() {
		return addressCity;
	}

	public void setAddressCity(ValueAndOperator<String> addressCity) {
		this.addressCity = addressCity;
	}

	public ValueAndOperator<Integer> getYearsOfService() {
		return yearsOfService;
	}

	public void setYearsOfService(ValueAndOperator<Integer> yearsOfService) {
		this.yearsOfService = yearsOfService;
	}

	public ValueAndOperator<Date> getStartDate() {
		return startDate;
	}

	public void setStartDate(ValueAndOperator<Date> startDate) {
		this.startDate = startDate;
	}

	public ValueAndOperator<Date> getEndDate() {
		return endDate;
	}

	public void setEndDate(ValueAndOperator<Date> endDate) {
		this.endDate = endDate;
	}

	public ValueAndOperator<String> getFirstName() {
		return firstName;
	}
}
