package net.anthavio.dao.test;

import net.anthavio.dao.test.entity.Address;
import net.anthavio.dao.test.entity.Employee;
import net.anthavio.dao.test.entity.Phone;
import net.anthavio.dao.test.entity.TimePeriod;
import net.anthavio.util.DateUtil;


/**
 * @author vanek
 *
 */
public class DataBuilder {

	public static Address buildAddress() {
		Address address = new Address("Berlin", "ReichStrasse");
		return address;
	}

	public static Phone buildPhone(Employee empl, String type) {
		Phone phone = new Phone(empl.getId(), type, "123-456-789");
		phone.setOwner(empl);
		empl.getPhones().add(phone);
		return phone;
	}

	public static Employee buildEmployee(String firstName, String lastName, Address address) {
		TimePeriod period = new TimePeriod(DateUtil.getDate(1, 1, 2000));
		Employee empl = new Employee(firstName, lastName, address.getId(), period, 0);
		empl.setAddress(address);
		address.setOwner(empl);
		return empl;
	}

	public static Employee buildEmployee(Address address) {
		return buildEmployee("Dolfy", "Hilter", address);
	}

}
