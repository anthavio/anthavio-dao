package net.anthavio.dao.test;

import java.math.BigDecimal;
import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.anthavio.dao.test.dao.AddressGdao;
import net.anthavio.dao.test.dao.EmployeeGdao;
import net.anthavio.dao.test.dao.PhoneGdao;
import net.anthavio.dao.test.entity.Address;
import net.anthavio.dao.test.entity.Computer;
import net.anthavio.dao.test.entity.Employee;
import net.anthavio.dao.test.entity.Examination;
import net.anthavio.dao.test.entity.LargeProject;
import net.anthavio.dao.test.entity.Phone;
import net.anthavio.dao.test.entity.SmallProject;
import net.anthavio.dao.test.entity.order.Item;
import net.anthavio.dao.test.entity.order.Order;
import net.anthavio.dao.test.entity.order.OrderDetail;
import net.anthavio.util.DateUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;


@ContextConfiguration("classpath:spring-test.xml")
public class BaseJpaTest extends AbstractTestNGSpringContextTests {

	@Inject
	protected PlatformTransactionManager tm;

	@PersistenceContext
	protected EntityManager em;

	@Inject
	protected EmployeeGdao employeeGdao;

	@Inject
	protected AddressGdao addressGdao;

	@Inject
	protected PhoneGdao phoneGdao;

	@Value("${hibernate.dialect}")
	protected String dialect;

	protected Employee employee;

	protected void failIfNotDerby() {
		if (isDerby() == false) {
			Assert.fail("In dialect " + dialect + " this should not fail");
		}
	}

	protected void failIfDerby() {
		if (isDerby()) {
			Assert.fail("In dialect " + dialect + " this should fail");
		}
	}

	protected boolean isDerby() {
		return this.dialect.contains("Derby");
	}

	@BeforeClass
	public void insertData() {
		TransactionTemplate tt = new TransactionTemplate(tm);
		Employee empl = tt.execute(new TransactionCallback<Employee>() {
			@Override
			public Employee doInTransaction(TransactionStatus status) {

				em.createNativeQuery("DELETE FROM ITEM_TABLE").executeUpdate();
				em.createNativeQuery("DELETE FROM ORDER_DETAIL").executeUpdate();
				em.createNativeQuery("DELETE FROM ORDER_TABLE").executeUpdate();

				em.createNativeQuery("DELETE FROM PHONE").executeUpdate();
				em.createNativeQuery("DELETE FROM COMPUTER").executeUpdate();
				em.createNativeQuery("DELETE FROM EXAMINATION").executeUpdate();
				em.createNativeQuery("DELETE FROM EMPL_PROJ").executeUpdate();

				em.createNativeQuery("DELETE FROM EMP_DATA").executeUpdate();
				em.createNativeQuery("DELETE FROM EMPLOYEE").executeUpdate();
				em.createNativeQuery("DELETE FROM ADDRESS").executeUpdate();

				em.createNativeQuery("DELETE FROM PROJECT_LARGE").executeUpdate();
				em.createNativeQuery("DELETE FROM PROJECT_SMALL").executeUpdate();
				em.createNativeQuery("DELETE FROM PROJECT").executeUpdate();

				Address addressMng = new Address("Munich", "ReichStrasse");
				addressGdao.persist(addressMng);
				Employee manager = DataBuilder.buildEmployee(addressMng);
				employeeGdao.persist(manager);

				Address address = DataBuilder.buildAddress();
				addressGdao.persist(address);

				Computer computer1 = new Computer("11111");
				Computer computer2 = new Computer("22222");
				//insert v opacnem poradi nez se radi
				em.persist(computer2);
				em.persist(computer1);

				Employee employee = DataBuilder.buildEmployee("Heinrich", "Bimmler", address);
				employee.setManager(manager);
				employee.getComputers().add(computer1);
				employee.getComputers().add(computer2);
				employeeGdao.persist(employee);

				Examination exam1 = new Examination(employee.getId(), new Date(), 5);
				Examination exam2 = new Examination(employee.getId(), DateUtil.addDays(new Date(), 1), 1);
				//insert v opacnem poradi nez se radi
				em.persist(exam2);
				em.persist(exam1);

				Phone phone1 = DataBuilder.buildPhone(employee, "business");
				Phone phone2 = DataBuilder.buildPhone(employee, "private");
				//tady razeni nema smysl, telefony jsou v java.util.Set
				phoneGdao.persist(phone1);
				phoneGdao.persist(phone2);

				LargeProject largeProject = new LargeProject("Vierte Reich", new BigDecimal(5));
				em.persist(largeProject);
				largeProject.getEmployees().add(employee);
				employee.getProjects().add(largeProject);

				SmallProject smallProject = new SmallProject("Anschluss");
				em.persist(smallProject);
				smallProject.getEmployees().add(employee);
				employee.getProjects().add(smallProject);

				OrderDetail detail1 = new OrderDetail("detail1");
				Order order1 = new Order("order1");
				detail1.setOrder(order1);
				order1.setDetail(detail1);
				order1.setEmployeeId(employee.getId());
				em.persist(detail1);

				Item item1 = new Item("item1 of order1", order1.getId());
				em.persist(item1);
				Item item2 = new Item("item2 of order1", order1.getId());
				em.persist(item2);

				/*
				Order order2 = new Order("order2", new OrderDetail("y"));
				em.persist(order2);
				Item detail3 = new Item("item3 of order2", order2.getId());
				em.persist(detail3);
				Item detail4 = new Item("item4 of order2", order2.getId());
				em.persist(detail4);
				*/
				return employee;
			}
		});
		this.employee = empl;
	}
}
