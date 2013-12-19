package net.anthavio.dao.test.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.anthavio.log.ToStringBuilder;


/**
 * @author vanek
 *
 */
@Entity
@Table(name = "EXAMINATION")
public class Examination implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "EMP_ID")
	private Integer employeeId;

	@Id
	@Column(name = "DATE_OF")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateOfExam;

	@Column(name = "RESULT", nullable = false)
	private Integer result;

	//v JPA 2 zde muze byt @Id a tim padem nemusi existovat field employeeId
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EMP_ID", referencedColumnName = "EMP_ID", insertable = false, updatable = false)
	private Employee employee;

	public Examination() {
		//default
	}

	public Examination(Integer employeeId, Date dateOfExam, Integer result) {
		this.employeeId = employeeId;
		this.dateOfExam = dateOfExam;
		this.result = result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateOfExam == null) ? 0 : dateOfExam.hashCode());
		result = prime * result + ((employeeId == null) ? 0 : employeeId.hashCode());
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
		Examination other = (Examination) obj;
		if (dateOfExam == null) {
			if (other.dateOfExam != null) {
				return false;
			}
		} else if (!dateOfExam.equals(other.dateOfExam)) {
			return false;
		}
		if (employeeId == null) {
			if (other.employeeId != null) {
				return false;
			}
		} else if (!employeeId.equals(other.employeeId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.toString(this);
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Date getDateOfExam() {
		return dateOfExam;
	}

	public void setDateOfExam(Date dateOfExam) {
		this.dateOfExam = dateOfExam;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

}
