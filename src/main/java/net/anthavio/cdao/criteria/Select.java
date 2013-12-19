package net.anthavio.cdao.criteria;

public class Select {
	private boolean distinct;
	private String name;
	public boolean isDistinct() {
		return distinct;
	}
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public static Select[] select (Select... selects) {
		return selects;
	}

	public static Select select (String name, boolean distinct) {
		return new Select(name, distinct);
	}
	public static Select select (String name) {
		return new Select(name);
	}

	public Select(String name, boolean distinct) {
		this.distinct = distinct;
		this.name = name;
	}
	public Select(String name) {
		this.distinct = false;
		this.name = name;
	}
	
	public String toString() {
		return String.format("Select %s %s", this.name, this.distinct);
	}
	
}
