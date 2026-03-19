package uz.sqb.joyda.carddeliveryservice.payload.sherdor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import uz.sqb.joyda.carddeliveryservice.exception.ErrorCode;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SherdorCardOrderRequest(
        @NotNull(message = ErrorCode.INPUT_CAN_NOT_BE_NULL)
        Long holderId,
        @NotBlank(message = ErrorCode.INPUT_CAN_NOT_BE_NULL)
        String cardId,
        Integer discountId,
        String uid
) {
    public SherdorCardOrderRequest {
        if (uid == null || uid.isBlank()) {
            uid = UUID.randomUUID().toString();
        }
    }
}