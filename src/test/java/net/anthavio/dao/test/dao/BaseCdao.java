package net.anthavio.dao.test.dao;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import net.anthavio.cdao.GenericDaoJpa;

import org.springframework.jdbc.core.JdbcTemplate;



/**
 * @author vanek
 *
 */
public class BaseCdao<T, ID extends Serializable> extends GenericDaoJpa<T, ID> {

	@Override
	@PersistenceContext(unitName = "TestUnit")
	public void setEntityManager(EntityManager entityManager) {
		super.setEntityManager(entityManager);
	}

	@Inject
	@Named("TestDataSource")
	protected DataSource dataSource;

	@Inject
	@Named("TestJdbcTemplate")
	protected JdbcTemplate jdbcTemplate;
}
