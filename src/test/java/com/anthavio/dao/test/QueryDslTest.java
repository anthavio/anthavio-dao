package com.anthavio.dao.test;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.hibernate.LazyInitializationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.jpa.JpaSystemException;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.anthavio.aspect.ApiPolicyOverride;
import com.anthavio.dao.search.PagedCriteria;
import com.anthavio.dao.search.PagedResult;
import com.anthavio.dao.search.QueryDslSearch;
import com.anthavio.dao.search.criteria.ColumnCriteria;
import com.anthavio.dao.search.criteria.CompareColumnCriteria;
import com.anthavio.dao.search.criteria.NumberColumnCriteria;
import com.anthavio.dao.search.criteria.Operator;
import com.anthavio.dao.search.criteria.StringColumnCriteria;
import com.anthavio.dao.search.criteria.ValueAndOperator;
import com.anthavio.dao.test.entity.Employee;
import com.anthavio.dao.test.entity.Phone;
import com.anthavio.dao.test.entity.QEmployee;
import com.anthavio.dao.test.entity.QPhone;
import com.anthavio.dao.test.entity.order.Order;
import com.anthavio.dao.test.entity.order.QOrder;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.path.ComparablePath;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.SimplePath;

/**
 * @author vanek
 *
 */
@ApiPolicyOverride
public class QueryDslTest extends BaseJpaTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testEntityFetchJoin() {
		Integer orderId = (Integer) em.createQuery("SELECT min(o.id) FROM Order o").getSingleResult();

		//select without joining lazy load Employee and Items
		Query query = em.createQuery("SELECT o FROM Order o WHERE o.id = ?1");
		List<Order> list = query.setParameter(1, orderId).getResultList();
		assertThat(list.size()).isEqualTo(1);
		Order order = list.get(0);

		try {
			order.getItems().size();
			Assert.fail("This should throw LazyInitializationException");
		} catch (LazyInitializationException lix) {
			//ok
		}

		try {
			order.getEmployee().getFirstName();
			Assert.fail("This should throw LazyInitializationException");
		} catch (LazyInitializationException lix) {
			//ok
		}

		//JOIN FETCH lazy load employee
		query = em.createQuery("SELECT o FROM Order o JOIN FETCH o.employee WHERE o.id = ?1");
		list = query.setParameter(1, orderId).getResultList();
		assertThat(list.size()).isEqualTo(1);
		order = list.get(0);
		try {
			order.getItems().size();
			Assert.fail("This should throw LazyInitializationException");
		} catch (LazyInitializationException lix) {
			//ok
		}
		assertThat(order.getEmployee().getFirstName()).isNotNull()
				.overridingErrorMessage("Order has Employee fetch joined");

		//simple SELECT COUNT
		query = em.createQuery("SELECT COUNT(o) FROM Order o WHERE o.id = ?1");
		Long count = (Long) query.setParameter(1, orderId).getSingleResult();
		assertThat(count).isEqualTo(1L); //easy

		//COUNT and JOIN makes sense again
		query = em.createQuery("SELECT COUNT(o) FROM Order o JOIN o.employee WHERE o.id = ?1");
		count = (Long) query.setParameter(1, orderId).getSingleResult();
		assertThat(count).isEqualTo(1L); //easy

		//COUNT and JOIN FETCH does not make sense
		try {
			query = em.createQuery("SELECT COUNT(o) FROM Order o JOIN FETCH o.employee WHERE o.id = ?1");
		} catch (IllegalArgumentException iax) {
			assertThat(iax.getMessage()).contains("owner of the fetched association was not present");
		} catch (InvalidDataAccessApiUsageException idux) {
			//spring exception transformed
			assertThat(idux.getMessage()).contains("owner of the fetched association was not present");
		}

		//QueryDslSearch search is clever and strips fetches from count select 
		//and returns them back in data select
		QueryDslSearch search = new QueryDslSearch(em);
		QOrder qo = QOrder.order;
		search.from(qo);
		search.joinFetch(qo.detail());
		search.eq(qo.id, orderId);
		PagedCriteria pc = new PagedCriteria();
		PagedResult<Order> result = search.listAllPaged(qo, pc);
		assertThat(result.getResults().size()).isEqualTo(1);
		assertThat(result.getTotal()).isEqualTo(1L);
		//assertThat("Results", result.getResults().size(), equalTo(1));
		//assertThat("Count", result.getTotal(), equalTo(1L));
	}

	/**
	 * Completely custom query 
	 */
	public static <T> List<T> findCustom(EntityManager em, Class<T> entityClass, Map<String, ?> filters, String sort) {
		EntityPath<T> entityPath = new EntityPathBase<T>(entityClass, "entity");
		BooleanBuilder builder = new BooleanBuilder();
		for (Map.Entry<String, ?> entry : filters.entrySet()) {
			if (entry.getValue() != null) {
				SimplePath<Object> property = new SimplePath<Object>(entry.getValue().getClass(), entityPath, entry.getKey());
				builder.and(property.eq(entry.getValue()));
			}
		}
		ComparablePath<?> sortProperty = new ComparablePath(Comparable.class, entityPath, sort);
		return new JPAQuery(em).from(entityPath).where(builder.getValue()).orderBy(sortProperty.asc()).list(entityPath);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCollectionFetchJoin() {
		//http://stackoverflow.com/questions/592825/jpa-please-help-understanding-join-fetch/592926#592926

		Integer orderId = (Integer) em.createQuery("SELECT min(o.id) FROM Order o").getSingleResult();

		//collection fetch without select distinct -> multiple root is in result
		String jpaQl = "SELECT o FROM Order o JOIN FETCH o.items" //
				+ " WHERE o.id = ?1";
		Query query = em.createQuery(jpaQl).setParameter(1, orderId);

		List<Order> list = query.getResultList();

		assertThat(list.size()).isEqualTo(2).overridingErrorMessage("Without DISTINCT duplicity should be returned");
		Order order = list.get(0);
		assertThat(order.getItems().size()).isEqualTo(2).overridingErrorMessage("Order has 2 Items");

		//collection fetch without select distinct with QueryDslSearch
		QOrder qo = QOrder.order;
		QueryDslSearch search = new QueryDslSearch(em);
		search.from(qo);
		search.joinFetch(qo.items);
		search.eq(qo.id, orderId);
		PagedCriteria pc = new PagedCriteria();
		PagedResult<Order> result = search.listPaged(qo, pc); //no distinct

		assertThat(result.getResults().size()).isEqualTo(2).overridingErrorMessage(
				"Without DISTINCT duplicity should be returned");
		assertThat(result.getTotal()).isEqualTo(2L).overridingErrorMessage("Without DISTINCT duplicity should be returned");
		order = result.getResults().get(0);
		assertThat(order.getItems().size()).isEqualTo(2).overridingErrorMessage("Order has 2 Items");
	}

	@Test
	public void testComplexFetchJoins() {
		//use DISTINCT
		String jpaQl = "SELECT DISTINCT e FROM Employee e"// 
				+ " JOIN FETCH e.address" //
				+ " JOIN FETCH e.manager"// 
				+ " JOIN FETCH e.phones"//
				+ " JOIN FETCH e.computers"//
				+ " JOIN FETCH e.projects" // 
				+ " JOIN FETCH e.examinations" //
				+ " WHERE e.id = ?1";
		Query query = em.createQuery(jpaQl);
		query.setParameter(1, employee.getId());

		@SuppressWarnings("unchecked")
		List<Employee> list = query.getResultList();
		for (Employee employee : list) {
			System.out.println(employee);
		}
		assertThat(list.size()).isEqualTo(1);
		Employee e = list.get(0);
		System.out.println(e.getPhones());
		assertThat(e.getPhones().size()).isEqualTo(2);
		System.out.println(e.getProjects());
		assertThat(e.getProjects().size()).isEqualTo(2);
		System.out.println(e.getManager());
		System.out.println(e.getComputers());
		assertThat(e.getComputers().size()).isEqualTo(2);
		System.out.println(e.getExaminations());
		assertThat(e.getExaminations().size()).isEqualTo(2);
	}

	@Test
	public void testCompositePkCount() {
		Query query = em.createQuery("SELECT COUNT(*) FROM Phone p"); //COUNT(*) umi jen hibernate
		Long count = (Long) query.getSingleResult();
		assertThat(count).isEqualTo(2L);

		//ORACLE, DERBY don't like COUNT(a,b,...) 
		//In Hibernate 4+ it works!
		try {
			query = em.createQuery("SELECT COUNT(p) FROM Phone p");
			count = (Long) query.getSingleResult();
			//failIfDerby();
		} catch (PersistenceException px) {
			failIfNotDerby();
		} catch (JpaSystemException jpax) {
			//spring exception transformed
			failIfNotDerby();
		}

		//QueryDslSearch supports SELECT COUNT(*)
		QPhone p = QPhone.phone;
		QueryDslSearch search = new QueryDslSearch(em);
		search.from(p);
		assertThat(search.countAll()).isEqualTo(2L);

		//Even in 
		PagedCriteria criteria = new PagedCriteria();
		PagedResult<Phone> result = search.listAllPaged(p, criteria);
		assertThat(result.getTotal()).isEqualTo(2L);
		assertThat(result.getResults().size()).isEqualTo(2);
	}

	@Test
	public void testLongPath() {
		Query query = em.createQuery("SELECT p FROM Phone p WHERE p.owner.address = ?1");
		query.setParameter(1, employee.getAddress());
		int count = query.getResultList().size();
		assertThat(count).isEqualTo(2);

		QPhone p = QPhone.phone;
		QueryDslSearch search = new QueryDslSearch(em);
		search.from(p);
		search.eq(p.owner().address(), employee.getAddress());
		assertThat(search.list(p).size()).isEqualTo(2);

		JPAQuery jpq = new JPAQuery(em);
		jpq.from(p);
		jpq.where(p.owner().address().eq(employee.getAddress()));
		assertThat(jpq.list(p).size()).isEqualTo(2);
	}

	@Test
	public void testDirectCriteria() {
		QEmployee e = QEmployee.employee;
		QueryDslSearch search = new QueryDslSearch(em);
		search.from(e);
		//join fetches
		/*
		search.joinFetch(e.address());
		search.joinFetch(e.manager());
		search.joinFetch(e.phones);
		search.joinFetch(e.computers);
		search.joinFetch(e.projects);
		search.joinFetch(e.examinations);
		*/
		//string
		search.eq(e.firstName, employee.getFirstName());
		//integer
		search.eq(e.yearsOfService, employee.getYearsOfService());
		//date
		search.eq(e.period().startDate, employee.getPeriod().getStartDate());
		search.eq(e.period().endDate, employee.getPeriod().getEndDate());
		//entity path
		search.eq(e.address().city, employee.getAddress().getCity());
		search.eq(e.manager(), employee.getManager());
		//multivalue
		search.eq(e.addressId, employee.getAddress().getId(), employee.getAddress().getId());

		search.eq(e.addressId, null);
		search.eq(e.addressId, null, null);
		search.eq(e.addressId, null, employee.getAddressId(), null);

		search.ne(e.addressId, null);
		search.ne(e.addressId, null, null);
		search.ne(e.addressId, -1);
		search.ne(e.addressId, -1, -2);
		search.ne(e.addressId, null, -1, null);

		search.eq(e.firstName, null);
		search.eq(e.firstName, null, null);
		search.eq(e.firstName, null, employee.getFirstName(), null);

		search.ne(e.firstName, null);
		search.ne(e.firstName, null, null);
		search.ne(e.firstName, "x");
		search.ne(e.firstName, "x", "y");
		search.ne(e.firstName, null, "x", null);

		assertThat(search.countAll()).isEqualTo(1L);
	}

	@Test
	public void testSimpleCriteria() {
		QEmployee e = QEmployee.employee;
		QueryDslSearch search = new QueryDslSearch(em);
		search.from(e);
		//join fetches
		/*
		search.joinFetch(e.address());
		search.joinFetch(e.manager());
		search.joinFetch(e.phones);
		search.joinFetch(e.computers);
		search.joinFetch(e.projects);
		search.joinFetch(e.examinations);
		*/
		//string
		search.where(e.firstName, new ValueAndOperator<String>(employee.getFirstName()));
		//integer
		search.where(e.yearsOfService, new ValueAndOperator<Integer>(employee.getYearsOfService()));
		//dates
		search.where(e.period().startDate, new ValueAndOperator<Date>(employee.getPeriod().getStartDate()));
		search.where(e.period().endDate, new ValueAndOperator<Date>(employee.getPeriod().getEndDate()));
		//entity path
		search.where(e.address().city, new ValueAndOperator<String>(employee.getAddress().getCity()));
		search.where(e.manager(), new ValueAndOperator<Employee>(employee.getManager()));

		//multivalue
		search.where(e.addressId, new ValueAndOperator<Integer>(employee.getAddress().getId(), employee.getAddress()
				.getId()));

		search.where(e.addressId, new ValueAndOperator<Integer>(null));
		search.where(e.addressId, new ValueAndOperator<Integer>(null, null));
		search.where(e.addressId, new ValueAndOperator<Integer>(Operator.NEQ, -1));
		search.where(e.addressId, new ValueAndOperator<Integer>(Operator.NEQ, -1, -2));
		search.where(e.addressId, new ValueAndOperator<Integer>(Operator.NEQ, null));
		search.where(e.addressId, new ValueAndOperator<Integer>(Operator.NEQ, null, null));
		search.where(e.addressId, new ValueAndOperator<Integer>(Operator.NEQ, null, -1, null));

		search.where(e.lastName, new ValueAndOperator<String>(null));
		search.where(e.lastName, new ValueAndOperator<String>(null, null));
		search.where(e.lastName, new ValueAndOperator<String>(Operator.NEQ, "x"));
		search.where(e.lastName, new ValueAndOperator<String>(Operator.NEQ, "x", "y"));
		search.where(e.lastName, new ValueAndOperator<String>(Operator.NEQ, null));
		search.where(e.lastName, new ValueAndOperator<String>(Operator.NEQ, null, null));
		search.where(e.lastName, new ValueAndOperator<String>(Operator.NEQ, null, "x", null));

		assertThat(search.countAll()).isEqualTo(1L);
	}

	@Test
	public void testColumnCriteria() {
		QEmployee e = QEmployee.employee;
		QueryDslSearch search = new QueryDslSearch(em);
		search.from(e);
		//join fetches
		/*
		search.joinFetch(e.address());
		search.joinFetch(e.manager());
		search.joinFetch(e.phones);
		search.joinFetch(e.computers);
		search.joinFetch(e.projects);
		search.joinFetch(e.examinations);
		*/
		//string
		search.where(new StringColumnCriteria(e.firstName, employee.getFirstName()));
		//integer
		search.where(new NumberColumnCriteria<Integer>(e.yearsOfService, employee.getYearsOfService()));
		//dates
		search.where(new CompareColumnCriteria<Date>(e.period().startDate, employee.getPeriod().getStartDate()));
		search.where(new CompareColumnCriteria<Date>(e.period().endDate, employee.getPeriod().getEndDate()));

		//entity path
		search.where(new StringColumnCriteria(e.address().city, employee.getAddress().getCity()));
		search.where(new ColumnCriteria<Employee>(e.manager(), employee.getManager()));

		search.where(new NumberColumnCriteria<Integer>(e.addressId, Operator.GT, -1));
		search.where(new CompareColumnCriteria<Date>(e.period().startDate, Operator.GOE, employee.getPeriod()
				.getStartDate()));

		search.where(new NumberColumnCriteria<Integer>(e.addressId, employee.getAddress().getId(), employee.getAddress()
				.getId()));

		search.where(new NumberColumnCriteria<Integer>(e.addressId, null));
		search.where(new NumberColumnCriteria<Integer>(e.addressId, null, null));
		search.where(new NumberColumnCriteria<Integer>(e.addressId, Operator.NEQ, -1));
		search.where(new NumberColumnCriteria<Integer>(e.addressId, Operator.NEQ, -1, -2));
		search.where(new NumberColumnCriteria<Integer>(e.addressId, Operator.NEQ, null));
		search.where(new NumberColumnCriteria<Integer>(e.addressId, Operator.NEQ, null, null));
		search.where(new NumberColumnCriteria<Integer>(e.addressId, Operator.NEQ, null, -1, null));

		search.where(new StringColumnCriteria(e.lastName, null));
		search.where(new StringColumnCriteria(e.lastName, null, null));
		search.where(new StringColumnCriteria(e.lastName, Operator.NEQ, "x"));
		search.where(new StringColumnCriteria(e.lastName, Operator.NEQ, "x", "y"));
		search.where(new StringColumnCriteria(e.lastName, Operator.NEQ, null));
		search.where(new StringColumnCriteria(e.lastName, Operator.NEQ, null, null));
		search.where(new StringColumnCriteria(e.lastName, Operator.NEQ, null, "x", null));

		assertThat(search.countAll()).isEqualTo(1L);
	}

	@Test
	public void testComplexCriteria() {
		//TODO dokoncit test
		EmployeeSearchCriteria criteria = new EmployeeSearchCriteria();
		criteria.setFirstName(new ValueAndOperator<String>(employee.getFirstName()));
		criteria.setAddressCity(new ValueAndOperator<String>(employee.getAddress().getCity()));
		criteria.setYearsOfService(new ValueAndOperator<Integer>(employee.getYearsOfService()));
		criteria.setStartDate(new ValueAndOperator<Date>(employee.getPeriod().getStartDate()));
		criteria.setEndDate(new ValueAndOperator<Date>(employee.getPeriod().getEndDate()));
		QueryDslSearch search = new QueryDslSearch(em);

	}

}
