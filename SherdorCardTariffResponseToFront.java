package uz.sqb.joyda.carddeliveryservice.payload.sherdor;

import uz.sqb.joyda.carddeliveryservice.constant.SherdorCardTariffTypes;

public record SherdorCardTariffResponseToFront(
        Integer id,
        String name,
        SherdorCardTariffTypes type,
        SherdorCardDiscountForFilter discount
) {
}
