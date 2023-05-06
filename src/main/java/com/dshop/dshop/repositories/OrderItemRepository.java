package com.dshop.dshop.repositories;

import com.dshop.dshop.models.OrderDetail;
import com.dshop.dshop.models.OrderItem;
import com.dshop.dshop.models.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    OrderItem findBySizeAndOrderDetail(Size size, OrderDetail orderDetail);

    List<OrderItem> findAllByOrderDetailId(Long orderDetailId);

    void deleteById(Long itemId);
}
