package uz.sqb.joyda.carddeliveryservice.payload.sherdor;

import uz.sqb.joyda.carddeliveryservice.constant.SherdorCardTariffTypes;

public record SherdorFilterLoggerDTO(
        String bin,
        String mask,
        Integer tariffId,
        SherdorCardTariffTypes tariffType) {
}
