/**
 * 
 */
package net.anthavio.dao.test.sdao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.anthavio.dao.jpa.QueryDslDaoBase;
import net.anthavio.dao.test.entity.Phone;

import org.springframework.stereotype.Repository;



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
