package com.anthavio.cdao.join;

public class RelationshipFetch extends RelationshipJoin {

	public RelationshipFetch(final JoinType aJoin) {
		this.joinType = aJoin;
	}

	public RelationshipFetch(final JoinType aJoin, final String relationship) {
		super.setRelationshipProperty(relationship);
		this.joinType = aJoin;
	}

	public RelationshipFetch(final JoinType aJoin, final String relationship,
			boolean aliasedRelationship) {
		super.setRelationshipProperty(relationship);
		this.joinType = aJoin;
		super.setAliasedRelationship(aliasedRelationship);
	}

	public RelationshipFetch(final JoinType aJoin, final String relationship, String alias) {
		super.setAlias(alias);
		super.setRelationshipProperty(relationship);
		this.joinType = aJoin;
	}

	public RelationshipFetch(final JoinType aJoin, final String relationship,
			boolean aliasedRelationship, String alias) {
		super.setAlias(alias);
		super.setRelationshipProperty(relationship);
		this.joinType = aJoin;
		super.setAliasedRelationship(aliasedRelationship);
	}

}
