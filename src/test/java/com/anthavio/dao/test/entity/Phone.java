package com.anthavio.dao.test.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.anthavio.log.ToStringBuilder;

/**
 * @author vanek
 *
 */
@Entity
@Table(name = "PHONE")
@IdClass(PhonePK.class)
public class Phone {

	//prvni cast klice (je zaroven cizi klic do Employee)
	@Id
	@Column(name = "OWNER_ID")
	private Integer ownerId;

	//druha cast klice
	@Id
	@Column(name = "TYPE")
	private String type;

	@Column(name = "NUMBER")
	private String number;

	//v JPA 2 zde muze byt @Id a tim padem nemusi existovat field ownerId
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER_ID", referencedColumnName = "EMP_ID", insertable = false, updatable = false)
	private Employee owner;

	public Phone() {
		//jpa
	}

	public Phone(Integer ownerId, String type, String number) {
		this.ownerId = ownerId;
		this.type = type;
		this.number = number;
	}

	@Override
	public String toString() {
		return ToStringBuilder.toString(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Phone other = (Phone) obj;
		if (ownerId == null) {
			if (other.ownerId != null) {
				return false;
			}
		} else if (!ownerId.equals(other.ownerId)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Employee getOwner() {
		return owner;
	}

	public void setOwner(Employee owner) {
		this.owner = owner;
	}

}
