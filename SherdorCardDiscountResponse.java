package uz.sqb.joyda.carddeliveryservice.payload.sherdor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import uz.sqb.joyda.carddeliveryservice.exception.ErrorCode;
import uz.sqb.joyda.commons.pojolibrary.payload.base.MessageTranslation;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SherdorCardDiscountResponse(
        Integer id,
        MessageTranslation name,
        @NotNull(message = ErrorCode.INPUT_CAN_NOT_BE_NULL)
        @JsonProperty("tariff_id")
        Integer tariffId,
        @JsonProperty("discount_percentage")
        Short discountPercentage,
        @JsonProperty("start_time")
        LocalDateTime startTime,
        @JsonProperty("finish_time")
        LocalDateTime finishTime,
        Boolean active,
        @JsonProperty("created_at")
        LocalDateTime createdAt,
        @JsonProperty("updated_at")
        LocalDateTime updatedAt,
        @JsonProperty("created_by")
        String createdBy,
        @JsonProperty("updated_by")
        String updatedBy
) {
}
