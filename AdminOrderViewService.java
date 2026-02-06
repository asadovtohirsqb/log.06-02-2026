package uz.sqb.joyda.carddeliveryservice.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import uz.sqb.joyda.carddeliveryservice.payload.adminOrderView.AdminOrderFilter;
import uz.sqb.joyda.carddeliveryservice.payload.adminOrderView.AdminOrderViewAdminPanelDto;
import uz.sqb.joyda.carddeliveryservice.payload.adminOrderView.OrderParamStatusUpdater;

import java.util.List;

public interface AdminOrderViewService {
    List<AdminOrderViewAdminPanelDto> findAllForAdminPanel();
    Page<AdminOrderViewAdminPanelDto> searchForAdminPanel(AdminOrderFilter filter);

    void orderParamStatusUpdater(@Valid OrderParamStatusUpdater updater);
}
