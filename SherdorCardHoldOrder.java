package uz.sqb.joyda.carddeliveryservice.payload.sherdor;

import java.time.LocalDateTime;

public record SherdorCardHoldOrder(
        Long holderId,
        LocalDateTime holdExpiredDate
) {
}
