package net.anthavio.cdao.criteria;

import java.io.Serializable;

public class OrderBy implements Serializable{
	private String name;
	private OrderDirection direction;
    private boolean enabled = false;
    private Integer sequence = 0;
    private boolean caseSensitive = true;
    private boolean alias;

    public boolean isAlias() {
		return alias;
	}
	public void setAlias(boolean alias) {
		this.alias = alias;
	}
	public Integer getSequence() {
        return sequence;
    }
    public void setSequence( Integer sequence ) {
        this.sequence = sequence;
    }
    public OrderBy () {
	}
	public OrderBy (final String aName, final OrderDirection aDirection) {
		this.name = aName;
        if (this.name.contains( "_" )) {
            this.name = name.replace( '_', '.');
        }
		this.direction = aDirection;
	}
	public static OrderBy[] orderBy(OrderBy... orderBy) {
        return orderBy;
	}
	public static OrderBy asc (String name) {
		return new OrderBy(name, OrderDirection.ASC);
	}
	public static OrderBy desc (String name) {
		return new OrderBy(name, OrderDirection.DESC);
	}

	public OrderDirection getDirection() {
		return direction;
	}
	public void setDirection(OrderDirection direction) {
		this.direction = direction;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }
    public boolean isDesc() {
        return direction == OrderDirection.DESC;
    }
    
    public void setDesc( boolean desc ) {
        if (desc) {
            direction = OrderDirection.DESC;
        } else {
            direction = OrderDirection.ASC;
        }
    }

    public boolean isAsc() {
        return direction == OrderDirection.ASC;
    }
    public void setAsc( boolean asc ) {
        if (asc) {
            direction = OrderDirection.ASC;
        } else {
            direction = OrderDirection.DESC;
        }
    }
    public void enable() {
        this.enabled = true;
    }
    
    public void disable() {
        this.enabled = false;
    }
    
    public void asc() {
        direction = OrderDirection.ASC;
    }
    
    public void desc() {
        direction = OrderDirection.DESC;
    }

    public void toggle() {
        this.enabled = true;
        this.setAsc( !isAsc() );
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((sequence == null) ? 0 : sequence.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final OrderBy other = (OrderBy) obj;
		if (direction == null) {
			if (other.direction != null)
				return false;
		} else if (!direction.equals(other.direction))
			return false;
		if (enabled != other.enabled)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
		return true;
	}

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    

}
