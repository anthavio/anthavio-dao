package com.anthavio.dao.test.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.anthavio.log.ToStringBuilder;


/**
 * @author vanek
 *
 */
@Entity
@Table(name = "COMPUTER")
public class Computer {

	@Id
	@GeneratedValue
	@Column(name = "COMPUTER_ID")
	private Integer id;

	@Column(name = "SERIAL_NUMBER", nullable = false)
	private String serialNumber;

	/*
		@Column(name = "OWNER_ID")
		private final Integer ownerId;

		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "OWNER_ID", referencedColumnName = "EMP_ID", insertable = false, updatable = false)
		private Employee owner;

		public Computer(String serialNumber, Integer ownerId) {
			this.serialNumber = serialNumber;
			this.ownerId = ownerId;
		}
	*/

	public Computer() {
		//default
	}

	public Computer(String serialNumber) {
		this.serialNumber = serialNumber;
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
		Computer other = (Computer) obj;
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

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

}
