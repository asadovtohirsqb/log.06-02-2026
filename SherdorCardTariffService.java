package uz.sqb.joyda.carddeliveryservice.service;

import uz.sqb.joyda.carddeliveryservice.payload.sherdor.SherdorCardTariffResponseToFront;

import java.util.List;

public interface SherdorCardTariffService {

    List<SherdorCardTariffResponseToFront> getTariffsToFilterPage();

}
