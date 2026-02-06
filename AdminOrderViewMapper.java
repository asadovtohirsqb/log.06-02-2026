package uz.sqb.joyda.carddeliveryservice.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.sqb.joyda.carddeliveryservice.configuration.security.session.UserSession;
import uz.sqb.joyda.carddeliveryservice.constant.TransactionStatus;
import uz.sqb.joyda.carddeliveryservice.domain.card_delivery.AdminOrderView;
import uz.sqb.joyda.carddeliveryservice.payload.adminOrderView.AdminOrderViewAdminPanelDto;


@Slf4j
@Component
@RequiredArgsConstructor
public class AdminOrderViewMapper {
    private final UserSession userSession;

    public AdminOrderViewAdminPanelDto toAdminOrderViewAdminPanelDto(AdminOrderView entity ){
        return new AdminOrderViewAdminPanelDto(
                entity.getOrderParamId(),
                entity.getUserId(),
                entity.getPhone(),
                entity.getPnfl(),
                entity.getFio(),
                entity.getProductCode(),
                entity.getProduct(),
                entity.getContractId(),
                entity.getStatus(),
                entity.getState(),
                entity.getBxmCode(),
                entity.getCardActionType(),
                entity.getCardAmount(),
                entity.getPaymentStatus() != null && entity.getPaymentStatus() == TransactionStatus.DONE,
                entity.getProvodkaStatus() != null && entity.getProvodkaStatus() == TransactionStatus.DONE,
                entity.getDeliveryType(),
                entity.getOrderedTime()
        );
    }
}
