package com.anthavio.dao.test.entity.order;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.anthavio.log.ToStringBuilder;

/**
 * @author vanek
 *
 */
@Entity
@Table(name = "ITEM_TABLE")
public class Item {

	@Id
	@GeneratedValue
	@Column(name = "ITEM_ID")
	private Integer id;

	@Column(name = "TEXT_VAL", nullable = false)
	private String text;

	@Column(name = "ORDER_ID", nullable = false)
	private Integer orderId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "ORDER_ID", insertable = false, updatable = false)
	private Order order;

	public Item() {
		//default
	}

	public Item(String text, Integer orderId) {
		this.text = text;
		this.orderId = orderId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.toString(this);
	}

	@Override
	public int hashCode() {
		return ((id == null) ? 0 : id.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Item other = (Item) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

}
