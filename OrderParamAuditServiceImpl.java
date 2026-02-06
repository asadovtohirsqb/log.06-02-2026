package uz.sqb.joyda.carddeliveryservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.sqb.joyda.carddeliveryservice.configuration.security.session.UserSession;
import uz.sqb.joyda.carddeliveryservice.constant.OrderStatus;
import uz.sqb.joyda.carddeliveryservice.domain.card_delivery.OrderParam;
import uz.sqb.joyda.carddeliveryservice.domain.card_delivery.OrderParamAudit;
import uz.sqb.joyda.carddeliveryservice.exception.ErrorCode;
import uz.sqb.joyda.carddeliveryservice.payload.adminOrderView.AdminOrderViewAdminPanelDto;
import uz.sqb.joyda.carddeliveryservice.payload.adminOrderView.OrderParamStatusUpdater;
import uz.sqb.joyda.carddeliveryservice.payload.user.UserInfoDTO;
import uz.sqb.joyda.carddeliveryservice.repository.card_delivery.OrderParamAuditRepository;
import uz.sqb.joyda.carddeliveryservice.repository.card_delivery.OrderParamRepository;
import uz.sqb.joyda.carddeliveryservice.service.OrderParamAuditService;
import uz.sqb.joyda.commons.errorhandler.exceptions.ServiceException;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderParamAuditServiceImpl implements OrderParamAuditService {
    private final OrderParamAuditRepository orderParamAuditRepository;
    private final OrderParamRepository orderParamRepository;
    private final UserSession userSession;
    private final ObjectMapper objectMapper;


    @Override
    public AdminOrderViewAdminPanelDto updateStatus(OrderParamStatusUpdater updater) throws JsonProcessingException {
        OrderParam orderParam = orderParamRepository.findById(updater.orderId()).get();

        OrderStatus oldStatus = orderParam.getStatus();
        OrderStatus newStatus = updater.status();

        if (oldStatus == newStatus) {
            throw ServiceException.with400(ErrorCode.ADMIN_ORDER_PARAM_STATUS_CHANGING_ERROR);
        }

        // Eski holatni JSON qilish
        String oldDataJson = objectMapper.writeValueAsString(orderParam);

        // Statusni yangilash
        orderParam.setStatus(newStatus);

        // OrderParam ni saqlash
        OrderParam newOrderParam = orderParamRepository.save(orderParam);

        // Yangi holatni JSON qilish
        String newDataJson = objectMapper.writeValueAsString(newOrderParam);

        // Hozirgi user ma'lumotlarini olish va JSON qilish
        UserInfoDTO currentUser = userSession.getUserInfo();

        String changedByJson = objectMapper.writeValueAsString(currentUser);

        // Audit yozish
        OrderParamAudit audit = OrderParamAudit.builder()
                .orderParamId(updater.orderId())
                .oldData(oldDataJson)
                .newData(newDataJson)
                .changedAt(LocalDateTime.now())
                .changedBy(changedByJson)  // JSON string sifatida saqlanadi
                .build();

        orderParamAuditRepository.save(audit);

        return null;
    }
}

/*
@Service
@RequiredArgsConstructor
public class AdminOrderViewServiceImpl implements AdminOrderViewService {

    private final OrderParamRepository orderParamRepository;
    private final OrderParamAuditRepository orderParamAuditRepository;
    private final AdminOrderViewMapper mapper;
    private final UserSession userSession;
    private final ObjectMapper objectMapper;  // Jackson ObjectMapper

    @Override
    @Transactional
    public AdminOrderViewAdminPanelDto updateStatus(Long orderId, UpdateOrderStatusRequest request) {
        OrderParam order = orderParamRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = request.newStatus();

        if (oldStatus == newStatus) {
            throw new RuntimeException("New status is the same as old status");
        }

        // Eski holatni JSON qilish
        String oldDataJson = objectMapper.writeValueAsString(order);

        // Statusni yangilash
        order.setStatus(newStatus);

        // Yangi holatni JSON qilish
        String newDataJson = objectMapper.writeValueAsString(order);

        // Hozirgi user ma'lumotlarini olish va JSON qilish
        UserInfoDTO currentUser = userSession.getUserInfo();
        String changedByJson = objectMapper.writeValueAsString(currentUser);

        // Audit yozish
        OrderParamAudit audit = OrderParamAudit.builder()
                .orderParamId(orderId)
                .oldData(oldDataJson)
                .newData(newDataJson)
                .changedAt(LocalDateTime.now())
                .changedBy(changedByJson)  // JSON string sifatida saqlanadi
                .build();

        orderParamAuditRepository.save(audit);

        // OrderParam ni saqlash
        orderParamRepository.save(order);

        return mapper.toAdminOrderViewAdminPanelDto(order);
    }
}

* */