package com.dshop.dshop.repositories;

import com.dshop.dshop.models.response.OrderDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dshop.dshop.models.OrderDetail;
import com.dshop.dshop.models.User;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
	// Lấy thông tin giỏ hàng chưa thanh toán ( type = 0)
	OrderDetail findByUserAndType(User user, int type);

	Page<OrderDetail> findAllByType(int type, Pageable pageable);

	Page<OrderDetail> findAllByTypeAndStatus(int type, int status, Pageable pageable);

	List<OrderDetail> findAllByUserAndType(User user, int type);

	OrderDetail findByUserAndTypeAndStatus(User user, int type, int status);

	List<OrderDetail> findAllByUserAndTypeAndStatus(User user, int type, int status);

	OrderDetail findByUserAndId(User user, Long orderDetailId);

	long countByType(int type);
}
