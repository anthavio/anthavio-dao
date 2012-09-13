package com.anthavio.cdao.join;

public class RelationshipJoin extends Join {

	public RelationshipJoin() {

	}

	public RelationshipJoin(final JoinType aJoin) {
		this.joinType = aJoin;
	}

	public RelationshipJoin(final JoinType aJoin, final String relationship) {
		this.relationshipProperty = relationship;
		this.joinType = aJoin;
	}

	public RelationshipJoin(final JoinType aJoin, final String relationship,
			boolean aliasedRelationship) {
		this.relationshipProperty = relationship;
		this.joinType = aJoin;
		this.aliasedRelationship = aliasedRelationship;
	}

	public RelationshipJoin(final JoinType aJoin, final String relationship, String alias) {
		this.alias = alias;
		this.relationshipProperty = relationship;
		this.joinType = aJoin;
	}

	public RelationshipJoin(final JoinType aJoin, final String relationship,
			boolean aliasedRelationship, String alias) {
		this.alias = alias;
		this.relationshipProperty = relationship;
		this.joinType = aJoin;
		this.aliasedRelationship = aliasedRelationship;
	}

	private String relationshipProperty;
	private String alias = "";
	private boolean aliasedRelationship = false;
	protected JoinType joinType;

	public String getRelationshipProperty() {
		return relationshipProperty;
	}

	public void setRelationshipProperty(String relationshipProperty) {
		this.relationshipProperty = relationshipProperty;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean isAliasedRelationship() {
		return aliasedRelationship;
	}

	public void setAliasedRelationship(boolean aliasedRelationship) {
		this.aliasedRelationship = aliasedRelationship;
	}

	public String getDefaultAlias() {
		return this.relationshipProperty.replace('.', '_');
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public void setJoinType(JoinType join) {
		this.joinType = join;
	}

}
