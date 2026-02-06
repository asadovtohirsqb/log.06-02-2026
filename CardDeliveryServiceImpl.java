package uz.sqb.joyda.carddeliveryservice.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.sqb.joyda.carddeliveryservice.configuration.security.session.UserSession;
import uz.sqb.joyda.carddeliveryservice.constant.*;
import uz.sqb.joyda.carddeliveryservice.constant.Currency;
import uz.sqb.joyda.carddeliveryservice.domain.card_delivery.*;
import uz.sqb.joyda.carddeliveryservice.domain.jsonb.Payload;
import uz.sqb.joyda.carddeliveryservice.domain.sqb_mobile.Operation;
import uz.sqb.joyda.carddeliveryservice.domain.sqb_mobile.OperationParam;
import uz.sqb.joyda.carddeliveryservice.domain.sqb_mobile.Transaction;
import uz.sqb.joyda.carddeliveryservice.exception.CustomBadRequestException;
import uz.sqb.joyda.carddeliveryservice.exception.ErrorCode;
import uz.sqb.joyda.carddeliveryservice.mapper.CardTypeMapper;
import uz.sqb.joyda.carddeliveryservice.mapper.OrderParamMapper;
import uz.sqb.joyda.carddeliveryservice.payload.base.Additional;
import uz.sqb.joyda.carddeliveryservice.payload.card.CardFormDTO;
import uz.sqb.joyda.carddeliveryservice.payload.card.CardWithBalanceRequest;
import uz.sqb.joyda.carddeliveryservice.payload.card.CheckCardIsCreatedResponse;
import uz.sqb.joyda.carddeliveryservice.payload.card.CheckCardIsCreatedResponseEnum;
import uz.sqb.joyda.carddeliveryservice.payload.card_delivery.*;
import uz.sqb.joyda.carddeliveryservice.payload.crobs.CrobsResponse;
import uz.sqb.joyda.carddeliveryservice.payload.crobs.delivery.CrobsDeliveryTakeAwayResponse;
import uz.sqb.joyda.carddeliveryservice.payload.holder.OperationOrderParamHolder;
import uz.sqb.joyda.carddeliveryservice.payload.holder.TransactionHolderDebit;
import uz.sqb.joyda.carddeliveryservice.payload.holder.TransactionHolderShina;
import uz.sqb.joyda.carddeliveryservice.payload.order.DebitCardOrderRequest;
import uz.sqb.joyda.carddeliveryservice.payload.order.DebitCardOrderResponse;
import uz.sqb.joyda.carddeliveryservice.payload.order.OrderStateResponse;
import uz.sqb.joyda.carddeliveryservice.payload.referral.AddRefFormDTO;
import uz.sqb.joyda.carddeliveryservice.payload.referral.DataDTO;
import uz.sqb.joyda.carddeliveryservice.payload.referral.SuccessDTO;
import uz.sqb.joyda.carddeliveryservice.payload.shina.CardList;
import uz.sqb.joyda.carddeliveryservice.payload.shina.CardListResponse;
import uz.sqb.joyda.carddeliveryservice.payload.shina.CreateCardResponse;
import uz.sqb.joyda.carddeliveryservice.payload.shina.nibbd.NibbdResponseResponse;
import uz.sqb.joyda.carddeliveryservice.payload.shina.nibbd.NibbdStatisticsResponse;
import uz.sqb.joyda.carddeliveryservice.payload.shina.base.BaseShinaResponse;
import uz.sqb.joyda.carddeliveryservice.payload.user.UserInfoDTO;
import uz.sqb.joyda.carddeliveryservice.property.feature.CardFeatures;
import uz.sqb.joyda.carddeliveryservice.repository.card_delivery.*;
import uz.sqb.joyda.carddeliveryservice.repository.sqb_mobile.OperationRepository;
import uz.sqb.joyda.carddeliveryservice.repository.sqb_mobile.TransactionRepository;
import uz.sqb.joyda.carddeliveryservice.service.*;
import uz.sqb.joyda.carddeliveryservice.utils.BaseUtils;
import uz.sqb.joyda.carddeliveryservice.utils.LiteralUtil;
import uz.sqb.joyda.carddeliveryservice.utils.MessageCode;
import uz.sqb.joyda.carddeliveryservice.utils.MessageUtil;
import uz.sqb.joyda.carddeliveryservice.utils.ServiceUtils;
import uz.sqb.joyda.commons.errorhandler.exceptions.ServiceException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardDeliveryServiceImpl implements CardDeliveryService {

    private final LogService logService;
    private final CardTypeRepository cardTypeRepository;
    private final CardTypeMapper cardTypeMapper;
    private final CardService cardService;
    private final UserService userService;
    private final UserSession userSession;
    private final OperationRepository operationRepository;
    private final MyIdService myIdService;
    private final CardTypeService cardTypeService;
    private final BranchService branchService;
    private final OperationParamService operationParamService;
    private final UzcardService uzcardService;
    private final HumoService humoService;
    private final Way4Service way4Service;
    private final ShinaService shinaService;
    private final CrobsService crobsService;
    private final OrderParamRepository orderParamRepository;
    private final OrderParamService orderParamService;
    private final SmsService smsService;
    private final MessageUtil messageUtil;
    private final ServiceUtils serviceUtils;
    private final DeliveryProviderRepository deliveryProviderRepository;
    private final BranchProviderRepository branchProviderRepository;
    private final OrderParamMapper orderParamMapper;
    private final AddressParamRepository addressParamRepository;
    private static final Set<String> ACTIVE_STATES = Set.of("000", "014", "1", "2", "00");
    private final CardFeatures cardFeatures;
    private final BranchRepository branchRepository;
    private final AddReferralFormService referralService;
    private final TransactionRepository transactionRepository;


    @Override
    public CardTypeResponse getDebitCardTypeList() {

        List<CardParentResponse> cardParentResponses =
                cardTypeMapper.groupByParent(
                        cardTypeMapper.toDTO(
                                serviceUtils.isProd()
                                        ? cardTypeRepository.findAllTypeCardActiveTrue(
                                        List.of(
                                                CardTypes.HUMO,
                                                CardTypes.UZCARD,
                                                CardTypes.VISA_INFINITE_UZS_EPIN,
                                                CardTypes.VISA_INFINITE_USD_EPIN,
                                                CardTypes.VISA_PLATINUM_UZS_EPIN,
                                                CardTypes.VISA_CLASSIC_UZS_EPIN,
                                                CardTypes.VISA_PLATINUM_USD_EPIN,
                                                CardTypes.VISA_CLASSIC_USD_EPIN,
                                                CardTypes.MC_WRLD_USD_EPIN,
                                                CardTypes.MC_WBE_USD_EPIN,
                                                CardTypes.MC_WRLD_UZ_EPIN,
                                                CardTypes.MC_WBE_UZ_EPIN,
                                                CardTypes.MC_ELITE_USD_EPIN,
                                                CardTypes.MC_ELITE_UZS_EPIN,
                                                CardTypes.UNIONPAY_GOLD_USD,
                                                CardTypes.UNIONPAY_GOLD_UZS))
                                        : cardTypeRepository.findAllTypeCardActiveTrueDev(
                                        List.of(
                                                CardTypes.HUMO,
                                                CardTypes.UZCARD,
                                                CardTypes.VISA_INFINITE_UZS_EPIN,
                                                CardTypes.VISA_INFINITE_USD_EPIN,
                                                CardTypes.VISA_PLATINUM_UZS_EPIN,
                                                CardTypes.VISA_CLASSIC_UZS_EPIN,
                                                CardTypes.VISA_PLATINUM_USD_EPIN,
                                                CardTypes.VISA_CLASSIC_USD_EPIN,
                                                CardTypes.MC_WRLD_USD_EPIN,
                                                CardTypes.MC_WBE_USD_EPIN,
                                                CardTypes.MC_WRLD_UZ_EPIN,
                                                CardTypes.MC_WBE_UZ_EPIN,
                                                CardTypes.MC_ELITE_USD_EPIN,
                                                CardTypes.MC_ELITE_UZS_EPIN,
                                                CardTypes.UNIONPAY_GOLD_USD,
                                                CardTypes.UNIONPAY_GOLD_UZS))));

        return new CardTypeResponse(cardParentResponses);
    }

    @Override
    public CardTypeResponse getVirtualCardTypeList() {
        List<CardParentResponse> cardParentResponses =
                cardTypeMapper.groupByParent(
                        cardTypeMapper.toDTO(
                                serviceUtils.isProd()
                                        ? cardTypeRepository.findAllTypeCardActiveTrue(
                                        List.of(
                                                CardTypes.HUMO_VIRTUAL,
                                                CardTypes.UZCARD_VIRTUAL,
                                                CardTypes.VISA_CL_VIRTUAL_UZS,
                                                CardTypes.VISA_STANDARD_UZS_VIRTUAL,
                                                CardTypes.VISA_PREMIUM_UZS_VIRTUAL,
                                                CardTypes.VISA_CLASSIC_USD_VIRTUAL,
                                                CardTypes.VISA_STANDARD_USD_VIRTUAL,
                                                CardTypes.VISA_PREMIUM_USD_VIRTUAL,
                                                CardTypes.MASTERCARD_CLASSIC_UZS_VIRTUAL,
                                                CardTypes.MASTERCARD_STANDARD_UZS_VIRTUAL,
                                                CardTypes.MASTERCARD_PREMIUM_UZS_VIRTUAL,
                                                CardTypes.MASTERCARD_CLASSIC_USD_VIRTUAL,
                                                CardTypes.MASTERCARD_STANDARD_USD_VIRTUAL,
                                                CardTypes.MASTERCARD_PREMIUM_USD_VIRTUAL))
                                        : cardTypeRepository.findAllTypeCardActiveTrueDev(
                                        List.of(
                                                CardTypes.HUMO_VIRTUAL,
                                                CardTypes.UZCARD_VIRTUAL,
                                                CardTypes.VISA_CL_VIRTUAL_UZS,
                                                CardTypes.VISA_STANDARD_UZS_VIRTUAL,
                                                CardTypes.VISA_PREMIUM_UZS_VIRTUAL,
                                                CardTypes.VISA_CLASSIC_USD_VIRTUAL,
                                                CardTypes.VISA_STANDARD_USD_VIRTUAL,
                                                CardTypes.VISA_PREMIUM_USD_VIRTUAL,
                                                CardTypes.MASTERCARD_CLASSIC_UZS_VIRTUAL,
                                                CardTypes.MASTERCARD_STANDARD_UZS_VIRTUAL,
                                                CardTypes.MASTERCARD_PREMIUM_UZS_VIRTUAL,
                                                CardTypes.MASTERCARD_CLASSIC_USD_VIRTUAL,
                                                CardTypes.MASTERCARD_STANDARD_USD_VIRTUAL,
                                                CardTypes.MASTERCARD_PREMIUM_USD_VIRTUAL))));
        return new CardTypeResponse(cardParentResponses);
    }

    @Override
    public UserDebitCardsResponse getUserOrders(boolean isHistory) {
        return new UserDebitCardsResponse(orderParamService.getUserOrders(isHistory));
    }

    @Override
    public UserOrder getUserOrderByOrderId(Long orderId) {
        return orderParamService.getUserOrderByOrderId(orderId);
    }

    /**
     * Berilgan vaqt oralig‘ida yaratilgan orderParams yozuvlarini bazadan olib,
     * har bir order bo‘yicha kerakli bog‘liq ma’lumotlarni (branch, card type,
     * delivery provider, address va user info) yig‘adi hamda
     * CROBS tizimiga qayta yuboradi.
     *
     * <p>
     * Ushbu metod asosan texnik nosozliklar, tarmoq uzilishlari yoki CROBS integratsiyasidagi
     * muammolar sababli o‘z vaqtida yuborilmagan debit karta buyurtmalarini qayta tiklash
     * (re-send) uchun ishlatiladi. Har bir order uchun quyidagi qadamlar bajariladi:
     * </p>
     *
     * <ul>
     *   <li>Bazada {@code createdAt} maydoni bo‘yicha {@code fromDate} va {@code toDate} oralig‘idagi
     *       orderParams topiladi.</li>
     *   <li>Har bir order uchun bog‘liq ma’lumotlar (filial, karta turi, yetkazib beruvchi,
     *       manzil va foydalanuvchi ma’lumotlari) yuklanadi.</li>
     *   <li>Ma’lumotlar {@link #sendToDeliveryTakeAwayDataToCrobs2} metodiga yuborilib,
     *       CROBS tizimiga qayta so‘rov jo‘natiladi.</li>
     * </ul>
     *
     * <p>
     * Qidiruv kriteriyasi:
     * <pre>
     * createdAt BETWEEN fromDate AND toDate
     * </pre>
     * </p>
     *
     * <p>
     * <strong>Eslatma:</strong><br>
     * Ushbu metodda CROBS ga yuborish uchun maxsus yaratilgan
     * {@link #sendToDeliveryTakeAwayDataToCrobs2} metodidan foydalaniladi.
     * Foydalanuvchi ma’lumotlari (UserInfoDTO) hozircha test maqsadida qattiq kodlangan.
     * </p>
     *
     * @param fromDate qidiruv boshlanish vaqti
     * @param toDate   qidiruv tugash vaqti
     * @return barcha orderParams muvaffaqiyatli qayta yuborilsa {@code true}
     * @throws ServiceException ma’lumotlarni bazadan o‘qish yoki CROBS ga yuborish jarayonida
     *                          xatolik yuz berganda (400 Internal Server Error)
     * @author Tohir Asadov
     */
    @Override
    public Boolean resendingCardsInfoToCrobsBetweenDate(
            LocalDateTime fromDate,
            LocalDateTime toDate) {

        List<OrderParam> orderParams =
                orderParamRepository.findByCreatedAtBetween(fromDate, toDate);

        try {
            orderParams.forEach(orderParam -> {

                Optional<Branch> optionalBranch =
                        branchRepository.findById(orderParam.getBranchId());

                CardType cardType =
                        cardTypeService.findByIdAndActiveTrue(orderParam.getCardTypeId());

                DeliveryProvider deliveryProvider =
                        orderParam.getProviderId() != null
                                ? deliveryProviderRepository.findById(orderParam.getProviderId()).get()
                                : null;

                Optional<AddressParam> optionalAddressParam =
                        addressParamRepository.findByOrderId(orderParam.getId());
                UserInfoDTO userInfo = userService.getUserInfo(orderParam.getUserId());

                UserInfoDTO userInfoDTO = new UserInfoDTO(
                        null,
                        null,
                        null,
                        "TEST-ALI",
                        "TEST-ALIYEV",
                        null,
                        null,
                        null,
                        null
                );

                sendToDeliveryTakeAwayDataToCrobs3(
                        orderParam.getDeliveryType() != DeliveryType.TAKE_AWAY,
                        orderParam.getRequestId(),
                        optionalBranch.get(),
                        cardType,
                        deliveryProvider,
                        optionalAddressParam.orElse(null),
                        orderParam,
                        userInfo.getFirstname() == null ? userInfoDTO : userInfo
                );
            });

            return true;

        } catch (Exception e) {
            log.error("TEST-ERROR : {}", e.getMessage(), e);
            throw ServiceException.with400(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
        }
    }

    @Override
    public CheckCardIsCreatedResponse checkCardIsCreated(Long operationId) {
        boolean existsById = operationRepository.existsById(operationId);
        if (existsById){
            Optional<OrderParam> optionalOrderParam = orderParamRepository.findByOperationId(operationId);
            if (optionalOrderParam.isPresent()){
                OrderParam orderParam = optionalOrderParam.get();
                return new CheckCardIsCreatedResponse(orderParam.getContractId()!=null ? CheckCardIsCreatedResponseEnum.FINISHED : CheckCardIsCreatedResponseEnum.ERROR);
            }
        }
        return new CheckCardIsCreatedResponse(CheckCardIsCreatedResponseEnum.UNKNOWN);
    }


    /**
     * Ma’lum bir vaqt oralig‘ida CROBS tizimiga muvaffaqiyatli yuborilmagan orderParams ma’lumotlarini
     * qayta yuborish uchun mo‘ljallangan metod.
     *
     * <p>
     * Ushbu metod, turli sabablarga ko‘ra (tarmoq uzilishi, CROBS tizimi ishlamay qolishi, xatoliklar va h.k.)
     * belgilangan vaqt oralig‘ida yaratilgan debit karta buyurtmalari (orderParams) CROBS ga yuborilmagan
     * bo‘lsa, ularni qayta yuborish imkonini beradi.
     * </p>
     *
     * <p>
     * <strong>Eslatma:</strong><br>
     * - Method test muhitida muvaffaqiyatli sinovdan o‘tgan va production muhitida bir necha marta ishlatilgan.<br>
     * - Oxirgi marta avtomatik rejimda (scheduled) {@code 2026-01-09} kuni soat 10:34 da ishlagan.<br>
     * - Hozirda avtomatik rejada ishlamaydi – {@code @Scheduled} annotatsiyasi kommentga olingan.<br>
     * - Faqat kerakli holatlarda qo‘lda ishga tushiriladi.<br>
     * - Agar CROBS ga ma’lumotlar yuborilmay qolgan vaqt oralig‘i aniqlansa, ushbu metodni chaqirib,
     * {@code resendingCardsInfoToCrobsBetweenDate} ga mos {@code fromDate} va {@code toDate} ni kiritish kifoya.
     * </p>
     *
     * <p>
     * <strong>Ishlatish misoli (kerak bo‘lganda):</strong><br>
     * {@code resendingCardsInfoToCrobsBetweenDate(
     * LocalDateTime.of(2026, 1, 6, 12, 55, 0),
     * LocalDateTime.of(2026, 1, 8, 16, 40, 0)
     * )}
     * </p>
     *
     * <p>
     * {@code @Transactional} annotatsiyasi qo‘yilgan – ma’lumotlar bazasida amalga oshirilgan o‘zgartirishlar
     * xato yuz bersa, rollback qilinadi.
     * </p>
     *
     * @author Tohir Asadov
     */
    @Transactional
// @Scheduled(cron = "0 55 17 * * ?")  // 2026-01-27 10:29 oxirgi ishlagan kun, hozircha faqat qo‘lda ishlatish uchun o‘chirilgan
    public void dailyTaskAt20() {
        log.info("Har kuni soat 17:46 da ishlaydigan task boshlandi");

        Boolean result = resendingCardsInfoToCrobsBetweenDate(
                LocalDateTime.of(2025, 12, 1, 0, 0, 0),
                LocalDateTime.of(2026, 2, 2, 18, 10, 0));

        log.info("TEST-TASK is done: {}", result);
    }

    @Override
    public OrderStateResponse getOrderState() {
        return new OrderStateResponse(crobsService.getStates());
    }

    @Override
    public CardCountCheckResponse checkCardCountOld() {

        int cardCode = 1;
        int localeCards = 0;

        while (cardCode < 4) {

            String requestId = UUID.randomUUID().toString();
            BaseShinaResponse<CardListResponse> shinaResponse =
                    shinaService.getCardListByClientId(
                            requestId, cardCode, userSession.getUserInfo().getIabsId());
            if (shinaResponse == null) {
                throw ServiceException.with400(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            if (Objects.nonNull(shinaResponse.getResponseBody())
                    && Objects.nonNull(shinaResponse.getResponseBody().cardList())) {

                List<CardList> cardList = getCardLists(shinaResponse);
                if (!cardList.isEmpty()) {
                    for (CardList list : cardList) {
                        if (list.stateCode().equals("000")
                                || list.stateCode().equals("014")
                                || list.stateCode().equals("1")
                                || list.stateCode().equals("2")
                                || list.stateCode().equals("00")) {
                            localeCards++;
                        }
                    }
                }
                cardCode++;

            } else {
                if (!(shinaResponse.getCode().equals(404)
                        && shinaResponse.getMsg().equals(LiteralUtil.CARD_NOT_FOUND))) {
                    if ((shinaResponse.getMsg() != null
                            && !shinaResponse.getMsg().isEmpty()
                            && (shinaResponse.getMsg().contains(LiteralUtil.LIMIT_SHINA)
                            || shinaResponse.getMsg().contains(LiteralUtil.LIMIT_SHINA_2)
                            || shinaResponse.getMsg().contains(LiteralUtil.LIMIT_SHINA_3)))) {
                        if (shinaResponse.getMsg().contains(LiteralUtil.LIMIT_20)) {
                            throw ServiceException.with400(ErrorCode.USER_CARD_LIMIT_ERROR_20);
                        } else {
                            throw ServiceException.with400(ErrorCode.USER_CARD_LIMIT_ERROR);
                        }
                    }
                    throw ServiceException.with400(
                            ErrorCode.INTERNAL_SERVER_ERROR, shinaResponse.getOraMsg());
                }
            }
        }
        // todo Faqat SQB kartalari soni tekshirildi, boshqa bank kartalari sonini ham tekshirish
        // qo'shilishi kerak
        // biznes API bergandan keyin tekshirolaman
        if (localeCards > 5) {
            return new CardCountCheckResponse(localeCards, false);
        } else {
            return new CardCountCheckResponse(localeCards, true);
        }
    }

    @Override
    public CardCountCheckResponse checkCardCount() {
        int localeCards = 0;
        for (int cardCode = 1; cardCode < 4; cardCode++) {
            String requestId = UUID.randomUUID().toString();
            BaseShinaResponse<CardListResponse> shinaResponse =
                    shinaService.getCardListByClientId(
                            requestId, cardCode, userSession.getUserInfo().getIabsId());
            if (shinaResponse == null) {
                throw ServiceException.with400(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            if (Objects.nonNull(shinaResponse.getResponseBody())
                    && Objects.nonNull(shinaResponse.getResponseBody().cardList())) {
                List<CardList> cardList = getCardLists(shinaResponse);
                if (!cardList.isEmpty()) {
                    for (CardList list : cardList) {
                        String stateCode = list.stateCode();
                        if (isActiveState(stateCode)) {
                            localeCards++;
                        }
                    }
                }
                continue;
            }
            String msg = shinaResponse.getMsg();
            Integer code = shinaResponse.getCode();
            if (Objects.equals(code, 404) && Objects.equals(msg, LiteralUtil.CARD_NOT_FOUND)) {
                continue;
            }
            if (msg != null
                    && !msg.isEmpty()
                    && (msg.contains(LiteralUtil.LIMIT_SHINA)
                    || msg.contains(LiteralUtil.LIMIT_SHINA_2)
                    || msg.contains(LiteralUtil.LIMIT_SHINA_3))) {
                if (msg.contains(LiteralUtil.LIMIT_20)) {
                    throw ServiceException.with400(ErrorCode.USER_CARD_LIMIT_ERROR_20);
                } else {
                    throw ServiceException.with400(ErrorCode.USER_CARD_LIMIT_ERROR);
                }
            }
            throw ServiceException.with400(ErrorCode.INTERNAL_SERVER_ERROR, shinaResponse.getOraMsg());
        }

        // todo: Faqat SQB kartalari tekshirilmoqda, boshqa bank kartalarini ham qo‘shish kerak
        boolean isOk = localeCards <= 5;
        return new CardCountCheckResponse(localeCards, isOk);
    }

    /**
     * Foydalanuvchining barcha kartalari sonini NIBBD tizimidan tekshirish uchun mo‘ljallangan metod.
     *
     * <p>
     * Ushbu metod joriy sessiyadagi foydalanuvchi (IABS ID bo‘yicha) uchun NIBBD orqali
     * mavjud bo‘lgan debit kartalari umumiy sonini so‘raydi va natijada:
     * </p>
     * <ul>
     *   <li>Kartalar sonini ({@code totalCount}) qaytaradi.</li>
     *   <li>Kartalar soni 20 tadan kam bo‘lsa – yangi karta chiqarishga ruxsat berilganligini
     *       ({@code canOrderNewCard = true}) belgilaydi.</li>
     * </ul>
     *
     * <p>
     * So‘rov {@link ShinaService#getUserAllCardCountInfo} orqali amalga oshiriladi.
     * Javob {@link BaseShinaResponse}<{@link NibbdStatisticsResponse}> shaklida keladi va
     * ichki {@code nibbdresponse.response.total_count} maydonidan kartalar soni olinadi.
     * </p>
     *
     * <p>
     * <strong>Xatolik holatlari:</strong><br>
     * - Shina javobi null bo‘lsa yoki muvaffaqiyatsiz code ({@code code ≠ 0}) bo‘lsa.<br>
     * - responseBody yoki ichki response strukturalari null bo‘lsa.<br>
     * - total_count maydoni mavjud bo‘lmasa yoki parse qilib bo‘lmasa.<br>
     * Barcha holatlarda {@link ServiceException} (400 – INTERNAL_SERVER_ERROR) tashlanadi.
     * </p>
     *
     * @return {@link CardCountCheckResponse} – kartalar soni va yangi buyurtma berish imkoniyati
     * @throws ServiceException NIBBD so‘rovi muvaffaqiyatsiz bo‘lsa yoki ma’lumotlar tuzilmasi kutilganidek bo‘lmasa
     * @author Tohir Asadov
     */
    @Override
    public CardCountCheckResponse checkAllCardCount() {
        String requestId = UUID.randomUUID().toString();

        //// referral_code - testing uchun yozildi
//        UserInfoDTO userInfo = userService.getUserInfo(711473L);
//        UserInfoDTO userInfoOwner = userService.getUserInfo(599387L);
//        AddRefFormDTO requestReferral = new AddRefFormDTO(
//                111112L,
//                "card-delivery",
//                userInfoOwner.getId(),
//                userInfoOwner.getPnfl(),
//                "UQDWDALE",
//                userInfo.getId(),
//                userInfo.getPhone(),
//                userInfo.getPnfl()
//        );
//        logService.logInfo("/referral/referralCode","CARD-DELIVERY (test) referralService.saveV3 before REFERRAL: "+ requestReferral);
////                    DataDTO<SuccessDTO> addReferralForm = referralService.addReferralData2(requestReferral);
//        AddReferralForm addReferralForm = referralService.saveV3(requestReferral);
//        logService.logInfo("/referral/referralCode", "CARD-DELIVERY (test) referralService.saveV3 saved REFERRAL: "+addReferralForm);

        BaseShinaResponse<NibbdStatisticsResponse> shinaResponse =
                shinaService.getUserAllCardCountInfo(requestId, userSession.getUserInfo().getPnfl());
        if (shinaResponse == null) {
            throw ServiceException.with400(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        if (Objects.nonNull(shinaResponse.getResponseBody())
                && Objects.nonNull(shinaResponse.getResponseBody().nibbdresponse())) {

            NibbdResponseResponse response = getNibbdResponse(shinaResponse);
            if (response != null) {
                int totalCount = Integer.parseInt(response.total_count());
                return new CardCountCheckResponse(totalCount, totalCount < 20);
            } else {
                throw ServiceException.with400(ErrorCode.INTERNAL_SERVER_ERROR, shinaResponse.getMsg());
            }

        } else {
            throw ServiceException.with400(ErrorCode.INTERNAL_SERVER_ERROR, shinaResponse.getOraMsg());
        }
    }

    // UQDWDALE

    /**
     * Shina javobidan ichki {@code nibbdresponse.response} qismini xavfsiz tarzda ajratib olish
     * uchun yordamchi metod.
     *
     * <p>
     * Quyidagi shartlarni tekshiradi:
     * <ul>
     *   <li>{@code shinaResponse.code == 0} (muvaffaqiyatli javob)</li>
     *   <li>{@code shinaResponse.responseBody.nibbdresponse} null emas</li>
     *   <li>{@code shinaResponse.responseBody.nibbdresponse.response} null emas</li>
     * </ul>
     * Agar birorta shart bajarilmasa – {@link ServiceException} tashlaydi.
     * </p>
     *
     * @param shinaResponse NIBBD dan kelgan to‘liq javob
     * @return ichki {@link NibbdResponseResponse} obyekti (total_count va banks bilan)
     * @throws ServiceException javob strukturasida muammo bo‘lsa yoki code muvaffaqiyatli bo‘lmasa
     * @author Tohir Asadov
     */
    private NibbdResponseResponse getNibbdResponse(BaseShinaResponse<NibbdStatisticsResponse> shinaResponse) {
        if (!shinaResponse.getCode().equals(0)
                || shinaResponse.getResponseBody().nibbdresponse() == null
                || shinaResponse.getResponseBody().nibbdresponse().response() == null) {
            throw ServiceException.with400(ErrorCode.INTERNAL_SERVER_ERROR, shinaResponse.getMsg());
        }

        return shinaResponse.getResponseBody().nibbdresponse().response();
    }

    private boolean isActiveState(String code) {
        return ACTIVE_STATES.contains(code);
    }

    public static List<CardList> getCardLists(BaseShinaResponse<CardListResponse> shinaResponse) {
        List<CardList> cardList;

        if (!shinaResponse.getCode().equals(0)
                || shinaResponse.getResponseBody() == null
                || shinaResponse.getResponseBody().cardList() == null) {
            if (shinaResponse.getCode().equals(404) && shinaResponse.getMsg().equals("Карта не найден")) {
                cardList = Collections.emptyList();
            } else {
                throw ServiceException.with400(ErrorCode.INTERNAL_SERVER_ERROR, shinaResponse.getMsg());
            }
        } else {
            cardList = shinaResponse.getResponseBody().cardList();
        }
        return cardList;
    }

    @Override
    public DebitCardOrderResponse createDebitCardOrder(DebitCardOrderRequest request) {
        return request.isDelivery() ? delivery(request) : takeAway(request);
    }

    private DebitCardOrderResponse takeAway(DebitCardOrderRequest request) {
        DataDTO<UserInfoDTO> userInfoDTODataDTO = checkReferralCodeExists(request);

        CardType cardType = cardTypeService.findByIdAndActiveTrue(request.cardTypeId());

        Branch branch = branchService.findTop1ByCardTypeId(cardType.getId(), request.branchId());

        UserInfoDTO userInfo = userService.getUserInfo(userSession.requireUserDetails().id());
        //    cardTypeService.checkCardType(cardType, userInfo.getIabsId());
        checkCardCount();

        CardFormDTO cardFormDTO =
                cardService.getCardWithBalance(
                        new CardWithBalanceRequest(request.cardId(), userInfo.getId(), userInfo.getPnfl()));
        myIdService.checkResidentUser(userInfo);
        cardService.checkCardEnough(cardFormDTO, cardType.getCardPrice());

        //    if (!userInfo.getId().equals(2652120L)) {
        //      checkActiveUserOrder();  //check user active orders
        //    }
        checkActiveUserOrder(); // check user active orders

        String type = generateParamType(cardFormDTO);
        OperationParam operationParam = operationParamService.findByOperationType(type);
        checkOperationParamStatus(operationParam);

        if (request.prepare()) {
            return new DebitCardOrderResponse(
                    null,
                    "success",
                    new ArrayList<>() {
                        {
                            add(
                                    new Additional(
                                            "amount", String.valueOf(cardType.getCardPrice()), "amount", null, null));
                            add(
                                    new Additional(
                                            "currency", cardType.getCurrency().name(), "currency", null, null));
                        }
                    });
        }

        //        if (Objects.nonNull(request.code()) && !request.code().isEmpty()) {
        //            BaseResponse<String> response = smsService.verify(cardFormDTO.getPhoneNumber(),
        // request.code(), UUID.randomUUID(), userInfo.getId(), userSession.getRequestHeaders().lang());
        //            if (!response.isSuccess()) {
        //                throw ServiceException.with400(response.getError().code());
        //            }
        //        } else {
        //            BaseResponse<String> response = smsService.send(cardFormDTO.getPhoneNumber(),
        // null, UUID.randomUUID());
        //            if (response.isSuccess()) {
        //                return new DebitCardOrderResponse("successfully", "pending", null);
        //            } else {
        //                throw ServiceException.with400(response.getError().code());
        //            }
        //        }

        //Todo-------------------- shu joyni tekshirishim kerak ------------------------------
        UUID uuid = UUID.randomUUID();
        OrderParam orderParam =
                makeInitialOrderParam4CreateDebitCard(
                        request.isDelivery(), userInfo, cardType, null, uuid.toString(), branch);

        Operation operation =
                makeInitialOperation4CreateDebitCard(
                        request.isDelivery(), userInfo, cardFormDTO, cardType, null, operationParam, uuid);

        orderParam.setOperationId(operation.getId());
        OrderParam savedOrderParam = orderParamRepository.save(orderParam);

        OperationOrderParamHolder holder =
                p2sExecutor(
                        request.referralCode(), // referralCode
                        userInfoDTODataDTO,     // referralCode user
                        request.isDelivery(),
                        operation,
                        cardFormDTO,
                        userInfo,
                        operationParam,
                        savedOrderParam,
                        branch,
                        cardType,
                        null,
                        null);

        return baseResponseGenerator(holder.orderParam());
    }

    private DataDTO<UserInfoDTO> checkReferralCodeExists(DebitCardOrderRequest request) {
        DataDTO<UserInfoDTO> userInfoDTODataDTO = null;
        if (request.referralCode() != null && !request.referralCode().isBlank()) {
            userInfoDTODataDTO = referralService.checkReferral(request.referralCode());
//            if (!userInfoDTODataDTO.isSuccess()){
//                throw ServiceException.with400(ErrorCode.REFERRAL_CODE_INVALID_ERROR);
//            }
        }
        return userInfoDTODataDTO;
    }

    private DebitCardOrderResponse delivery(DebitCardOrderRequest request) {

        System.out.println("delivery-enabled: " + cardFeatures.deliveryEnabled());
        if (!cardFeatures.deliveryEnabled()) {
            String message = messageUtil.get(MessageCode.SERVICE_TEMPORARILY_UNAVAILABLE);
            throw ServiceException.with400(ErrorCode.SERVICE_TEMPORARILY_UNAVAILABLE, message);

            //yoki
	/*
		return new DebitCardOrderResponse(
              		message,
              		"error",
              		Collections.singletonList(
                      		new Additional("message", message, "service_unavailable")
              		)
      		);
	*/
        }

        DataDTO<UserInfoDTO> userInfoDTODataDTO = checkReferralCodeExists(request);

        if (request.address() == null) {
            throw ServiceException.with400(ErrorCode.INPUT_CAN_NOT_BE_NULL);
        }
        if (request.providerId() == null) {
            throw ServiceException.with400(ErrorCode.INPUT_CAN_NOT_BE_NULL);
        }

        CardType cardType = cardTypeService.findByIdAndActiveTrue(request.cardTypeId());
        if (!cardType.getDeliveryService()) {
            throw ServiceException.with400(ErrorCode.OPERATION_PARAM_STATUS_INACTIVE);
        }

        Branch branch =
                branchService.findTop1ByCardTypeIdAndMainTrue(cardType.getId(), request.branchId());
        DeliveryProvider provider =
                deliveryProviderRepository
                        .findByIdAndActiveTrue(request.providerId())
                        .orElseThrow(() -> ServiceException.with400(ErrorCode.PROVIDER_NOT_FOUND));

        if (!branchProviderRepository.existsByBranchIdAndProviderId(
                branch.getBranchId(), provider.getId())) {
            throw ServiceException.with400(ErrorCode.PROVIDER_NOT_FOUND);
        }

        UserInfoDTO userInfo = userService.getUserInfo(userSession.requireUserDetails().id());
        //    cardTypeService.checkCardType(cardType, userInfo.getIabsId());
        checkCardCount();

        CardFormDTO cardFormDTO =
                cardService.getCardWithBalance(
                        new CardWithBalanceRequest(request.cardId(), userInfo.getId(), userInfo.getPnfl()));
        myIdService.checkResidentUser(userInfo);

        long amount = cardType.getCardPrice() + provider.getAmount(); // full amount

        cardService.checkCardEnough(cardFormDTO, amount);

        checkActiveUserOrder(); // check user active orders

        String type = generateParamType(cardFormDTO);
        OperationParam operationParam = operationParamService.findByOperationType(type);
        checkOperationParamStatus(operationParam);

        if (request.prepare()) {
            return new DebitCardOrderResponse(
                    null,
                    "success",
                    new ArrayList<>() {
                        {
                            add(new Additional("address", request.address().address(), "address"));

                            add(
                                    new Additional(
                                            "card_amount", String.valueOf(cardType.getCardPrice()), "card_amount"));

                            add(
                                    new Additional(
                                            "delivery_amount", String.valueOf(provider.getAmount()), "delivery_amount"));

                            add(new Additional("total_amount", String.valueOf(amount), "total_amount"));

                            add(new Additional("currency", cardType.getCurrency().name(), "currency"));
                        }
                    });
        }

        //        if (Objects.nonNull(request.code()) && !request.code().isEmpty()) {
        //            BaseResponse<String> response = smsService.verify(cardFormDTO.getPhoneNumber(),
        // request.code(), UUID.randomUUID(), userInfo.getId(), userSession.getRequestHeaders().lang());
        //            if (!response.isSuccess()) {
        //                throw ServiceException.with400(response.getError().code());
        //            }
        //        } else {
        //            BaseResponse<String> response = smsService.send(cardFormDTO.getPhoneNumber(),
        // null, UUID.randomUUID());
        //            if (response.isSuccess()) {
        //                return new DebitCardOrderResponse("successfully", "pending", null);
        //            } else {
        //                throw ServiceException.with400(response.getError().code());
        //            }
        //        }

        UUID uuid = UUID.randomUUID();
        OrderParam orderParam =
                makeInitialOrderParam4CreateDebitCard(
                        request.isDelivery(),
                        userInfo,
                        cardType,
                        provider,
                        uuid.toString(),
                        branch); // 51302015730038

        //todo==============
        AddressParam addressParam =
                orderParamMapper.toAddressParamEntity(request.address(), orderParam);
        AddressParam savedAddress = addressParamRepository.save(addressParam);

        Operation operation =
                makeInitialOperation4CreateDebitCard(
                        request.isDelivery(), userInfo, cardFormDTO, cardType, provider, operationParam, uuid);
        orderParam.setOperationId(operation.getId());
        OrderParam savedOrderParam = orderParamRepository.save(orderParam);

        OperationOrderParamHolder holder =
                p2sExecutor(
                        request.referralCode(), // referralCode
                        userInfoDTODataDTO,     // referralCode user
                        request.isDelivery(),
                        operation,
                        cardFormDTO,
                        userInfo,
                        operationParam,
                        savedOrderParam,
                        branch,
                        cardType,
                        provider,
                        savedAddress);

        return baseResponseGenerator(holder.orderParam());
    }

    private DebitCardOrderResponse baseResponseGenerator(OrderParam orderParam) {
        if (orderParam.getStatus() == OrderStatus.COMPLETED
                || orderParam.getStatus() == OrderStatus.PENDING) {
            return new DebitCardOrderResponse(
                    messageUtil.get(MessageCode.CREATE_CARD_ORDER_SUCCESS), "success", null);

        } else {
            if (orderParam.getDetails() != null
                    && !orderParam.getDetails().isEmpty()
                    && (orderParam.getDetails().contains(LiteralUtil.LIMIT_SHINA)
                    || orderParam.getDetails().contains(LiteralUtil.LIMIT_SHINA_2)
                    || orderParam.getDetails().contains(LiteralUtil.LIMIT_SHINA_3))) {
                if (orderParam.getDetails().contains(LiteralUtil.LIMIT_20)) {
                    throw ServiceException.with400(ErrorCode.USER_CARD_LIMIT_ERROR_20);
                } else {
                    throw ServiceException.with400(ErrorCode.USER_CARD_LIMIT_ERROR);
                }
            }
            if (orderParam.getDetails() != null
                    && !orderParam.getDetails().isEmpty()
                    && BaseUtils.canParseToObject(
                    orderParam.getDetails(), new TypeReference<BaseShinaResponse<Object>>() {
                    })) {
                BaseShinaResponse<Object> shinaResponse =
                        BaseUtils.toObjectFromJson(orderParam.getDetails(), new TypeReference<>() {
                        });
                /**
                 * shina dan success response qaytganda code=0 buladi.
                 * ya'ni yangi karta yaratilsa code=0 buladi.
                 * */
                if (shinaResponse.getCode() == 0) {
                    new DebitCardOrderResponse(
                            messageUtil.get(MessageCode.CREATE_CARD_ORDER_SUCCESS),
                            "success",
                            null
                    );
//          return new DebitCardOrderResponse(
//                  shinaResponse.getMsg(),
//                  "success",
//                  Collections.emptyList()
//          );
                }
                if (shinaResponse.getMsg() != null || shinaResponse.getOraMsg() != null) {
                    throw new CustomBadRequestException(
                            shinaResponse.getMsg() != null ? shinaResponse.getMsg() : shinaResponse.getOraMsg());
                }
            }
            throw ServiceException.with400(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void checkActiveUserOrder() {
        List<OrderParam> userOrder =
                orderParamRepository.findAllByUserId(
                        userSession.getUserInfo().getId(),
                        OrderStatus.COMPLETED,
                        MessageCode.STATUS_ORDER_PARAM_ERROR);
        if (!userOrder.isEmpty()) {
            throw ServiceException.with400(ErrorCode.HAVE_ACTIVE_ORDER_FOR_THIS_USERS);
        }
    }

    private OrderParam makeInitialOrderParam4CreateDebitCard(
            boolean isDelivery,
            UserInfoDTO userInfo,
            CardType cardType,
            DeliveryProvider provider,
            String operationUid,
            Branch branch) {

        OrderParam orderParam = new OrderParam();
        orderParam.setUid(operationUid);
        orderParam.setCardTypeId(cardType.getId());
        orderParam.setUserId(userInfo.getId());
        orderParam.setOwnerName(userInfo.getLastname() + " " + userInfo.getFirstname());
        orderParam.setTitle(cardType.getTitle());
        orderParam.setStatus(OrderStatus.STARTED);
        orderParam.setOrderType(OrderType.DEBIT_CARD);
        orderParam.setDeliveryType(isDelivery ? DeliveryType.DELIVERY_SERVICE : DeliveryType.TAKE_AWAY);
        orderParam.setBranchId(branch.getId());
        orderParam.setCardActionType(CardActionType.NEW_CARD);
        orderParam.setCardAmount(cardType.getCardPrice());
        orderParam.setDeliveryAmount(isDelivery ? provider.getAmount() : 0L);
        orderParam.setProviderId(isDelivery ? provider.getId() : null);
        orderParam.setState(MessageCode.STATUS_NEW); // MessageCode.STATUS_0
        orderParam.setIabsClientId(
                Objects.nonNull(userInfo.getIabsId()) ? Long.parseLong(userInfo.getIabsId()) : null);
        orderParam.setStartTime(LocalDateTime.now());
        orderParam.setCreatedBy(
                userSession.getUserInfo().getFirstname() + " " + userSession.getUserInfo().getLastname());
        orderParam.setUpdatedBy(
                userSession.getUserInfo().getFirstname() + " " + userSession.getUserInfo().getLastname());
        orderParam.setUpdatedAt(LocalDateTime.now());
        orderParam.setCreatedAt(LocalDateTime.now());
        return orderParamRepository.save(orderParam);
    }

    private Operation makeInitialOperation4CreateDebitCard(
            boolean isDelivery,
            UserInfoDTO userInfo,
            CardFormDTO cardFormDTO,
            CardType cardType,
            DeliveryProvider provider,
            OperationParam operationParam,
            UUID uuid) {

        Operation operation = new Operation();
        operation.setUid(uuid);
        operation.setUserId(userInfo.getId());
        operation.setSender(cardFormDTO.getPan());
        operation.setReceiver("IABS");

        operation.setReceiverName(messageUtil.get(MessageCode.CARD_ORDER_OPERATION_RECEIVER_NAME));

        operation.setAmount(
                isDelivery ? cardType.getCardPrice() + provider.getAmount() : cardType.getCardPrice());
        operation.setCurrency(Currency.UZS);
        operation.setFeeAmount(0L);
        operation.setFeeCurrency(Currency.UZS);
        operation.setType(OperationType.valueOf(operationParam.getType()));
        operation.setMode(OperationMode.P2S);

        Payload payload = new Payload();

        payload.setCardId(cardFormDTO.getId());
        payload.setSupplierId(4516L);
        operation.setPayload(payload);

        operation.setStatus(OperationStatus.PROCESSING);
        return operationRepository.save(operation);
    }

    private OperationOrderParamHolder p2sExecutor(
            String referralCode,
            DataDTO<UserInfoDTO> userInfoDTODataDTO,
            boolean isDelivery,
            Operation operation,
            CardFormDTO cardFormDTO,
            UserInfoDTO userInfo,
            OperationParam operationParam,
            OrderParam orderParam,
            Branch branch,
            CardType cardType,
            DeliveryProvider provider,
            AddressParam address) {

        OperationOrderParamHolder holder;

        if (operationParam.getType().startsWith("U")) {

            holder =
                    uzcardCardPayment(
                            referralCode,
                            userInfoDTODataDTO,
                            isDelivery,
                            operation,
                            cardFormDTO,
                            userInfo,
                            orderParam,
                            branch,
                            cardType,
                            provider,
                            address,
                            operationParam);

        } else if (operationParam.getType().startsWith("H")) {

            holder =
                    humoPayment(
                            referralCode,
                            userInfoDTODataDTO,
                            isDelivery,
                            operation,
                            cardFormDTO,
                            userInfo,
                            orderParam,
                            branch,
                            cardType,
                            provider,
                            address,
                            operationParam);

        } else if (operationParam.getType().startsWith("W")) {

            holder =
                    wayPayment(
                            referralCode,
                            userInfoDTODataDTO,
                            isDelivery,
                            operation,
                            cardFormDTO,
                            userInfo,
                            orderParam,
                            branch,
                            cardType,
                            provider,
                            address,
                            operationParam);

        } else {
            throw ServiceException.with400(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return holder;
    }

    public OrderParam sendToDeliveryTakeAwayDataToCrobs(
            boolean isDelivery,
            Transaction transaction,
            Branch branch,
            CardType cardType,
            DeliveryProvider provider,
            AddressParam address,
            OrderParam orderParam,
            UserInfoDTO userInfo) {

        orderParam.setRequestId(transaction.getRequestId());
        if (orderParam.getStatus() == OrderStatus.OPERATION_DONE) {

            try {
                CrobsResponse<CrobsDeliveryTakeAwayResponse> crobsResponse =
                        crobsService.sendToCrobsDebitCardInfo(
                                isDelivery,
                                transaction.getRequestId(),
                                branch,
                                cardType,
                                orderParam,
                                address,
                                provider,
                                userInfo);
                if (crobsResponse.success()) {

                    orderParam.setStatus(OrderStatus.PENDING);

                } else {

                    orderParam.setStatus(OrderStatus.UZ_POST_SEND_ERROR);
                }
            } catch (Exception e) {
                log.error("ERROR SEND_TO_UZ_POST: DELIVERY: {}", e.getMessage(), e);
            }
        }
        orderParam = orderParamRepository.save(orderParam);
        return orderParam;
    }

    /**
     * Debit karta buyurtmasining yetkazib berish (delivery) yoki olib ketish (take away)
     * ma’lumotlarini CROBS tizimiga yuborish uchun maxsus ishlab chiqilgan metod.
     *
     * <p>
     * Ushbu metod {@code resendingCardsInfoToCrobsBetweenDate} metodida qayta yuborish
     * (re-send) jarayonlari uchun maxsus yaratilgan bo‘lib, asl
     * {@code sendToDeliveryTakeAwayDataToCrobs} metodining muqobil va takomillashtirilgan
     * versiyasidir. Asosiy maqsad – belgilangan vaqt oralig‘ida CROBS ga yuborilmagan
     * buyurtmalarni qayta ishlash va holatni to‘g‘ri yangilash.
     * </p>
     *
     * <p>
     * <strong>Ishlash logikasi:</strong>
     * <ul>
     *   <li>{@code orderParam} holati {@link OrderStatus#OPERATION_DONE} bo‘lsa:</li>
     *   <ul>
     *     <li>{@link CrobsService#sendToCrobsDebitCardInfo} orqali CROBS ga ma’lumotlar yuboriladi.</li>
     *     <li>Muvaffaqiyatli javob kelsa – holat {@link OrderStatus#PENDING} ga o‘zgartiriladi.</li>
     *     <li>Muvaffaqiyatsiz javob kelsa – holat {@link OrderStatus#UZ_POST_SEND_ERROR} ga o‘zgartiriladi.</li>
     *     <li>Xatolik yuz bersa – faqat logga yoziladi, holat o‘zgartirilmaydi (re-send xavfsizligi uchun).</li>
     *   </ul>
     *   <li>Har qanday holatda ham yangilangan {@code orderParam} ma’lumotlar bazasiga saqlanadi.</li>
     * </ul>
     * </p>
     *
     * <p>
     * <strong>Eslatma:</strong><br>
     * - Ushbu metod faqat qayta yuborish (re-send) operatsiyalari uchun mo‘ljallangan.<br>
     * - {@code requestId} majburiy ravishda o‘rnatiladi.<br>
     * - Xatoliklar faqat logga yoziladi, exception tashlanmaydi – bu re-send jarayonida
     *   bitta orderning xatosi boshqalarga ta’sir qilmasligi uchun muhimdir.
     * </p>
     *
     * @param isDelivery {@code true} – yetkazib berish, {@code false} – olib ketish
     * @param requestId  CROBS ga yuborishda ishlatiladigan unikal identifikator
     * @param branch     buyurtma qabul qilingan filial
     * @param cardType   buyurtma qilingan karta turi
     * @param provider   yetkazib berish provayderi (null bo‘lishi mumkin)
     * @param address    yetkazib berish manzili (delivery holatida ishlatiladi, null bo‘lishi mumkin)
     * @param orderParam yangilanishi kerak bo‘lgan buyurtma obyekti
     * @param userInfo   CROBS so‘rovida qo‘shimcha yuboriladigan foydalanuvchi ma’lumotlari
     * @return ma’lumotlar bazasiga saqlangan yangilangan {@link OrderParam}
     * @author Tohir Asadov
     */
    public OrderParam sendToDeliveryTakeAwayDataToCrobs2(
            boolean isDelivery,
            String requestId,
            Branch branch,
            CardType cardType,
            DeliveryProvider provider,
            AddressParam address,
            OrderParam orderParam,
            UserInfoDTO userInfo) {

        orderParam.setRequestId(requestId);
        if (orderParam.getStatus() == OrderStatus.OPERATION_DONE) {

            try {
                CrobsResponse<CrobsDeliveryTakeAwayResponse> crobsResponse =
                        crobsService.sendToCrobsDebitCardInfo(
                                isDelivery,
                                requestId,
                                branch,
                                cardType,
                                orderParam,
                                address,
                                provider,
                                userInfo);
                if (crobsResponse.success()) {
                    log.info("--- SUCCESS ---");
                    orderParam.setStatus(OrderStatus.PENDING);

                } else {
                    orderParam.setStatus(OrderStatus.UZ_POST_SEND_ERROR);
                }
            } catch (Exception e) {
                log.error("ERROR SEND_TO_UZ_POST: DELIVERY: {}", e.getMessage(), e);
            }
        }
        orderParam = orderParamRepository.save(orderParam);
        return orderParam;
    }

    public OrderParam sendToDeliveryTakeAwayDataToCrobs3(
            boolean isDelivery,
            String requestId,
            Branch branch,
            CardType cardType,
            DeliveryProvider provider,
            AddressParam address,
            OrderParam orderParam,
            UserInfoDTO userInfo) {

        orderParam.setRequestId(requestId);
        if (orderParam.getContractId() != null) {

            try {
                CrobsResponse<CrobsDeliveryTakeAwayResponse> crobsResponse =
                        crobsService.sendToCrobsDebitCardInfo(
                                isDelivery,
                                requestId,
                                branch,
                                cardType,
                                orderParam,
                                address,
                                provider,
                                userInfo);
                if (crobsResponse.success()) {
                    log.info("--- SUCCESS 3 ---");
//                    orderParam.setStatus(OrderStatus.PENDING);

                } else {
//                    orderParam.setStatus(OrderStatus.UZ_POST_SEND_ERROR);
                }
            } catch (Exception e) {
                log.error("ERROR SEND_TO_UZ_POST: DELIVERY 3: {}", e.getMessage(), e);
            }
        }
//        orderParam = orderParamRepository.save(orderParam);
        return orderParam;
    }

    public void cancelPayment(
            String type,
            Operation operation,
            OrderParam orderParam,
            Transaction debitTransaction,
            Transaction transactionDebitCard) {

        operation.setDetails(transactionDebitCard.getDetails());
        TransactionHolderDebit debitHolder;

        if (type.equals("U")) {
            debitHolder = uzcardService.uzcardCancel(debitTransaction);
        } else if (type.equals("H")) {
            debitHolder = humoService.humoCancel(debitTransaction);
        } else {
            debitHolder = way4Service.wayCancel(debitTransaction);
        }

        if (debitHolder.success()) {
            orderParam.setStatus(OrderStatus.OPERATION_ERROR);
            operation.setStatus(OperationStatus.ERROR);
            orderParam.setState(MessageCode.STATUS_ORDER_PARAM_ERROR);
        } else {
            operation.setDetails(debitHolder.transaction().getDetails());
            operation.setStatus(OperationStatus.PROCESSING);
            orderParam.setState(MessageCode.STATUS_ORDER_PARAM_ERROR);
        }

        operationRepository.save(operation);
        orderParamRepository.save(orderParam);
    }

    public void updateOperationStatusErrorOrProcessing(
            Operation operation,
            OperationStatus operationStatus,
            OrderParam orderParam,
            OrderStatus orderStatus,
            String orderState) {

        operation.setStatus(operationStatus);
        orderParam.setStatus(orderStatus);
        orderParam.setState(orderState);
        operationRepository.save(operation);
        orderParamRepository.save(orderParam);
    }

    //todo---------- 30.01
    public void checkPayment(
            String type,
            Operation operation,
            OrderParam orderParam,
            Transaction debitTransaction,
            Transaction transactionDebitCard) {
        BaseShinaResponse<CreateCardResponse> responseCreateCard =
                shinaService.checkCreateCard(transactionDebitCard.getLogId());

        BaseShinaResponse<CreateCardResponse> responseTransaction =
                shinaService.checkCreateCard(transactionDebitCard.getRequestId());

        if (((responseCreateCard != null
                && responseCreateCard.getPerformCode() != null
                && !responseCreateCard.getPerformCode().equals("0"))
                || (responseCreateCard != null
                && responseCreateCard.getCode() != null
                && responseCreateCard.getCode() != 0
                && responseCreateCard.getMsg() != null
                && responseCreateCard.getMsg().contains("So'rov topilmadi")))
                && ((responseTransaction != null
                && responseTransaction.getPerformCode() != null
                && !responseTransaction.getPerformCode().equals("0"))
                || (responseTransaction != null
                && responseTransaction.getCode() != null
                && responseTransaction.getCode() != 0
                && responseTransaction.getMsg() != null
                && responseTransaction.getMsg().contains("So'rov topilmadi")))) {
            cancelPayment(type, operation, orderParam, debitTransaction, transactionDebitCard);
        } else {

            operation.setDetails(transactionDebitCard.getDetails());
            operation.setStatus(OperationStatus.PROCESSING);
            orderParam.setState(MessageCode.STATUS_ORDER_PARAM_ERROR);
        }
        operationRepository.save(operation);
        orderParamRepository.save(orderParam);
    }

    public OperationOrderParamHolder uzcardCardPayment(
            String referralCode,
            DataDTO<UserInfoDTO> userInfoDTODataDTO,
            boolean isDelivery,
            Operation operation,
            CardFormDTO cardFormDTO,
            UserInfoDTO userInfo,
            OrderParam orderParam,
            Branch branch,
            CardType cardType,
            DeliveryProvider provider,
            AddressParam address,
            OperationParam operationParam) {
        TransactionHolderDebit debitHolder;
        TransactionHolderShina creditHolder;
        Transaction debitTransaction;
        Transaction creditTransaction = null;

//referral_code
//        AddReferralForm addReferralForm = null;
//        if (userInfoDTODataDTO != null && userInfoDTODataDTO.isSuccess()) {
//            AddRefFormDTO requestReferral = new AddRefFormDTO(
//                    operation.getId(),
//                    "card-delivery",
//                    userInfoDTODataDTO.getData().getId(),
//                    userInfoDTODataDTO.getData().getPnfl(),
//                    referralCode,
//                    userInfo.getId(),
//                    userInfo.getPhone(),
//                    userInfo.getPnfl()
//            );
//            logService.logInfo("/referral/referralCode", "CARD-DELIVERY (uzcardPayment) referralService.saveV3 before REFERRAL: " + requestReferral);
//            addReferralForm = referralService.saveV3(requestReferral);
//            logService.logInfo("/referral/referralCode", "CARD-DELIVERY (uzcardPayment) referralService.saveV3 saved REFERRAL: " + addReferralForm);
//        }
        AddReferralForm addReferralForm = createAddReferralForm(userInfoDTODataDTO, userInfo, referralCode, operation.getId(), "uzcardPayment");

        //todo------------ uzcard pul kesadi......
        debitHolder = uzcardService.uzcardDebit(cardFormDTO, userInfo, operationParam, operation);

        if (debitHolder.success()) {

            //todo---------------- karta yaratadi va iabs shinaga prodga uradi
            creditHolder =
                    shinaService.createDebitCard(
                            isDelivery,
                            branch,
                            userInfo,
                            cardType,
                            provider,
                            orderParam,
                            operation,
                            operationParam);
            debitTransaction = debitHolder.transaction();
            if (creditHolder.success()) {

                operation.setStatus(OperationStatus.DONE);
                orderParam.setStatus(OrderStatus.OPERATION_DONE);

                //referral_code
                if (userInfoDTODataDTO != null && userInfoDTODataDTO.isSuccess() && addReferralForm != null){
                    logService.logInfo("/referral/referralCode","CARD-DELIVERY (uzcardPayment) referralService.saveV2 before REFERRAL: "+ addReferralForm);
                    AddReferralForm addReferralFormAfter = referralService.saveV2(addReferralForm);
                    logService.logInfo("/referral/referralCode", "CARD-DELIVERY (uzcardPayment) referralService.saveV2 saved REFERRAL: "+addReferralFormAfter);
                }
            }
            else {

                creditTransaction = creditHolder.transaction();

                if (creditTransaction.getStatus().equals(TransactionStatus.PROCESSING)) {

                    operation.setDetails(creditTransaction.getDetails());
                    operation.setStatus(OperationStatus.PROCESSING);
                    orderParam.setState(MessageCode.STATUS_ORDER_PARAM_ERROR);

                } else {
//todo---------- 30.01
                    checkPayment("U", operation, orderParam, debitTransaction, creditTransaction);
                }
            }

            creditTransaction = creditHolder.transaction();

        } else if (!debitHolder.error().code().equals(ExternalErrorCode.CONNECTION_ERROR.getCode())
                && !debitHolder.error().code().equals(ExternalErrorCode.UZCARD_ABSTRACT_ERROR.getCode())) {

            updateOperationStatusErrorOrProcessing(
                    operation,
                    OperationStatus.ERROR,
                    orderParam,
                    OrderStatus.OPERATION_ERROR,
                    MessageCode.STATUS_ORDER_PARAM_ERROR);

        } else {

            operation.setDetails(debitHolder.transaction().getDetails());
            updateOperationStatusErrorOrProcessing(
                    operation,
                    OperationStatus.PROCESSING,
                    orderParam,
                    OrderStatus.OPERATION_PROCESSING,
                    orderParam.getState());
        }
        operationRepository.save(operation);
        orderParam = orderParamRepository.save(orderParam);

        debitTransaction = debitHolder.transaction();

        if (orderParam.getStatus() == OrderStatus.OPERATION_DONE) {
            orderParam =
                    sendToDeliveryTakeAwayDataToCrobs(
                            isDelivery,
                            creditTransaction,
                            branch,
                            cardType,
                            provider,
                            address,
                            orderParam,
                            userInfo);
        }

        return new OperationOrderParamHolder(
                operation, orderParam, debitTransaction, creditTransaction);
    }

    //todo---------------------------------------------------------------------------
    public OperationOrderParamHolder humoPayment(
            String referralCode,
            DataDTO<UserInfoDTO> userInfoDTODataDTO,
            boolean isDelivery,
            Operation operation,
            CardFormDTO cardFormDTO,
            UserInfoDTO userInfo,
            OrderParam orderParam,
            Branch branch,
            CardType cardType,
            DeliveryProvider provider,
            AddressParam address,
            OperationParam operationParam) {
        TransactionHolderDebit debitHolder;
        TransactionHolderShina creditHolder;
        Transaction debitTransaction;
        Transaction creditTransaction = null;

        //referral_code
//        AddReferralForm addReferralForm = null;
//        if (userInfoDTODataDTO != null && userInfoDTODataDTO.isSuccess()) {
//            AddRefFormDTO requestReferral = new AddRefFormDTO(
//                    operation.getId(),
//                    "card-delivery",
//                    userInfoDTODataDTO.getData().getId(),
//                    userInfoDTODataDTO.getData().getPnfl(),
//                    referralCode,
//                    userInfo.getId(),
//                    userInfo.getPhone(),
//                    userInfo.getPnfl()
//            );
//            logService.logInfo("/referral/referralCode", "CARD-DELIVERY (humoPayment) referralService.saveV3 before REFERRAL: " + requestReferral);
//            addReferralForm = referralService.saveV3(requestReferral);
//            logService.logInfo("/referral/referralCode", "CARD-DELIVERY (humoPayment) referralService.saveV3 saved REFERRAL: " + addReferralForm);
//        }
        AddReferralForm addReferralForm = createAddReferralForm(userInfoDTODataDTO, userInfo, referralCode, operation.getId(), "humoPayment");
        debitHolder = humoService.humoDebit(cardFormDTO, userInfo, operationParam, operation);

        if (debitHolder.success()) {

            creditHolder =
                    shinaService.createDebitCard(
                            isDelivery,
                            branch,
                            userInfo,
                            cardType,
                            provider,
                            orderParam,
                            operation,
                            operationParam);
            debitTransaction = debitHolder.transaction();
            if (creditHolder.success()) {
                operation.setStatus(OperationStatus.ALMOST_DONE);
                orderParam.setStatus(OrderStatus.OPERATION_DONE);

                //referral_code
                if (userInfoDTODataDTO != null && userInfoDTODataDTO.isSuccess() && addReferralForm != null){
                    logService.logInfo("/referral/referralCode","CARD-DELIVERY (humoPayment) referralService.saveV2 before REFERRAL: "+ addReferralForm);
                    AddReferralForm addReferralFormAfter = referralService.saveV2(addReferralForm);
                    logService.logInfo("/referral/referralCode", "CARD-DELIVERY (humoPayment) referralService.saveV2 saved REFERRAL: "+addReferralFormAfter);
                }
            } else {

                creditTransaction = creditHolder.transaction();
                if (creditTransaction.getStatus().equals(TransactionStatus.PROCESSING)) {
                    operation.setDetails(creditTransaction.getDetails());
                    operation.setStatus(OperationStatus.PROCESSING);
                    orderParam.setState(MessageCode.STATUS_ORDER_PARAM_ERROR);

                } else {

                    checkPayment("H", operation, orderParam, debitTransaction, creditTransaction);
                }
            }
            creditTransaction = creditHolder.transaction();

        } else if (!debitHolder.error().code().equals(ExternalErrorCode.CONNECTION_ERROR.getCode())
                && !debitHolder.error().code().equals(ExternalErrorCode.HUMO_READ_TIMEOUT.getCode())) {

            updateOperationStatusErrorOrProcessing(
                    operation,
                    OperationStatus.ERROR,
                    orderParam,
                    OrderStatus.OPERATION_ERROR,
                    MessageCode.STATUS_ORDER_PARAM_ERROR);

        } else {

            operation.setDetails(debitHolder.transaction().getDetails());
            updateOperationStatusErrorOrProcessing(
                    operation,
                    OperationStatus.PROCESSING,
                    orderParam,
                    OrderStatus.OPERATION_PROCESSING,
                    orderParam.getState());
        }

        operationRepository.save(operation);
        orderParam = orderParamRepository.save(orderParam);
        debitTransaction = debitHolder.transaction();

        if (orderParam.getStatus() == OrderStatus.OPERATION_DONE) {
            orderParam =
                    sendToDeliveryTakeAwayDataToCrobs(
                            isDelivery,
                            creditTransaction,
                            branch,
                            cardType,
                            provider,
                            address,
                            orderParam,
                            userInfo);
        }

        return new OperationOrderParamHolder(
                operation, orderParam, debitTransaction, creditTransaction);
    }

    public OperationOrderParamHolder wayPayment(
            String referralCode,
            DataDTO<UserInfoDTO> userInfoDTODataDTO,
            boolean isDelivery,
            Operation operation,
            CardFormDTO cardFormDTO,
            UserInfoDTO userInfo,
            OrderParam orderParam,
            Branch branch,
            CardType cardType,
            DeliveryProvider provider,
            AddressParam address,
            OperationParam operationParam) {

        TransactionHolderDebit debitHolder;
        TransactionHolderShina creditHolder;
        Transaction debitTransaction;
        Transaction creditTransaction = null;

//        AddReferralForm addReferralForm = null;
//        if (userInfoDTODataDTO != null && userInfoDTODataDTO.isSuccess()) {
//            AddRefFormDTO requestReferral = new AddRefFormDTO(
//                    operation.getId(),
//                    "card-delivery",
//                    userInfoDTODataDTO.getData().getId(),
//                    userInfoDTODataDTO.getData().getPnfl(),
//                    referralCode,
//                    userInfo.getId(),
//                    userInfo.getPhone(),
//                    userInfo.getPnfl()
//            );
//            logService.logInfo("/referral/referralCode", "CARD-DELIVERY (wayPayment) referralService.saveV3 before REFERRAL: " + requestReferral);
//            addReferralForm = referralService.saveV3(requestReferral);
//            logService.logInfo("/referral/referralCode", "CARD-DELIVERY (wayPayment) referralService.saveV3 saved REFERRAL: " + addReferralForm);
//        }

        AddReferralForm addReferralForm = createAddReferralForm(userInfoDTODataDTO, userInfo, referralCode, operation.getId(), "wayPayment");

        debitHolder = way4Service.wayDebit(cardFormDTO, userInfo, operationParam, operation);

        if (debitHolder.success()) {

            creditHolder =
                    shinaService.createDebitCard(
                            isDelivery,
                            branch,
                            userInfo,
                            cardType,
                            provider,
                            orderParam,
                            operation,
                            operationParam);
            debitTransaction = debitHolder.transaction();
            if (creditHolder.success()) {
                operation.setStatus(OperationStatus.DONE);
                orderParam.setStatus(OrderStatus.OPERATION_DONE);
                if (userInfoDTODataDTO != null && userInfoDTODataDTO.isSuccess() && addReferralForm != null){
                    logService.logInfo("/referral/referralCode","CARD-DELIVERY (wayPayment) referralService.saveV2 before REFERRAL: "+ addReferralForm);
                    AddReferralForm addReferralFormAfter = referralService.saveV2(addReferralForm);
                    logService.logInfo("/referral/referralCode", "CARD-DELIVERY (wayPayment) referralService.saveV2 saved REFERRAL: "+addReferralFormAfter);
                }
            } else {
                creditTransaction = creditHolder.transaction();
                if (creditTransaction.getStatus().equals(TransactionStatus.PROCESSING)) {
                    operation.setStatus(OperationStatus.PROCESSING);
                    operation.setDetails(creditTransaction.getDetails());
                    orderParam.setState(MessageCode.STATUS_ORDER_PARAM_ERROR);
                } else {

                    checkPayment("W", operation, orderParam, debitTransaction, creditTransaction);
                }
            }

            creditTransaction = creditHolder.transaction();

        } else if (!debitHolder.error().code().equals(ExternalErrorCode.CONNECTION_ERROR.getCode())
                && !debitHolder.error().code().equals(ExternalErrorCode.UZCARD_ABSTRACT_ERROR.getCode())) {

            updateOperationStatusErrorOrProcessing(
                    operation,
                    OperationStatus.ERROR,
                    orderParam,
                    OrderStatus.OPERATION_ERROR,
                    MessageCode.STATUS_ORDER_PARAM_ERROR);

        } else {

            operation.setDetails(debitHolder.transaction().getDetails());
            updateOperationStatusErrorOrProcessing(
                    operation,
                    OperationStatus.PROCESSING,
                    orderParam,
                    OrderStatus.OPERATION_PROCESSING,
                    orderParam.getState());
        }

        operationRepository.save(operation);
        orderParam = orderParamRepository.save(orderParam);
        debitTransaction = debitHolder.transaction();

        if (orderParam.getStatus() == OrderStatus.OPERATION_DONE) {
            orderParam =
                    sendToDeliveryTakeAwayDataToCrobs(
                            isDelivery,
                            creditTransaction,
                            branch,
                            cardType,
                            provider,
                            address,
                            orderParam,
                            userInfo);
        }

        return new OperationOrderParamHolder(
                operation, orderParam, debitTransaction, creditTransaction);
    }

    public void checkOperationParamStatus(OperationParam operationParam) {
        if (operationParam.getStatus() == OperationParamStatus.INACTIVE) {
            throw ServiceException.with400(ErrorCode.OPERATION_PARAM_STATUS_INACTIVE);
        }
    }

    public String generateParamType(CardFormDTO cardFormDTO) {
        StringBuilder builder = new StringBuilder();
        if (cardFormDTO.getProvider().equals("uzcard")) {
            builder.append("U");
        }
        if (cardFormDTO.getProvider().equals("way4")) {
            builder.append("W");
        }
        if (cardFormDTO.getProvider().equals("humo")) {
            builder.append("H");
        }
        builder.append(cardFormDTO.getCurrency().equals("USD") ? "F" : "N");
        builder.append(cardFormDTO.getIsSqb() ? "B" : "U");
        builder.append("2OCARD");
        return builder.toString();
    }

    public AddReferralForm createAddReferralForm(DataDTO<UserInfoDTO> userInfoDTODataDTO,UserInfoDTO userInfo,String referralCode,Long operationId,String logTitle){
        AddReferralForm addReferralForm = null;
        if (userInfoDTODataDTO != null && userInfoDTODataDTO.isSuccess()) {
            AddRefFormDTO requestReferral = new AddRefFormDTO(
                    operationId,
                    "card-delivery",
                    userInfoDTODataDTO.getData().getId(),
                    userInfoDTODataDTO.getData().getPnfl(),
                    referralCode,
                    userInfo.getId(),
                    userInfo.getPhone(),
                    userInfo.getPnfl()
            );
            logService.logInfo("/referral/referralCode", "CARD-DELIVERY ("+ logTitle +") referralService.saveV3 before REFERRAL: " + requestReferral);
            addReferralForm = referralService.saveV3(requestReferral);
            logService.logInfo("/referral/referralCode", "CARD-DELIVERY ("+ logTitle +") referralService.saveV3 saved REFERRAL: " + addReferralForm);
        }
        return addReferralForm;
    }
}
