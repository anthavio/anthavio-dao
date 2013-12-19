/**
 * 
 */
package net.anthavio.dao.test.sdao;

import net.anthavio.dao.test.entity.Address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;



/**
 * @author vanek
 *
 */
public interface AddressSdjDao extends JpaRepository<Address, Integer>, QueryDslPredicateExecutor<Address> {

}
