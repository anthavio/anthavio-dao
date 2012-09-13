package com.anthavio.dao.test;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.anthavio.aspect.ApiPolicyOverride;
import com.anthavio.dao.test.dao.AddressCdao;
import com.anthavio.dao.test.dao.AddressGdao;
import com.anthavio.dao.test.dao.EmployeeCdao;
import com.anthavio.dao.test.dao.EmployeeGdao;
import com.anthavio.dao.test.dao.PhoneCdao;
import com.anthavio.dao.test.dao.PhoneGdao;
import com.anthavio.dao.test.entity.Address;
import com.anthavio.dao.test.entity.Employee;
import com.anthavio.dao.test.entity.Phone;

/**
 * @author vanek
 *
 */
@ApiPolicyOverride
@ContextConfiguration("classpath:spring-test.xml")
public class SimpleTest extends AbstractTestNGSpringContextTests {
	//AbstractTransactionalJUnit4SpringContextTests

	@Inject
	private PlatformTransactionManager transactionManager;

	@Inject
	private EmployeeGdao employeeGdao;
	@Inject
	private EmployeeCdao employeeCdao;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private AddressGdao addressGdao;
	@Inject
	private AddressCdao addressCdao;

	@Inject
	private PhoneGdao phoneGdao;
	@Inject
	private PhoneCdao phoneCdao;

	@Test
	public void testExceptionTranslationDao() {
		try {
			persistDao();
		} catch (PersistenceException jpx) {
			Assert.fail("Exception translation does not work in DAO class", jpx);
		} catch (JpaSystemException spx) {
			Assert.fail("Exception translation does not work in direct JPA call - Hibernate dialect is not used", spx);
		} catch (DataIntegrityViolationException x) {
			//hurray !
		}
	}

	@Transactional
	private void persistDao() {
		employeeGdao.persist(new Employee());
	}

	/*
	 *XXX: Neprojde pri buildu mavenem protoze ten nejspis neaplikuje HibernateJpaDialectExceptionTranslationAspect.aj na test classy
	@Test
	public void testExceptionTranslationJpa() {
		try {
			persistJpa();
		} catch (PersistenceException jpx) {
			Assert.fail("Exception translation does not work in direct JPA call", jpx);
		} catch (DataIntegrityViolationException x) {
			//hurray - HibernateJpaDialectExceptionTranslationAspect
		} catch (DataAccessException sdx) {
			//happens with spring default JpaExceptionTranslatorAspect
			Assert.fail("Exception translation does not work in direct JPA call - Hibernate dialect is not used", sdx);
		}
	}

	@Transactional
	private void persistJpa() {
		em.persist(new Employee());
	}
	*/

	@Test
	public void testCdao() {
		TransactionTemplate tt = new TransactionTemplate(transactionManager);
		tt.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Address address = DataBuilder.buildAddress();
				addressCdao.persist(address);

				Employee empl = DataBuilder.buildEmployee(address);
				employeeCdao.persist(empl);

				Phone phone = DataBuilder.buildPhone(empl, "private");
				phoneCdao.persist(phone);

			}
		});

		List<Employee> all = employeeCdao.findAll();
		for (Employee employee : all) {
			System.out.println(employee);
		}
	}

	@Test
	public void testGdao() {
		TransactionTemplate tt = new TransactionTemplate(transactionManager);
		tt.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Address address = DataBuilder.buildAddress();
				addressGdao.persist(address);

				Employee empl = DataBuilder.buildEmployee(address);
				employeeGdao.persist(empl);
				employeeGdao.find(empl.getId());

				Phone phone = DataBuilder.buildPhone(empl, "private");
				phoneGdao.persist(phone);

			}
		});
		List<Employee> all = employeeGdao.findAll();
		for (Employee employee : all) {
			System.out.println(employee);
		}
	}

}
