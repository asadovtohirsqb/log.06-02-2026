package uz.sqb.joyda.carddeliveryservice.repository.card_delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uz.sqb.joyda.carddeliveryservice.domain.card_delivery.AdminOrderView;

public interface AdminOrderViewRepository extends JpaRepository<AdminOrderView, Long>, JpaSpecificationExecutor<AdminOrderView> {
}
