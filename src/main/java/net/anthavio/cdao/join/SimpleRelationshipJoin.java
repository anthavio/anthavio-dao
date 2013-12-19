package net.anthavio.cdao.join;

public class SimpleRelationshipJoin extends RelationshipJoin {

	public SimpleRelationshipJoin() {
		super();
	}

	public SimpleRelationshipJoin(JoinType join, String relationship, boolean aliasedRelationship,
			String alias) {
		super(join, relationship, aliasedRelationship, alias);
	}

	public SimpleRelationshipJoin(JoinType join, String relationship, boolean aliasedRelationship) {
		super(join, relationship, aliasedRelationship);
	}

	public SimpleRelationshipJoin(JoinType join, String relationship, String alias) {
		super(join, relationship, alias);
	}

	public SimpleRelationshipJoin(JoinType join, String relationship) {
		super(join, relationship);
	}

	public SimpleRelationshipJoin(JoinType join) {
		super(join);
	}

}
