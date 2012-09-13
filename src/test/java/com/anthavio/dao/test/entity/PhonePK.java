package com.anthavio.dao.test.entity;

import java.io.Serializable;

/**
 * @author vanek
 *
 */
public class PhonePK implements Serializable {

	private static final long serialVersionUID = 1L;

	private String type;

	private Integer ownerId;

	public PhonePK() {
	}

	public PhonePK(String type, int owner) {
		this.type = type;
		this.ownerId = owner;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof PhonePK) {
			PhonePK pk = (PhonePK) object;
			return type.equals(pk.type) && ownerId == pk.ownerId;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return type.hashCode() + ownerId;
	}

}
