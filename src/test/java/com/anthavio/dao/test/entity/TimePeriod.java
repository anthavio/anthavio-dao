package com.anthavio.dao.test.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.anthavio.log.ToStringBuilder;

/**
 * @author vanek
 *
 */
@Embeddable
public class TimePeriod {

	@Temporal(TemporalType.DATE)
	@Column(name = "START_DATE")
	private Date startDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "END_DATE")
	private Date endDate;

	public TimePeriod() {
		//jpa
	}

	public TimePeriod(Date startDate) {
		this(startDate, null);
	}

	public TimePeriod(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
