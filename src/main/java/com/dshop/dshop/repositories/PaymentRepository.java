package com.dshop.dshop.repositories;

import com.dshop.dshop.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
