package uz.sqb.joyda.carddeliveryservice.payload.adminOrderView;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import uz.sqb.joyda.carddeliveryservice.constant.CardActionType;
import uz.sqb.joyda.carddeliveryservice.constant.DeliveryType;
import uz.sqb.joyda.carddeliveryservice.constant.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class AdminOrderFilter {

    // Text search (LIKE yoki exact)
    private String orderId;
    private String userId;
    private String phone;
    private String pnfl;
    private String fio;
    private String contractId;

    // Dropdown / Multi-select
    private List<OrderStatus> statuses;           // multi
    private List<String> bxmCodes;                // multi (branch kodlari)
    private List<CardActionType> cardActionTypes; // multi
    private List<DeliveryType> deliveryTypes;     // multi

    // Boolean
    private Boolean paymentIsDone;
    private Boolean provodkaIsDone;

    // Date range
    private LocalDateTime orderedTimeFrom;
    private LocalDateTime orderedTimeTo;

    // Pagination & Sort
    private Pageable pageable;
}