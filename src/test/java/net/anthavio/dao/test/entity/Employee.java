package net.anthavio.dao.test.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;

import net.anthavio.log.ToStringBuilder;



/**
 * @author vanek
 *
 */
@Entity
@Table(name = "EMPLOYEE")
@SecondaryTable(name = "EMP_DATA", pkJoinColumns = @PrimaryKeyJoinColumn(name = "EMP_ID"/*EMP_DATA*/, referencedColumnName = "EMP_ID"/*EMPLOYEE*/))
public class Employee implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "EMP_ID")
	private Integer id;

	@Column(name = "F_NAME", nullable = false)
	private String firstName;

	@Column(name = "L_NAME", nullable = false)
	private String lastName;

	//read&write idcko
	@Column(name = "ADDRESS_ID", nullable = false)
	private Integer addressId;

	//readonly relace
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ADDRESS_ID", insertable = false, updatable = false)
	private Address address;

	//telefony neni podle ceho radit a proto java.util.Set misto java.util.List
	//navic to pri @OneToMany(mappedBy = "...") ani nelze
	@OneToMany(mappedBy = "owner")
	private Set<Phone> phones;

	//V JPA 2 lze i takto aniz by na Computer musela byt relace @ManyToOne
	@OneToMany
	@JoinColumn(name = "OWNER_ID", referencedColumnName = "EMP_ID")
	@OrderColumn(name = "SERIAL_NUMBER")
	private List<Computer> computers; //@OrderColumn -> lze join fetch i s dalsim List

	@OneToMany(mappedBy = "employee")
	private Set<Examination> examinations; //@OneToMany(mappedBy="...") nelze pouzit s @OrderColumn

	//@AttributeOverrides pokud jmena sloupcu nesedi
	@Embedded
	private TimePeriod period;

	//Sloupec v sekundarni tabulce
	@Column(name = "YEAR_OF_SERV", table = "EMP_DATA", nullable = false)
	private int yearsOfService;

	//Sloupec v sekundarni tabulce a navic i cizi klic
	@OneToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "MGR_ID", table = "EMP_DATA", referencedColumnName = "EMP_ID")
	private Employee manager;

	@ManyToMany
	@JoinTable(name = "EMPL_PROJ", //
	joinColumns = { @JoinColumn(name = "EMP_ID", referencedColumnName = "EMP_ID") }, //
	inverseJoinColumns = { @JoinColumn(name = "PROJ_ID", referencedColumnName = "PROJ_ID") })
	@OrderColumn(name = "PROJ_NAME")
	private List<Project> projects;//razeni v listu pomoci @OrderColumn

	//@OneToMany(mappedBy = "employee")
	//private List<Order> orders;

	public Employee() {
		//jpa
	}

	public Employee(String firstName, String lastName, Address address, TimePeriod period,
			int yearsOfService) {
		this(firstName, lastName, address.getId(), period, yearsOfService);
		this.address = address;
	}

	public Employee(String firstName, String lastName, Integer addressId, TimePeriod period,
			int yearsOfService) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.addressId = addressId;
		this.period = period;
		this.yearsOfService = yearsOfService;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Employee other = (Employee) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.toString(this);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Set<Phone> getPhones() {
		if (this.phones == null) {
			this.phones = new HashSet<Phone>();
		}
		return phones;
	}

	public void setPhones(Set<Phone> phones) {
		this.phones = phones;
	}

	public TimePeriod getPeriod() {
		return period;
	}

	public void setPeriod(TimePeriod period) {
		this.period = period;
	}

	public int getYearsOfService() {
		return yearsOfService;
	}

	public void setYearsOfService(int yearsOfService) {
		this.yearsOfService = yearsOfService;
	}

	public Employee getManager() {
		return manager;
	}

	public void setManager(Employee manager) {
		this.manager = manager;
	}

	public List<Project> getProjects() {
		if (this.projects == null) {
			this.projects = new ArrayList<Project>();
		}
		return this.projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public List<Computer> getComputers() {
		if (this.computers == null) {
			this.computers = new ArrayList<Computer>();
		}
		return computers;
	}

	public void setComputers(List<Computer> computers) {
		this.computers = computers;
	}

	public Set<Examination> getExaminations() {
		if (this.examinations == null) {
			this.examinations = new HashSet<Examination>();
		}
		return examinations;
	}

	public void setExaminations(Set<Examination> examinations) {
		this.examinations = examinations;
	}

}
