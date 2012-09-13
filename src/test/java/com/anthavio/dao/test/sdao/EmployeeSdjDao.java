/**
 * 
 */
package com.anthavio.dao.test.sdao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import com.anthavio.dao.test.entity.Employee;


/**
 * @author vanek
 *
 */
public interface EmployeeSdjDao extends JpaRepository<Employee, Integer>, QueryDslPredicateExecutor<Employee> {

}
