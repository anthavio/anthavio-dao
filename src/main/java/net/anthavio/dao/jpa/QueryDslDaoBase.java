/**
 * 
 */
package net.anthavio.dao.jpa;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.LockMetadataProvider;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;

import com.googlecode.genericdao.dao.DAOUtil;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;

/**
 * @author vanek
 *
 */
public abstract class QueryDslDaoBase<T, ID extends Serializable> {

	private QueryDslJpaRepository<T, ID> delegate;

	private EntityManager entityManager;

	/**
	 * Use in tandem with init method 
	 */
	public QueryDslDaoBase() {
		//default
	}

	public QueryDslDaoBase(EntityManager entityManager) {
		init(entityManager);
	}

	public QueryDslDaoBase(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
		this.entityManager = entityManager;
		this.delegate = new QueryDslJpaRepository<T, ID>(entityInformation, entityManager);
	}

	public QueryDslDaoBase(Class<T> entityClass, EntityManager entityManager) {
		JpaEntityInformation<T, ID> ei = (JpaEntityInformation<T, ID>) JpaEntityInformationSupport.getMetadata(entityClass,
				entityManager);
		this.entityManager = entityManager;
		this.delegate = new QueryDslJpaRepository<T, ID>(ei, entityManager);
	}

	public void init(EntityManager entityManager) {
		if (this.delegate != null) {
			throw new IllegalStateException("Already initialized");
		}
		this.entityManager = entityManager;
		Class<T> entityClass = (Class<T>) DAOUtil.getTypeArguments(QueryDslDaoBase.class, this.getClass()).get(0);
		JpaEntityInformation<T, ID> ei = (JpaEntityInformation<T, ID>) JpaEntityInformationSupport.getMetadata(entityClass,
				entityManager);
		this.delegate = new QueryDslJpaRepository<T, ID>(ei, entityManager);
	}

	//gdao compatibility methods

	protected EntityManager em() {
		return entityManager;
	}

	public <S extends T> S persist(S entity) {
		em().persist(entity);
		return entity;
	}

	public T find(ID id) {
		return this.findOne(id);
	}

	public void remove(T entity) {
		em().remove(entity);
	}

	//delagated methods

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	public T findOne(Predicate predicate) {
		return delegate.findOne(predicate);
	}

	public void setLockMetadataProvider(LockMetadataProvider lockMetadataProvider) {
		delegate.setLockMetadataProvider(lockMetadataProvider);
	}

	@Override
	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	public List<T> findAll(Predicate predicate) {
		return delegate.findAll(predicate);
	}

	public List<T> findAll(Predicate predicate, OrderSpecifier<?>... orders) {
		return delegate.findAll(predicate, orders);
	}

	public Page<T> findAll(Predicate predicate, Pageable pageable) {
		return delegate.findAll(predicate, pageable);
	}

	public void delete(ID id) {
		delegate.delete(id);
	}

	public void delete(T entity) {
		delegate.delete(entity);
	}

	public long count(Predicate predicate) {
		return delegate.count(predicate);
	}

	public void delete(Iterable<? extends T> entities) {
		delegate.delete(entities);
	}

	public void deleteInBatch(Iterable<T> entities) {
		delegate.deleteInBatch(entities);
	}

	public void deleteAll() {
		delegate.deleteAll();
	}

	public void deleteAllInBatch() {
		delegate.deleteAllInBatch();
	}

	public T findOne(ID id) {
		return delegate.findOne(id);
	}

	public boolean exists(ID id) {
		return delegate.exists(id);
	}

	public List<T> findAll() {
		return delegate.findAll();
	}

	public List<T> findAll(Iterable<ID> ids) {
		return delegate.findAll(ids);
	}

	public List<T> findAll(Sort sort) {
		return delegate.findAll(sort);
	}

	public Page<T> findAll(Pageable pageable) {
		return delegate.findAll(pageable);
	}

	public T findOne(Specification<T> spec) {
		return delegate.findOne(spec);
	}

	public List<T> findAll(Specification<T> spec) {
		return delegate.findAll(spec);
	}

	public Page<T> findAll(Specification<T> spec, Pageable pageable) {
		return delegate.findAll(spec, pageable);
	}

	public List<T> findAll(Specification<T> spec, Sort sort) {
		return delegate.findAll(spec, sort);
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	public long count() {
		return delegate.count();
	}

	public long count(Specification<T> spec) {
		return delegate.count(spec);
	}

	public <S extends T> S save(S entity) {
		return delegate.save(entity);
	}

	public T saveAndFlush(T entity) {
		return delegate.saveAndFlush(entity);
	}

	public <S extends T> List<S> save(Iterable<S> entities) {
		return delegate.save(entities);
	}

	public void flush() {
		delegate.flush();
	}

}
