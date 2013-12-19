/**
 * 
 */
package net.anthavio.dao.test.sdao;

import net.anthavio.dao.test.entity.Employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;



/**
 * @author vanek
 *
 */
public interface EmployeeSdjDao extends JpaRepository<Employee, Integer>, QueryDslPredicateExecutor<Employee> {

}
