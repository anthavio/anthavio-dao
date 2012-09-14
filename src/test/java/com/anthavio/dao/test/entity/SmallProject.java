package com.anthavio.dao.test.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author vanek
 *
 */
@Entity
@Table(name = "PROJECT_SMALL")
@DiscriminatorValue("S")
public class SmallProject extends Project {

	public SmallProject() {
		//default
	}

	public SmallProject(String name) {
		super(name);
	}

}
