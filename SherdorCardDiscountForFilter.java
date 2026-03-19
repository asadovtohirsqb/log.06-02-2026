package uz.sqb.joyda.carddeliveryservice.payload.sherdor;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SherdorCardDiscountForFilter(
        Integer id,
        String name,
        Short discountPercentage,
        LocalDateTime finishTime
) {
}
