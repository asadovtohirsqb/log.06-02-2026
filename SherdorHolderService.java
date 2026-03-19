package uz.sqb.joyda.carddeliveryservice.service;

import jakarta.validation.Valid;
import uz.sqb.joyda.carddeliveryservice.payload.sherdor.SherdorHolderRequest;
import uz.sqb.joyda.carddeliveryservice.payload.sherdor.SherdorHolderResponse;

public interface SherdorHolderService {
    SherdorHolderResponse holding(@Valid SherdorHolderRequest request);
    Boolean cancelHold(Long sherdorHolderId);
    SherdorHolderResponse findUserActiveHold();
}
