/**
 * 
 */
package com.anthavio.dao.aspect;

/**
 * @author vanek
 *
 * http://alexandros-karypidis.blogspot.cz/2011/06/spring-exception-translation-with.html
 * https://jira.springsource.org/browse/SPR-6282
 * 
 * Spring JpaExceptionTranslatorAspect don't know about dialects
 */
import org.springframework.dao.DataAccessException;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
 
public aspect HibernateJpaDialectExceptionTranslationAspect {
	
    pointcut entityManagerCall(): call(* javax.persistence.EntityManager.*(..)) || call(* javax.persistence..EntityManagerFactory.*(..)) || call(* javax.persistence.EntityTransaction.*(..)) || call(* javax.persistence.Query.*(..));
     
    after() throwing(RuntimeException re): entityManagerCall() {
     HibernateJpaDialect hibernateDialect = new HibernateJpaDialect();
     DataAccessException dex = hibernateDialect.translateExceptionIfPossible(re);
     if (dex != null) {
      throw dex;
     } else {
      throw re;
     }        
    } 
}
