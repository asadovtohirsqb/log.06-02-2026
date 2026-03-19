package uz.sqb.joyda.carddeliveryservice.payload.sherdor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import uz.sqb.joyda.carddeliveryservice.constant.SherdorHolderStatus;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SherdorHolderResponse(
        Long id,
        SherdorHolderStatus status,
        @JsonProperty("held_at")
        LocalDateTime heldAt,
        @JsonProperty("holding_expires_at")
        LocalDateTime holdingExpiresAt,
        SherdorHolderRequest details
) {
}
