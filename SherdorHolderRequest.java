package uz.sqb.joyda.carddeliveryservice.payload.sherdor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import uz.sqb.joyda.carddeliveryservice.exception.ErrorCode;
import uz.sqb.joyda.carddeliveryservice.payload.card_delivery.AddressParamDTO;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SherdorHolderRequest(
        @JsonProperty("referral_code")
        String referralCode,
        @JsonProperty("sherdor_card_number")
        @NotNull(message = ErrorCode.INPUT_CAN_NOT_BE_NULL)
        String sherdorCardNumber,
        @JsonProperty("sherdor_card_price")
        @NotNull(message = ErrorCode.INPUT_CAN_NOT_BE_NULL)
        Long sherdorCardPrice,
        @JsonProperty("sherdor_card_discount_id")
        Integer sherdorCardDiscountId,
        @JsonProperty("sherdor_tariff_id")
        @NotNull(message = ErrorCode.INPUT_CAN_NOT_BE_NULL)
        Integer sherdorTariffId,
        @NotNull(message = ErrorCode.INPUT_CAN_NOT_BE_NULL)
        Integer branchId,
        @NotNull(message = ErrorCode.INPUT_CAN_NOT_BE_NULL)
        Integer cardTypeId,
        @NotNull(message = ErrorCode.INPUT_CAN_NOT_BE_NULL)
        Boolean prepare,
        boolean isDelivery,
        AddressParamDTO address,
        Integer providerId,
        String uid
) {
    public SherdorHolderRequest {
        if (uid == null || uid.isBlank()) {
            uid = UUID.randomUUID().toString();
        }
    }
}