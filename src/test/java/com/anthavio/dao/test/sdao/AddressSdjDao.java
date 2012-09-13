/**
 * 
 */
package com.anthavio.dao.test.sdao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import com.anthavio.dao.test.entity.Address;


/**
 * @author vanek
 *
 */
public interface AddressSdjDao extends JpaRepository<Address, Integer>, QueryDslPredicateExecutor<Address> {

}
