package net.anthavio.dao.search.criteria;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.mysema.query.types.EntityPath;

/**
 * @author vanek
 * 
 * @param <C>
 *          table columns eumn
 */
public class ComplexCriteria<C extends Column<?>> implements Serializable {

	private static final long serialVersionUID = 1L;

	private EntityPath<?> root;

	private List<C> selectColumns; //pro renderovani

	private List<C> whereColumns; //pro vyrobu selectu

	private List<C> orderColumns; //pro vyrobu selectu

	public List<C> getOrderColumns() {
		return orderColumns == null ? new LinkedList<C>() : orderColumns;
	}

	/**
	 * TODO pokracovat v tomto ???
	 
	public List<C> getWhereColumns() {
		try {
			PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(this.getClass());
			List<C> list = new LinkedList<C>();
			for (PropertyDescriptor desc : descriptors) {
				Class<?> clazz = desc.getPropertyType();
				if (!ValueAndOperator.class.isAssignableFrom(clazz)) {
					continue;
				}
				String name = desc.getName();
				ValueAndOperator<?> value = (ValueAndOperator<?>) desc.getReadMethod().invoke(this);
				if (value == null) {
					continue;
				}
				list.add(columnByName(name));
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException("Chyba zpracovani instance: " + this.getClass().getName(), e);
		}
	}
	*/
	private C columnByName(String name) {
		for (C column : whereColumns) {
			if (column.getName().equals(name)) {
				return column;
			}
		}
		for (C column : selectColumns) {
			if (column.getName().equals(name)) {
				return column;
			}
		}
		for (C column : orderColumns) {
			if (column.getName().equals(name)) {
				return column;
			}
		}
		throw new IllegalArgumentException("Column not found: " + name);
	}

	public EntityPath<?> getRoot() {
		return root;
	}

	public void setRoot(EntityPath<?> root) {
		this.root = root;
	}
}
