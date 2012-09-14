package com.anthavio.cdao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anthavio.cdao.criteria.Between;
import com.anthavio.cdao.criteria.Comparison;
import com.anthavio.cdao.criteria.Criterion;
import com.anthavio.cdao.criteria.Group;
import com.anthavio.cdao.criteria.Operator;
import com.anthavio.cdao.criteria.VerifiedBetween;


public class GenericDaoUtils {

	protected static Logger logger = LoggerFactory.getLogger(GenericDaoUtils.class);

	public static boolean hasId(Object entity, String idPropertyName) {
		/*
		BeanWrapper bw = new BeanWrapperImpl(entity);
		Object propertyValue = bw.getPropertyValue(idPropertyName);
		if (propertyValue == null) {
			logger.debug("hasId: no id was found returning false");
			return false;
		}
		*/
		String getName = "get" + Character.toUpperCase(idPropertyName.charAt(0))
				+ idPropertyName.substring(1);

		Method method;
		try {
			method = entity.getClass().getMethod(getName, (Class[]) null);
		} catch (SecurityException sx) {
			throw new IllegalStateException("Security manager is turned on", sx);
		} catch (NoSuchMethodException nmx) {
			throw new IllegalArgumentException(idPropertyName + " id property getter " + getName
					+ " not found on entity " + entity.getClass(), nmx);
		}
		Object propertyValue;
		try {
			propertyValue = method.invoke(entity, (Object[]) null);
		} catch (IllegalAccessException iax) {
			throw new IllegalArgumentException("Illegal access to " + getName + " on entity "
					+ entity.getClass(), iax);
		} catch (InvocationTargetException itx) {
			throw new IllegalArgumentException("Exception while invoking " + getName + " on entity "
					+ entity.getClass(), itx);
		}

		if (propertyValue == null) {
			logger.debug("hasId: no id was found returning false");
			return false;
		}

		if (propertyValue instanceof Number) {
			//boolean isPrimitive = bw.getPropertyType(idPropertyName).isPrimitive();
			boolean isPrimitive = method.getReturnType().isPrimitive();

			Number number = (Number) propertyValue;
			if (isPrimitive && number.longValue() <= 0) {
				logger
						.debug("hasId: an id was found and it was a number, but it was less than zero returning false");
				return false;
			} else {
				logger.debug("hasId: an id was found and it was a number returning true");
				return true;
			}
		}

		return true;
	}

	public static String searchFieldsForPK(Class<?> aType) {
		String pkName = null;
		Field[] fields = aType.getDeclaredFields();
		for (Field field : fields) {
			Id id = field.getAnnotation(Id.class);
			if (id != null) {
				pkName = field.getName();
				break;
			}
		}
		if (pkName == null && aType.getSuperclass() != null) {
			pkName = searchFieldsForPK(aType.getSuperclass());
		}
		return pkName;
	}

	public static void addGroupParams(Query query, Group group, Set<String> names) {

		if (names == null) {
			names = new HashSet<String>();
		}
		for (Criterion criterion : group) {
			if (criterion instanceof Group) {
				addGroupParams(query, (Group) criterion, names);
			} else {
				Comparison comparison = (Comparison) criterion;

				if (comparison.isObjectIdentity()) {
					continue;
				}

				String name = CriteriaUtils.ditchDot(comparison.getName());

				name = CriteriaUtils.ensureUnique(names, name);

				if (comparison.getValue() != null) {
					final String sOperator = comparison.getOperator().getOperator();
					if (!"like".equalsIgnoreCase(sOperator)) {
						if (comparison instanceof Between) {
							Between between = (Between) comparison;
							query.setParameter(name + "_1", comparison.getValue());
							query.setParameter(name + "_2", between.getValue2());
						} else if (comparison instanceof VerifiedBetween) {
							VerifiedBetween between = (VerifiedBetween) comparison;
							query.setParameter(name + "_1", comparison.getValue());
							query.setParameter(name + "_2", between.getValue2());
						} else {
							query.setParameter(name, comparison.getValue());
						}

					} else {
						Operator operator = comparison.getOperator();
						StringBuilder value = new StringBuilder(50);
						if (operator == Operator.LIKE) {
							value.append(comparison.getValue());
						} else if (operator == Operator.LIKE_CONTAINS) {
							value.append("%").append(comparison.getValue()).append("%");
						} else if (operator == Operator.LIKE_END) {
							value.append("%").append(comparison.getValue());
						} else if (operator == Operator.LIKE_START) {
							value.append(comparison.getValue()).append("%");
						}
						if (logger.isDebugEnabled()) {
							logger.debug("parameters");
							logger.debug("value name = " + comparison.getName());
							logger.debug("value value = " + value);
						}
						query.setParameter(name, value.toString());
					}
				}
			}
		}
	}

	public static String getEntityName(Class<?> aType) {
		Entity entity = aType.getAnnotation(Entity.class);
		if (entity == null) {
			return aType.getSimpleName();
		}
		String entityName = entity.name();

		if (entityName == null) {
			return aType.getSimpleName();
		} else if (!(entityName.length() > 0)) {
			return aType.getSimpleName();
		} else {
			return entityName;
		}

	}

	public static String searchMethodsForPK(Class<?> aType) {
		String pkName = null;
		Method[] methods = aType.getDeclaredMethods();
		for (Method method : methods) {
			Id id = method.getAnnotation(Id.class);
			if (id != null) {
				pkName = method.getName().substring(4);
				pkName = method.getName().substring(3, 4).toLowerCase() + pkName;
				break;
			}
		}
		if (pkName == null && aType.getSuperclass() != null) {
			pkName = searchMethodsForPK(aType.getSuperclass());
		}
		return pkName;
	}

}
