package com.anthavio.dao.test.vo;

import com.anthavio.dao.test.entity.Employee;
import com.mysema.query.annotations.QueryProjection;


//TODO pouzit v examplu viz http://source.mysema.com/static/querydsl/1.9.0/reference/html/ch03.html#d0e965
public class ExampleWrapper {

	private final Employee empl;
	private final String city;

	@QueryProjection
	public ExampleWrapper(Employee empl, String city) {
		this.empl = empl;
		this.city = city;
	}

	public Employee getEmpl() {
		return empl;
	}

	public String getCity() {
		return city;
	}

}
