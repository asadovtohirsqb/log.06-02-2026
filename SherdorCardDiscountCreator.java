package uz.sqb.joyda.carddeliveryservice.payload.sherdor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import uz.sqb.joyda.carddeliveryservice.exception.ErrorCode;
import uz.sqb.joyda.commons.pojolibrary.payload.base.MessageTranslation;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SherdorCardDiscountCreator(
        MessageTranslation name,
        @NotNull(message = ErrorCode.INPUT_CAN_NOT_BE_NULL)
        @JsonProperty("tariff_id")
        Integer tariffId,
        @NotNull(message = ErrorCode.INPUT_CAN_NOT_BE_NULL)
        @Min(value = 0, message = ErrorCode.SHERDOR_CARD_DISCOUNT_MIN_VALUE_ERROR)
        @Max(value = 99, message = ErrorCode.SHERDOR_CARD_DISCOUNT_MAX_VALUE_ERROR)
        @JsonProperty("discount_percentage")
        Short discountPercentage,
        @NotNull(message = ErrorCode.INPUT_CAN_NOT_BE_NULL)
        @JsonProperty("start_time")
        LocalDateTime startTime,
        @NotNull(message = ErrorCode.INPUT_CAN_NOT_BE_NULL)
        @JsonProperty("finish_time")
        LocalDateTime finishTime,
        @NotNull(message = ErrorCode.INPUT_CAN_NOT_BE_NULL)
        Boolean active
) {
}
