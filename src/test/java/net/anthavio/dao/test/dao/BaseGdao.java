package net.anthavio.dao.test.dao;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.jpa.JPASearchProcessor;

/**
 * @author vanek
 * 
 * Predkonfigurovane dao pro dotycny EntityManager a DataSource
 */
public class BaseGdao<T, ID extends Serializable> extends GenericDAOImpl<T, ID> {

	@Override
	@PersistenceContext(unitName = "TestUnit")
	public void setEntityManager(EntityManager entityManager) {
		super.setEntityManager(entityManager);
	}

	@Override
	@Inject
	@Named("TestSearchProcessor")
	public void setSearchProcessor(JPASearchProcessor searchProcessor) {
		super.setSearchProcessor(searchProcessor);
	}

	@Inject
	@Named("TestDataSource")
	protected DataSource dataSource;

	@Inject
	@Named("TestJdbcTemplate")
	protected JdbcTemplate jdbcTemplate;
}
