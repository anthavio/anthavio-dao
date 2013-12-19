package net.anthavio.dao.test.entity;

import java.math.BigDecimal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author vanek
 *
 */
@Entity
@Table(name = "PROJECT_LARGE")
@DiscriminatorValue("L")
public class LargeProject extends Project {

	private BigDecimal budget;

	public LargeProject() {
		//default
	}

	public LargeProject(String name, BigDecimal budget) {
		super(name);
		this.budget = budget;
	}

	public BigDecimal getBudget() {
		return budget;
	}

	public void setBudget(BigDecimal budget) {
		this.budget = budget;
	}

}
