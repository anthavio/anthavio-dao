package net.anthavio.dao.test.entity.order;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.anthavio.dao.test.entity.Employee;
import net.anthavio.log.ToStringBuilder;



/**
 * @author vanek
 *
 */
@Entity
@Table(name = "ORDER_TABLE")
public class Order {

	@Id
	@GeneratedValue
	@Column(name = "ORDER_ID")
	private Integer id;

	@Column(name = "TEXT_VAL", nullable = false)
	private String text;

	@OneToMany(mappedBy = "order")
	private List<Item> items;

	@OneToOne
	@JoinColumn(name = "ORDER_ID", referencedColumnName = "ORDER_ID")
	private OrderDetail detail;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EMP_ID", insertable = false, updatable = false)
	private Employee employee;

	@Column(name = "EMP_ID", nullable = false)
	private Integer employeeId;

	public Order() {
		//default
	}

	public Order(String text) {
		this.text = text;
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
		Order other = (Order) obj;
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

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> details) {
		this.items = details;
	}

	public OrderDetail getDetail() {
		return detail;
	}

	public void setDetail(OrderDetail detail) {
		this.detail = detail;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

}
