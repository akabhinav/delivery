package com.quickserve.repository;

import com.quickserve.model.Delivery;
import com.quickserve.model.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Delivery entity operations
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByOrderId(Long orderId);

    List<Delivery> findByDeliveryPartnerId(Long deliveryPartnerId);

    List<Delivery> findByStatus(DeliveryStatus status);

    List<Delivery> findByDeliveryPartnerIdOrderByCreatedAtDesc(Long deliveryPartnerId);
}
