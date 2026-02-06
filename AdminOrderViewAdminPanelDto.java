package uz.sqb.joyda.carddeliveryservice.payload.adminOrderView;

import uz.sqb.joyda.carddeliveryservice.constant.CardActionType;
import uz.sqb.joyda.carddeliveryservice.constant.DeliveryType;
import uz.sqb.joyda.carddeliveryservice.constant.OrderStatus;

import java.time.LocalDateTime;

public record AdminOrderViewAdminPanelDto(
        Long orderId,
        Long userId,
        String phone, // user-account-phone number
        String pnfl,
        String fio,
        String productCode, // card_type_id -  Bank tizimidagi karta mahsuloti kodi
        String product, // title - Karta mahsulotining nomi (masalan, Visa Classic, HUMO va h.k.)
        Long contractId, // contract_id - Karta bo‘yicha shartnoma identifikatori
        OrderStatus status,// status - Buyurtmaning joriy holati (New, In Progress, Issued, Delivered, Cancelled va h.k.)
        String state,// status - Buyurtmaning joriy holati (New, In Progress, Issued, Delivered, Cancelled va h.k.)
        String bxmCode, // branch_id bilan branches ga borib branch_id ni olish kerak. Karta chiqaruvchi BXM kodi va nomi
        CardActionType cardType,// card_action_type - Buyurtma turi: yangi karta ochish yoki qayta chiqarish | Enum (New / Reissue)
        Long amount, // card_amount - To‘lov summasi	Buyurtma uchun to‘langan summa	Number	Range
        Boolean paymentIsDone,// transactions dan operation_id bilan olib done ga tekshiriladi... To‘lov holati (To‘langan / To‘lanmagan)	Boolean	Dropdown
        Boolean provodkaIsDone,// transactions dan operation_id va server= shina or card-delivery larini statusini olib done ga tekshiriladi...  Provodka holati	Buxgalteriya provodkasi o‘tkazilgan yoki yo‘qligi	Boolean	Dropdown
        DeliveryType orderType,// delivery_type (provider_id: uzpost, tezbor) - Buyurtma turi	Kartani olish usuli: Take a way / Delivery (UzPost, Tezbor)	Enum	Dropdown
        LocalDateTime ordered_time // created_at - Buyurtma vaqti	Buyurtma yaratilgan sana va vaqt	DateTime	Period (dan–gacha)
) {
}
