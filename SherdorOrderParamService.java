package uz.sqb.joyda.carddeliveryservice.service;

import jakarta.validation.Valid;
import uz.sqb.joyda.carddeliveryservice.payload.card_delivery.CardTypeTableDTO;
import uz.sqb.joyda.carddeliveryservice.payload.order.DebitCardOrderResponse;
import uz.sqb.joyda.carddeliveryservice.payload.sherdor.SherdorCardInfoWithDiscount;
import uz.sqb.joyda.carddeliveryservice.payload.sherdor.SherdorCardOrderRequest;

import java.util.List;

public interface SherdorOrderParamService {
    // shag 2 page uchun API
    List<SherdorCardInfoWithDiscount> filterCards(String mask, Integer tariffId, Integer page, Integer perPage);

    // shag 3 page uchun API
    CardTypeTableDTO getSelectedCardFullInfo(String cardNumber, Integer cardTypeId, Integer tariffId);

    DebitCardOrderResponse createSherdorCard(@Valid SherdorCardOrderRequest request);

}
