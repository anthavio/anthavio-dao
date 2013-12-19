package net.anthavio.dao.test.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import net.anthavio.log.ToStringBuilder;


/**
 * @author vanek
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "PROJ_TYPE", discriminatorType = DiscriminatorType.STRING, length = 20)
@Table(name = "PROJECT")
public class Project {

	@Id
	@GeneratedValue
	@Column(name = "PROJ_ID")
	private Integer id;

	@Column(name = "PROJ_NAME")
	private String name;

	@ManyToMany(mappedBy = "projects")
	private List<Employee> employees;

	public Project() {
		//jpa
	}

	public Project(String name) {
		super();
		this.name = name;
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
		Project other = (Project) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Employee> getEmployees() {
		if (this.employees == null) {
			this.employees = new ArrayList<Employee>();
		}
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
