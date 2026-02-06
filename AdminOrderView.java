package uz.sqb.joyda.carddeliveryservice.domain.card_delivery;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.sqb.joyda.carddeliveryservice.constant.CardActionType;
import uz.sqb.joyda.carddeliveryservice.constant.DeliveryType;
import uz.sqb.joyda.carddeliveryservice.constant.OrderStatus;
import uz.sqb.joyda.carddeliveryservice.constant.TransactionStatus;
import uz.sqb.joyda.carddeliveryservice.domain.card_delivery.base.AuditableV2;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admin_order_view")
public class AdminOrderView extends AuditableV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_param_id", nullable = false, unique = true)
    private Long orderParamId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "product")
    private String product;

    @Column(name = "product_code", length = 50)
    private String productCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private OrderStatus status;

    @Column(name = "\"state\"", length = 50)
    private String state;

    @Column(name = "contract_id")
    private Long contractId;

    @Column(name = "operation_id")
    private Long operationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_action_type", length = 50)
    private CardActionType cardActionType;

    @Column(name = "card_amount")
    private Long cardAmount;

    @Column(name = "delivery_amount")
    private Long deliveryAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type", length = 50)
    private DeliveryType deliveryType;

    @Column(name = "bxm_code", length = 50)
    private String bxmCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 50)
    private TransactionStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "provodka_status", length = 50)
    private TransactionStatus provodkaStatus;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "pnfl", length = 14)
    private String pnfl;

    @Column(name = "fio", length = 255)
    private String fio;

    @Column(name = "ordered_time", nullable = false)
    private LocalDateTime orderedTime;
}
