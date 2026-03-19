package uz.sqb.joyda.carddeliveryservice.service;

import jakarta.validation.Valid;
import uz.sqb.joyda.carddeliveryservice.payload.sherdor.SherdorCardDiscountCreator;
import uz.sqb.joyda.carddeliveryservice.payload.sherdor.SherdorCardDiscountResponse;

public interface SherdorCardDiscountService {
    SherdorCardDiscountResponse createDiscount(@Valid SherdorCardDiscountCreator creator);
}
