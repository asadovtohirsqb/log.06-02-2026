package uz.sqb.joyda.carddeliveryservice.payload.sherdor;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SherdorCardInfoWithDiscount(
        @JsonProperty("card_number")
        String cardNumber,
        String tariff,
        Long price,
        @JsonProperty("card_type")
        String cardType,
        @JsonProperty("discount_price")
        Long discountPrice
) {

}
