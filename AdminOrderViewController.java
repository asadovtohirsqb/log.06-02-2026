package uz.sqb.joyda.carddeliveryservice.controller.admin_panel;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uz.sqb.joyda.carddeliveryservice.constant.CardActionType;
import uz.sqb.joyda.carddeliveryservice.constant.DeliveryType;
import uz.sqb.joyda.carddeliveryservice.constant.OrderStatus;
import uz.sqb.joyda.carddeliveryservice.payload.adminOrderView.AdminOrderFilter;
import uz.sqb.joyda.carddeliveryservice.payload.adminOrderView.AdminOrderViewAdminPanelDto;
import uz.sqb.joyda.carddeliveryservice.payload.adminOrderView.OrderParamStatusUpdater;
import uz.sqb.joyda.carddeliveryservice.service.AdminOrderViewService;
import uz.sqb.joyda.commons.pojolibrary.payload.base.BaseResponse;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/card-delivery/admin/order")
@Validated
public class AdminOrderViewController {

    private final AdminOrderViewService adminOrderViewService;


    /*
    GET /api/admin/orders?page=0&size=20&sort=orderedTime,desc
          &orderId=12345
          &userId=987654
          &phone=998901234567
          &pnfl=31201991234567
          &fio=Aliyev
          &contractId=CONTR-2025-001
          &statuses=NEW,IN_PROGRESS
          &bxmCodes=BXM001,BXM005
          &cardActionTypes=NEW,REISSUE
          &deliveryTypes=UZPOST,TAKE_AWAY
          &paymentIsDone=true
          &provodkaIsDone=false
          &orderedTimeFrom=2026-01-01T00:00:00
          &orderedTimeTo=2026-02-06T23:59:59
    * */
    @GetMapping("/filter")
    public BaseResponse<Page<AdminOrderViewAdminPanelDto>> searchOrders(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String pnfl,
            @RequestParam(required = false) String fio,
            @RequestParam(required = false) String contractId,
            @RequestParam(required = false) List<OrderStatus> statuses,
            @RequestParam(required = false) List<String> bxmCodes,
            @RequestParam(required = false) List<CardActionType> cardActionTypes,
            @RequestParam(required = false) List<DeliveryType> deliveryTypes,
            @RequestParam(required = false) Boolean paymentIsDone,
            @RequestParam(required = false) Boolean provodkaIsDone,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime orderedTimeFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime orderedTimeTo,
            @PageableDefault(size = 20, sort = "orderedTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        AdminOrderFilter filter = AdminOrderFilter.builder()
                .orderId(orderId)
                .userId(userId)
                .phone(phone)
                .pnfl(pnfl)
                .fio(fio)
                .contractId(contractId)
                .statuses(statuses)
                .bxmCodes(bxmCodes)
                .cardActionTypes(cardActionTypes)
                .deliveryTypes(deliveryTypes)
                .paymentIsDone(paymentIsDone)
                .provodkaIsDone(provodkaIsDone)
                .orderedTimeFrom(orderedTimeFrom)
                .orderedTimeTo(orderedTimeTo)
                .pageable(pageable)
                .build();

        Page<AdminOrderViewAdminPanelDto> result = adminOrderViewService.searchForAdminPanel(filter);
        return BaseResponse.ok(result);
    }

//    @PostMapping("/update-status")
//    public BaseResponse<?> updateStatus(
//            @RequestBody OrderParamStatusUpdater updater
//            ) {
//        adminOrderViewService.orderParamStatusUpdater(updater);
//    }
}
