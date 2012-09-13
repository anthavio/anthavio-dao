package com.anthavio.dao.test.entity.order;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * @author vanek
 * 
 * Hibernate specific mapping
 */
@Entity
@Table(name = "ORDER_DETAIL")
public class OrderDetail {

	@Id
	@Column(name = "ORDER_ID")
	@GeneratedValue(generator = "foreign")
	@GenericGenerator(name = "foreign", strategy = "foreign", // 
	parameters = { @Parameter(name = "property", value = "order") })
	private Integer orderId;

	@Column(name = "TEXT_VAL", nullable = false)
	private String text;

	@OneToOne(optional = false, fetch = FetchType.LAZY, mappedBy = "detail")
	private Order order;

	public OrderDetail() {
		//default
	}

	public OrderDetail(String text) {
		this.text = text;
	}

	@Override
	public int hashCode() {
		return ((orderId == null) ? 0 : orderId.hashCode());
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
		OrderDetail other = (OrderDetail) obj;
		if (orderId == null) {
			if (other.orderId != null) {
				return false;
			}
		} else if (!orderId.equals(other.orderId)) {
			return false;
		}
		return true;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

}
