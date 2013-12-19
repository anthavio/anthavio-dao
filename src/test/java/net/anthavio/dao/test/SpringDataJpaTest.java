/**
 * 
 */
package net.anthavio.dao.test;

import static org.fest.assertions.api.Assertions.assertThat;

import javax.inject.Inject;

import net.anthavio.dao.test.entity.Address;
import net.anthavio.dao.test.entity.Employee;
import net.anthavio.dao.test.entity.Phone;
import net.anthavio.dao.test.entity.QAddress;
import net.anthavio.dao.test.sdao.AddressSdjDao;
import net.anthavio.dao.test.sdao.EmployeeSdjDao;
import net.anthavio.dao.test.sdao.PhoneSdjDao;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

/**
 * @author vanek
 * 
 */
@ContextConfiguration("classpath:spring-test.xml")
public class SpringDataJpaTest extends AbstractTestNGSpringContextTests {

	@Inject
	protected EmployeeSdjDao employeeDao;

	@Inject
	protected AddressSdjDao addressDao;

	@Inject
	private PhoneSdjDao phoneDao;

	@Test
	@Transactional
	public void test() {
		Address address = DataBuilder.buildAddress();
		addressDao.save(address);

		QAddress qa = QAddress.address;

		Address one = addressDao.findOne(qa.street.eq(address.getStreet()).and(qa.id.eq(address.getId())));
		assertThat(one.getId()).isEqualTo(address.getId());

		Employee empl = DataBuilder.buildEmployee(address);
		employeeDao.save(empl);

		Phone phone = DataBuilder.buildPhone(empl, "private");
		phoneDao.save(phone);

	}
}
