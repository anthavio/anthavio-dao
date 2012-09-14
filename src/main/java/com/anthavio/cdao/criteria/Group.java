package com.anthavio.cdao.criteria;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Group extends Criterion implements Iterable<Criterion>{
	protected List<Criterion> criteria = new ArrayList<Criterion>();
	protected Junction junction = Junction.AND;
	
	public Group () {
		
	}
    
    public void clear () {
        criteria.clear();
    }
	
	public Group (final Junction aJunction, final Criterion... aCriteria) {
		this.junction = aJunction;
		
		for (Criterion criterion : aCriteria) {
			add(criterion);
		}
	}

	public static String [] orderBy (String... orderBy) {
		return orderBy;
	}

	public static Group and (Class<?> baseType) {
		VerifiedGroup group =  new VerifiedGroup(); 
		group.junction = Junction.AND;
		return group;
	}

	public static Group or (Class<?> baseType) {
		VerifiedGroup group =  new VerifiedGroup();
		group.junction = Junction.OR;
		return group;
	}
	
	public static Group and (Class<?> baseType, final Criterion... criteria) {
		return  new VerifiedGroup(baseType, Junction.AND, criteria);
	}

	public static Group and (Class<?> baseType, final Map <String, Object> map) {
		VerifiedGroup group =  new VerifiedGroup(); group.setJunction(Junction.AND);
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			group.add(Comparison.eq(entry.getKey(), entry.getValue()));
		}
		return group;
	}

	public static Group or (Class<?> baseType, final Map <String, Object> map) {
		VerifiedGroup group =  new VerifiedGroup(); group.setJunction(Junction.OR);
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			group.add(Comparison.eq(entry.getKey(), entry.getValue()));
		}
		return group;
	}

	public static Group and (Class<?> baseType, final Map <String, Object> map, Operator operator) {
		VerifiedGroup group =  new VerifiedGroup(); group.setJunction(Junction.AND);
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			group.add(new Comparison(entry.getKey(), operator, entry.getValue()));
		}
		return group;
	}

	public static Group or (Class<?> baseType, final Map <String, Object> map, Operator operator) {
		VerifiedGroup group =  new VerifiedGroup(); group.setJunction(Junction.OR);
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			group.add(new Comparison(entry.getKey(), operator, entry.getValue()));
		}
		return group;
	}

	public static Group or (Class<?> baseType, final Criterion... criteria) {
		return  new Group(Junction.OR, criteria);
	}

	public static Group and () {
		Group group =  new Group(); group.junction = Junction.AND;
		return group;
	}

	public static Group or () {
		Group group =  new Group(); group.junction = Junction.OR;
		return group;
	}
	
	public static Group and (final Criterion... criteria) {
		return  new Group(Junction.AND, criteria);
	}

	public static Group and (final Map <String, Object> map) {
		Group group = new Group(); group.setJunction(Junction.AND);
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			group.add(Comparison.eq(entry.getKey(), entry.getValue()));
		}
		return group;
	}

	public static Group or (final Map <String, Object> map) {
		Group group = new Group(); group.setJunction(Junction.OR);
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			group.add(Comparison.eq(entry.getKey(), entry.getValue()));
		}
		return group;
	}

	public static Group and (final Map <String, Object> map, Operator operator) {
		Group group = new Group(); group.setJunction(Junction.AND);
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			group.add(new Comparison(entry.getKey(), operator, entry.getValue()));
		}
		return group;
	}

	public static Group or (final Map <String, Object> map, Operator operator) {
		Group group = new Group(); group.setJunction(Junction.OR);
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			group.add(new Comparison(entry.getKey(), operator, entry.getValue()));
		}
		return group;
	}

	public static Group or (final Criterion... criteria) {
		return  new Group(Junction.OR, criteria);
	}

	public Group add (Criterion criterion) {
		criteria.add(criterion);
		return this;
	}

	public Group add (String name, Operator operator, Object value) {
		add(new Comparison(name, operator, value));
		return this;
	}

	public Group between (String name, Object value1, Object value2) {
		add(new Between(name, value1, value2));
		return this;
	}

	public Group eq (String name, Object value) {
		add(new Comparison(name, Operator.EQ, value));
		return this;
	}
	public Group ne (String name, Object value) {
		add(new Comparison(name, Operator.NE, value));
		return this;
	}
	public Group gt (String name, Object value) {
		add(new Comparison(name, Operator.GT, value));
		return this;
	}
	public Group lt (String name, Object value) {
		add(new Comparison(name, Operator.LT, value));
		return this;
	}
	public Group ge (String name, Object value) {
		add(new Comparison(name, Operator.GE, value));
		return this;
	}
	public Group le (String name, Object value) {
		add(new Comparison(name, Operator.LE, value));
		return this;
	}
		
	public String toString () {
		return "(" + junction + " " + criteria + ")";
	}

	public Iterator<Criterion> iterator() {
		return criteria.iterator();
	}
	
	public int size () {
		return criteria.size();
	}

	public Junction getJunction() {
		return junction;
	}

	public void setJunction(Junction junction) {
		this.junction = junction;
	}
	
}
	