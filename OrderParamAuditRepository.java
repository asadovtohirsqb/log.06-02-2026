package uz.sqb.joyda.carddeliveryservice.repository.card_delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.sqb.joyda.carddeliveryservice.domain.card_delivery.OrderParamAudit;

public interface OrderParamAuditRepository extends JpaRepository<OrderParamAudit, Long> {
}
