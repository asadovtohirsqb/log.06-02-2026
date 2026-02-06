package uz.sqb.joyda.carddeliveryservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import uz.sqb.joyda.carddeliveryservice.payload.adminOrderView.AdminOrderViewAdminPanelDto;
import uz.sqb.joyda.carddeliveryservice.payload.adminOrderView.OrderParamStatusUpdater;

public interface OrderParamAuditService {
    //todo--------------
    AdminOrderViewAdminPanelDto updateStatus(OrderParamStatusUpdater updater) throws JsonProcessingException;
}
