package com.dshop.dshop.models;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ORDER_ITEM")
public class OrderItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private int quantity;

	@ManyToOne()
	@JoinColumn(name = "order_id")
	private OrderDetail orderDetail;

	@ManyToOne()
	@JoinColumn(name = "size_id")
	private  Size size;
}
