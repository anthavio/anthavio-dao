package net.anthavio.cdao.join;

public class Join {

	public Join() {
	}

	public static EntityJoin entityJoin(String entityName, String alias) {
		return new EntityJoin(entityName, alias);
	}

	public static Join join(String property) {
		return new SimpleRelationshipJoin(JoinType.RIGHT, property);
	}

	public static Join join(String property, boolean aliasedRelationship) {
		return new SimpleRelationshipJoin(JoinType.RIGHT, property, aliasedRelationship);
	}

	public static Join join(String property, String alias) {
		return new SimpleRelationshipJoin(JoinType.RIGHT, property, alias);
	}

	public static Join join(String property, boolean aliasedRelationship, String alias) {
		return new SimpleRelationshipJoin(JoinType.RIGHT, property, aliasedRelationship, alias);
	}

	public static Join leftJoin(String property) {
		return new SimpleRelationshipJoin(JoinType.LEFT, property);
	}

	public static Join leftJoin(String property, boolean aliasedRelationship) {
		return new SimpleRelationshipJoin(JoinType.LEFT, property, aliasedRelationship);
	}

	public static Join leftJoin(String property, boolean aliasedRelationship, String alias) {
		return new SimpleRelationshipJoin(JoinType.LEFT, property, aliasedRelationship, alias);
	}

	public static Join joinFetch(String property) {
		return new RelationshipFetch(JoinType.RIGHT, property);
	}

	public static Join joinFetch(String property, boolean aliasedRelationship) {
		return new RelationshipFetch(JoinType.RIGHT, property, aliasedRelationship);
	}

	public static Join joinFetch(String property, String alias) {
		return new RelationshipFetch(JoinType.RIGHT, property, alias);
	}

	public static Join joinFetch(String property, boolean aliasedRelationship, String alias) {
		return new RelationshipFetch(JoinType.RIGHT, property, aliasedRelationship, alias);
	}

	public static Join leftJoinFetch(String property) {
		return new RelationshipFetch(JoinType.LEFT, property);
	}

	public static Join leftJoinFetch(String property, boolean aliasedRelationship) {
		return new RelationshipFetch(JoinType.LEFT, property, aliasedRelationship);
	}

	public static Join leftJoinFetch(String property, boolean aliasedRelationship, String alias) {
		return new RelationshipFetch(JoinType.LEFT, property, aliasedRelationship, alias);
	}

	public static Join[] join(Join... fetchList) {
		return fetchList;
	}

	public static Join[] joinList(Join... joinList) {
		return joinList;
	}

	public static Join[] fetch(Join... fetchList) {
		return fetchList;
	}

	@Override
	public String toString() {
		return String.format("%s id=%s", this.getClass().getSimpleName(), this.hashCode());
	}
}
