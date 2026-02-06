package uz.sqb.joyda.carddeliveryservice.payload.adminOrderView;

import jakarta.validation.constraints.NotNull;
import uz.sqb.joyda.carddeliveryservice.annotation.validation.ValidOrderParamStatusUpdater;
import uz.sqb.joyda.carddeliveryservice.constant.OrderStatus;

@ValidOrderParamStatusUpdater
public record OrderParamStatusUpdater(
        @NotNull Long orderId,
        OrderStatus status
) {
}
