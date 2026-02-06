package uz.sqb.joyda.carddeliveryservice.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.sqb.joyda.carddeliveryservice.configuration.security.session.UserSession;
import uz.sqb.joyda.carddeliveryservice.constant.*;
import uz.sqb.joyda.carddeliveryservice.criteria.OrderParamCriteria;
import uz.sqb.joyda.carddeliveryservice.domain.card_delivery.AddressParam;
import uz.sqb.joyda.carddeliveryservice.domain.card_delivery.Branch;
import uz.sqb.joyda.carddeliveryservice.domain.card_delivery.CardType;
import uz.sqb.joyda.carddeliveryservice.domain.card_delivery.DeliveryProvider;
import uz.sqb.joyda.carddeliveryservice.domain.card_delivery.OpeningCardInfo;
import uz.sqb.joyda.carddeliveryservice.domain.card_delivery.OrderParam;
import uz.sqb.joyda.carddeliveryservice.domain.sqb_mobile.Operation;
import uz.sqb.joyda.carddeliveryservice.domain.sqb_mobile.OperationParam;
import uz.sqb.joyda.carddeliveryservice.domain.sqb_mobile.Transaction;
import uz.sqb.joyda.carddeliveryservice.exception.ErrorCode;
import uz.sqb.joyda.carddeliveryservice.mapper.OrderParamMapper;
import uz.sqb.joyda.carddeliveryservice.payload.base.PageDTO;
import uz.sqb.joyda.carddeliveryservice.payload.card.CardAddListRequest;
import uz.sqb.joyda.carddeliveryservice.payload.card.CardAddListResponse;
import uz.sqb.joyda.carddeliveryservice.payload.card.CardAddRequest;
import uz.sqb.joyda.carddeliveryservice.payload.card.CardAddResponse;
import uz.sqb.joyda.carddeliveryservice.payload.card_delivery.UserOrder;
import uz.sqb.joyda.carddeliveryservice.payload.crobs.CardStatusInfo;
import uz.sqb.joyda.carddeliveryservice.payload.crobs.CardStatusInfoDelivery;
import uz.sqb.joyda.carddeliveryservice.payload.crobs.CrobsResponse;
import uz.sqb.joyda.carddeliveryservice.payload.crobs.CrobsRows;
import uz.sqb.joyda.carddeliveryservice.payload.crobs.CrobsRowsDelivery;
import uz.sqb.joyda.carddeliveryservice.payload.crobs.UzpostResponse;
import uz.sqb.joyda.carddeliveryservice.payload.crobs.delivery.CrobsDeliveryTakeAwayResponse;
import uz.sqb.joyda.carddeliveryservice.payload.holder.TransactionHolderShina;
import uz.sqb.joyda.carddeliveryservice.payload.order.CurrentOrder;
import uz.sqb.joyda.carddeliveryservice.payload.order.OrderParamDTO;
import uz.sqb.joyda.carddeliveryservice.payload.shina.TransactionsResponse;
import uz.sqb.joyda.carddeliveryservice.payload.shina.base.BaseShinaResponse;
import uz.sqb.joyda.carddeliveryservice.payload.user.UserInfoDTO;
import uz.sqb.joyda.carddeliveryservice.property.ServiceProperties;
import uz.sqb.joyda.carddeliveryservice.repository.card_delivery.AddressParamRepository;
import uz.sqb.joyda.carddeliveryservice.repository.card_delivery.BranchRepository;
import uz.sqb.joyda.carddeliveryservice.repository.card_delivery.CardTypeRepository;
import uz.sqb.joyda.carddeliveryservice.repository.card_delivery.DeliveryProviderRepository;
import uz.sqb.joyda.carddeliveryservice.repository.card_delivery.OpeningCardInfoRepository;
import uz.sqb.joyda.carddeliveryservice.repository.card_delivery.OrderParamRepository;
import uz.sqb.joyda.carddeliveryservice.repository.card_delivery.specification.OrderParamSpecification;
import uz.sqb.joyda.carddeliveryservice.repository.sqb_mobile.OperationParamRepository;
import uz.sqb.joyda.carddeliveryservice.repository.sqb_mobile.OperationRepository;
import uz.sqb.joyda.carddeliveryservice.repository.sqb_mobile.TransactionRepository;
import uz.sqb.joyda.carddeliveryservice.service.BranchService;
import uz.sqb.joyda.carddeliveryservice.service.CardService;
import uz.sqb.joyda.carddeliveryservice.service.CardTypeService;
import uz.sqb.joyda.carddeliveryservice.service.CrobsService;
import uz.sqb.joyda.carddeliveryservice.service.LogService;
import uz.sqb.joyda.carddeliveryservice.service.OrderParamService;
import uz.sqb.joyda.carddeliveryservice.service.ShinaService;
import uz.sqb.joyda.carddeliveryservice.service.UserService;
import uz.sqb.joyda.carddeliveryservice.utils.BaseUtils;
import uz.sqb.joyda.carddeliveryservice.utils.MessageCode;
import uz.sqb.joyda.commons.errorhandler.exceptions.ServiceException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderParamServiceImpl implements OrderParamService {

  private final OrderParamRepository orderParamRepository;
  private final CrobsService crobsService;
  private final CardTypeService cardTypeService;
  private final BranchService branchService;
  private final BranchRepository branchRepository;
  private final OrderParamMapper orderParamMapper;
  private final UserSession userSession;
  private final OpeningCardInfoRepository openingCardInfoRepository;
  private final CardService cardService;
  private final ShinaService shinaService;
  private final UserService userService;
  private final CardTypeRepository cardTypeRepository;
  private final ServiceProperties serviceProperties;
  private final LogService logService;
  private final OrderParamSpecification orderParamSpecification;
  private final DeliveryProviderRepository deliveryProviderRepository;
  private final OperationRepository operationRepository;
  private final OperationParamRepository operationParamRepository;
  private final TransactionRepository transactionRepository;
  private final AddressParamRepository addressParamRepository;


  @Override
  public OrderParamDTO changeOrderStatus(Long orderId) {

    OrderParam orderParam = orderParamRepository.findById(orderId)
        .orElseThrow(() -> ServiceException.with400(
            ErrorCode.ORDER_PARAM_NOT_FOUND));

    if (orderParam.getStatus() == OrderStatus.CARD_CREATED_ERROR) {
    } else if (orderParam.getStatus() == OrderStatus.UZ_POST_SEND_ERROR) {
      //todo--------------------
    } else if (orderParam.getStatus() == OrderStatus.SHINA_CREDIT_TRANSACTION_ERROR) {
    }
    return orderParamMapper.toOrderParamDTO(orderParam);
  }


  @Override
  public OrderParamDTO getOrderById(Long orderId) {
    OrderParam orderParam = orderParamRepository.findById(orderId)
        .orElseThrow(() -> ServiceException.with400(
            ErrorCode.ORDER_PARAM_NOT_FOUND));
    return orderParamMapper.toOrderParamDTO(orderParam);
  }


  @Override
  public PageDTO<OrderParamDTO> getAllOrders(OrderParamCriteria criteria) {
    Specification<OrderParam> specification = orderParamSpecification.getSpecification(criteria);
    Pageable pageable = orderParamSpecification.getPageable(criteria);
    Page<OrderParamDTO> page = orderParamRepository.findAll(specification, pageable)
        .map(orderParamMapper::toOrderParamDTO);
    return new PageDTO<>(page);
  }


  @Transactional(readOnly = true)
  @Override
  public List<UserOrder> getUserOrders(boolean isHistory) {
    List<OrderParam> byUserId;
    if (isHistory) {
      byUserId = orderParamRepository.findOrdersByUserIdExcludeToday(
          userSession.requireUserDetails().id());
    } else {
      byUserId = orderParamRepository.findTodayOrdersByUserId(
          userSession.requireUserDetails().id());
    }

    return byUserId.stream().map(order -> {
      Optional<Branch> branchOptional = branchRepository.findById(order.getBranchId());
      if (branchOptional.isEmpty()) {
        throw ServiceException.with400(ErrorCode.BRANCH_NOT_FOUND);
      }
      return orderParamMapper.toUserOrderDTO(order, branchOptional.get());
    }).toList();
  }

  @Override
  public UserOrder getUserOrderByOrderId(Long orderId) {
    OrderParam orderParam = orderParamRepository.findByIdAndUserId(orderId,
            userSession.getUserInfo().getId())
        .orElseThrow(() -> ServiceException.with400(ErrorCode.ORDER_PARAM_NOT_FOUND));
    Branch branch = branchRepository.findById(orderParam.getBranchId())
        .orElseThrow(() -> ServiceException.with400(ErrorCode.BRANCH_NOT_FOUND));
    return orderParamMapper.toUserOrderDTO(orderParam, branch);
  }

  @Override
  @Transactional(readOnly = true)
  public CurrentOrder getCurrentOrder() {
    long byUserId = orderParamRepository.countTodayOrdersByUserId(
        userSession.getUserInfo().getId());
    return new CurrentOrder(byUserId,
        orderParamRepository.existsByUserId(userSession.getUserInfo().getId()),
        serviceProperties.available());
  }

  @Transactional(value = "primaryTransactionManager")
  @Scheduled(cron = "0 */10 * * * *")
  public void runPendingStatuses() {
    log.info("Running pending_statuses");
    changeOrderParamPendingStatus();
  }


  @Transactional(value = "primaryTransactionManager")
  @Scheduled(cron = "0 */10 * * * *")
  public void runPendingStatusesDelivery() {
    log.info("Running pending_statuses (delivery)");
    changeOrderParamPendingStatusDelivery();
  }

  @Transactional(value = "primaryTransactionManager", readOnly = true)
  @Scheduled(cron = "0 */20 * * * *")
  public void runErrorOrProcessingStatuses() {
    log.info("CROBS_SEND_SCHEDULE_RUNNING (UZPOST_SEND_ERROR) status check");
    sendToUzpostStatusCheck();
  }

  @Transactional
  @Scheduled(cron = "0 */20 * * * *")
  public void runCreditTransactionStatuses() {
    log.info("Running uzpost_send_error status check");
    checkCreditTransactionStatuses();
  }

  @Transactional
  @Scheduled(cron = "0 */7 * * * *")
  public void runVirtualCardAvailable() {
    log.info("Running virtual card available");
    List<OrderParam> allByStatusIn = orderParamRepository.findAllByStatusIn(
        List.of(OrderStatus.STATUS_CHECKING));
    for (OrderParam orderParam : allByStatusIn) {
      changeOrderParamStatusChecking(orderParam);
    }
  }

  @Transactional
  @Scheduled(cron = "0 */10 * * * *")
  public void runVirtualCardErrorStatuses() {
    log.info("Running virtual card error statuses");
    List<OrderParam> allByStatusIn = orderParamRepository.findAllByStatusIn(
        List.of(OrderStatus.STATUS_CHECKED, OrderStatus.CHANGED_PHONE_NUMBER, OrderStatus.ADD_CARD_TO_MOBILE_SQB_ERROR));
    for (OrderParam orderParam : allByStatusIn) {
      CardType cardType = cardTypeRepository.findById(orderParam.getCardTypeId()).orElse(null);
      OpeningCardInfo openingCardInfo = openingCardInfoRepository.findByOrderId(orderParam.getId()).orElse(null);
      if (cardType == null ||  openingCardInfo == null) {
        log.error("ERROR_STATUS_CHECKING ERROR: {}", orderParam.getId());
        return;
      }
      OrderParam changed = addCardToMobileSQB(orderParam, cardType, openingCardInfo);
      orderParamRepository.save(changed);
    }
  }

  public void checkCreditTransactionStatuses() {
    List<OrderParam> byStatus = orderParamRepository.findAllByStatusIn(
        List.of(OrderStatus.SHINA_DELIVERY_CREDIT_TRANSACTION_ERROR)
    );
    byStatus.forEach(orderParam -> {
      String requestId = UUID.randomUUID().toString();
      Branch branch = branchRepository.findById(orderParam.getBranchId()).orElse(null);
      if (branch == null) {
        return;
      }
      DeliveryProvider provider = deliveryProviderRepository.findById(orderParam.getProviderId()).orElse(null);
      if (provider == null) {
        return;
      }

      CardType cardType = cardTypeRepository.findById(orderParam.getCardTypeId()).orElse(null);
      if (cardType == null) {
        return;
      }
      Operation operation = operationRepository.findById(orderParam.getOperationId()).orElse(null);
      if (operation == null) {
        return;
      }
      Transaction transaction = transactionRepository.findTopByOperationIdOrderByIdDesc(operation.getId()).orElse(null);

      if (transaction == null) {
        return;
      }

      OperationParam operationParam = operationParamRepository.findByOperationType(operation.getType().name()).orElse(null);

      if (operationParam == null) {
        return;
      }

      try {
        UserInfoDTO userInfoDTO = userService.getUserInfo(operation.getUserId());
        BaseShinaResponse<TransactionsResponse> shinaResponse = shinaService.creditTransaction(
            true,
            transaction,
            cardType,
            provider,
            operationParam,
            branch,
            userInfoDTO
        );

        if (shinaResponse.getCode() == 0) {

          transaction.setStatus(TransactionStatus.DONE);
          operation.setStatus(OperationStatus.DONE);
          orderParam.setStatus(OrderStatus.PENDING);

        } else {
          orderParam.setStatus(OrderStatus.SHINA_DELIVERY_CREDIT_TRANSACTION_ERROR);
        }
      } catch (Exception e) {
        log.error("ERROR DELIVERY_CREDIT_WIRING_TRANSACTION: {}", e.getMessage());
      }
      transactionRepository.save(transaction);
      operationRepository.save(operation);
      orderParamRepository.save(orderParam);
    });
  }

  public void sendToUzpostStatusCheck() {
    List<OrderParam> byStatus = orderParamRepository.findAllByStatusIn(
        List.of(OrderStatus.UZ_POST_SEND_ERROR));
    byStatus.forEach(orderParam -> {
      String requestId = UUID.randomUUID().toString();

      UserInfoDTO  userInfoDTO =  userService.getUserInfo(orderParam.getUserId());

      Branch branch = branchRepository.findById(orderParam.getBranchId()).orElse(null);
      if (branch == null) {
        return;
      }
      CardType cardType = cardTypeRepository.findById(orderParam.getCardTypeId()).orElse(null);
      if (cardType == null) {
        return;
      }
      boolean isDelivery = orderParam.getDeliveryType() == DeliveryType.DELIVERY_SERVICE;
      DeliveryProvider provider = null;
      AddressParam addressParam = null;

      if (isDelivery) {

        provider =  deliveryProviderRepository.findById(orderParam.getProviderId()).orElse(null);

        if (provider == null) {
          log.error("ERROR_STATUS_CHECKING ERROR(PROVIDER NOT FOUND): {}", orderParam.getId());
          return;
        }

        addressParam = addressParamRepository.findByOrderId(orderParam.getId()).orElse(null);
        if (addressParam == null) {
          log.error("ERROR_STATUS_CHECKING ERROR(ADDRESS_PARAM_NOT_FOUND): {}", orderParam.getId());
          return;
        }

      }
      try {

        CrobsResponse<CrobsDeliveryTakeAwayResponse> crobsResponse = crobsService.sendToCrobsDebitCardInfo(
            isDelivery,
            requestId,
            branch,
            cardType,
            orderParam,
            addressParam,
            provider,
            userInfoDTO

        );
        if (crobsResponse.success()) {

          orderParam.setStatus(OrderStatus.PENDING);

        } else {

          orderParam.setStatus(OrderStatus.UZ_POST_SEND_ERROR);

        }
      } catch (Exception e) {
        log.error("ERROR sendToUzpost: {}", e.getMessage());
      }
      orderParamRepository.save(orderParam);
    });
  }

  public void changeOrderParamStatusChecking(OrderParam orderParam) {
    Optional<OpeningCardInfo> openingCardInfoOptional = openingCardInfoRepository.findByOrderId(
        orderParam.getId());
    if (openingCardInfoOptional.isPresent()) {

      OpeningCardInfo openingCardInfo = openingCardInfoOptional.get();

      if (cardService.checkCardAvailableStatus(openingCardInfo.getCardNumber())) {
        orderParam.setStatus(OrderStatus.STATUS_CHECKED);
        orderParam = orderParamRepository.save(orderParam);
        try {
          OrderParam saved;
          CardType cardType = cardTypeService.findByIdAndActiveTrue(orderParam.getCardTypeId());
          UserInfoDTO userInfo = userService.getUserInfo(orderParam.getUserId());
          if (cardType.getCardTypes() == CardTypes.HUMO_VIRTUAL) {
            TransactionHolderShina shinaData = shinaService.addPhoneNumber(userInfo,
                openingCardInfo, cardType, orderParam);
            saved = orderParamRepository.save(shinaData.orderParam());
          } else {
            orderParam.setStatus(OrderStatus.CHANGED_PHONE_NUMBER);
            saved = orderParamRepository.save(orderParam);
          }

          OrderParam savedOrderParam = addCardToMobileSQB(saved, cardType, openingCardInfo);
        } catch (Exception e) {
          orderParam.setStatus(OrderStatus.CHANGED_PHONE_NUMBER_ERROR);
          log.error(e.getMessage());
        }
      }
    } else {
      log.error("Opening card info not found for order id {}", orderParam.getId());
    }
  }

  public OrderParam addCardToMobileSQB(OrderParam orderParam, CardType cardType,
      OpeningCardInfo cardInfo) {
    CardAddRequest cardAddRequest = new CardAddRequest();
    if (cardType.getCardTypes() == CardTypes.HUMO_VIRTUAL) {
      cardAddRequest.setCardNumber(cardInfo.getCardNumber());
      cardAddRequest.setTitle(cardType.getTitle());
      cardAddRequest.setType("humo");
      cardAddRequest.setToken(cardInfo.getCardNumber());
      cardAddRequest.setExpire(
          cardInfo.getDateExpire().substring(8) + cardInfo.getDateExpire().substring(3, 5));
    } else if (cardType.getCardTypes() == CardTypes.UZCARD_VIRTUAL) {
      cardAddRequest.setCardNumber(cardInfo.getCardNumber());
      cardAddRequest.setTitle(cardType.getTitle());
      cardAddRequest.setType("uzcard");
      cardAddRequest.setToken(cardInfo.getToken());
      cardAddRequest.setExpire(
          cardInfo.getDateExpire().substring(2) + cardInfo.getDateExpire().substring(0, 2));
    } else {
      orderParam.setStatus(OrderStatus.ADD_CARD_TO_MOBILE_SQB_ERROR);
      return orderParamRepository.save(orderParam);
    }
    CardAddListResponse response = cardService.addCardToApp(
        new CardAddListRequest(orderParam.getUserId(), Collections.singletonList(cardAddRequest)));
    if (response.cards().isEmpty()) {
      orderParam.setStatus(OrderStatus.ADD_CARD_TO_MOBILE_SQB_ERROR);
      return orderParamRepository.save(orderParam);
    }

    CardAddResponse card = response.cards().getFirst();
    if (card.addStatus() == StatusAdd.ADDED) {
      orderParam.setStatus(OrderStatus.COMPLETED);
    } else {
      orderParam.setStatus(OrderStatus.STATUS_CHECKED);
    }

    return orderParamRepository.save(orderParam);
  }

  public void changeOrderParamPendingStatus() {
    try {
      CrobsResponse<CrobsRows> crobsRowsCrobsResponse = crobsService.lastChangeStatuses();
      if (crobsRowsCrobsResponse.success() && crobsRowsCrobsResponse.data() != null
          && crobsRowsCrobsResponse.data().rows() != null && !crobsRowsCrobsResponse.data().rows()
          .isEmpty()) {
        List<CardStatusInfo> statusInfos = crobsRowsCrobsResponse.data().rows();
        statusInfos.forEach(statusInfo -> {
          if (statusInfo.contractId() != null && statusInfo.status() != null) {
            Optional<OrderParam> changingParamOptional = orderParamRepository.findByContractId(
                statusInfo.contractId());
            changingParamOptional.ifPresent(orderParam -> {
              orderParam.setState(
                  getState(String.valueOf(statusInfo.status()))
              );
              orderParam.setStatus(OrderStatus.PENDING);
              if (
                  getState(String.valueOf(statusInfo.status())).equals(MessageCode.STATUS_DELIVERED_TO_THE_CUSTOMER)
                  || statusInfo.status().equals(5) || statusInfo.status().equals(3)
              ) {
                orderParam.setStatus(OrderStatus.COMPLETED);
              }
              orderParamRepository.save(orderParam);
            });
          }
        });
      } else {
        logService.logInfo("/schedule/change_state", BaseUtils.toJson(crobsRowsCrobsResponse));
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }

  }

  public void changeOrderParamPendingStatusDelivery() {
    try {
      CrobsResponse<CrobsRowsDelivery> crobsRowsCrobsResponse = crobsService.lastChangeStatusesDelivery();
      if (crobsRowsCrobsResponse.success() && crobsRowsCrobsResponse.data() != null
          && crobsRowsCrobsResponse.data().rows() != null && !crobsRowsCrobsResponse.data().rows()
          .isEmpty()) {
        List<CardStatusInfoDelivery> statusInfos = crobsRowsCrobsResponse.data().rows();
        statusInfos.forEach(statusInfo -> {
          if (statusInfo.contractId() != null && statusInfo.crobsStatus() != null) {
            Optional<OrderParam> changingParamOptional = orderParamRepository.findByContractId(
                statusInfo.contractId());
            changingParamOptional.ifPresent(orderParam -> {
              orderParam.setState(getState(statusInfo.crobsStatus()));
              orderParam.setStatus(OrderStatus.PENDING);
              if (getState(statusInfo.crobsStatus()).equals(MessageCode.STATUS_DELIVERED_TO_THE_CUSTOMER)
              || statusInfo.crobsStatus().equals("3")) {
                orderParam.setStatus(OrderStatus.COMPLETED);
              }
              orderParamRepository.save(orderParam);
            });
          }
        });
      } else {
        logService.logInfo("/schedule/change_state", BaseUtils.toJson(crobsRowsCrobsResponse));
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }

  }


  public String getState(String stateCode) {
    return switch (stateCode) {
      case "0" -> MessageCode.STATUS_0;
      case "1" -> MessageCode.STATUS_1;
      case "2" -> MessageCode.STATUS_2;
      case "3" -> MessageCode.STATUS_3;
      case "4" -> MessageCode.STATUS_4;
      case "5" -> MessageCode.STATUS_5;
      case "7" -> MessageCode.STATUS_DELIVERED_TO_THE_CUSTOMER;
      case "CREATED" -> MessageCode.STATUS_DONT_SEND_TO_CROBS;
      case "PROCESSING" -> MessageCode.STATUS_ORDER_PARAM_PROCESSING;
      case "ERROR" -> MessageCode.STATUS_ORDER_PARAM_ERROR;
      case null -> MessageCode.STATUS_ORDER_PARAM_ERROR;
      default -> throw ServiceException.with400(ErrorCode.INTERNAL_SERVER_ERROR);
    };
  }
}
