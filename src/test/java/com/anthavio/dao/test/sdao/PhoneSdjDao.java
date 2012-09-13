/**
 * 
 */
package com.anthavio.dao.test.sdao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.anthavio.dao.jpa.QueryDslDaoBase;
import com.anthavio.dao.test.entity.Phone;


/**
 * @author vanek
 *
 */
@Repository
public class PhoneSdjDao extends QueryDslDaoBase<Phone, Integer> {

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		super.init(entityManager);
	}

}
