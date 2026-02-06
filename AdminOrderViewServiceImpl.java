package uz.sqb.joyda.carddeliveryservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.sqb.joyda.carddeliveryservice.constant.CardActionType;
import uz.sqb.joyda.carddeliveryservice.constant.DeliveryType;
import uz.sqb.joyda.carddeliveryservice.constant.OrderStatus;
import uz.sqb.joyda.carddeliveryservice.constant.TransactionStatus;
import uz.sqb.joyda.carddeliveryservice.domain.card_delivery.AdminOrderView;
import uz.sqb.joyda.carddeliveryservice.mapper.AdminOrderViewMapper;
import uz.sqb.joyda.carddeliveryservice.payload.adminOrderView.AdminOrderFilter;
import uz.sqb.joyda.carddeliveryservice.payload.adminOrderView.AdminOrderViewAdminPanelDto;
import uz.sqb.joyda.carddeliveryservice.payload.adminOrderView.OrderParamStatusUpdater;
import uz.sqb.joyda.carddeliveryservice.repository.card_delivery.*;
import uz.sqb.joyda.carddeliveryservice.repository.sqb_mobile.TransactionRepository;
import uz.sqb.joyda.carddeliveryservice.service.AdminOrderViewService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminOrderViewServiceImpl implements AdminOrderViewService {
    private final AdminOrderViewRepository adminOrderViewRepository;
    private final OrderParamRepository orderParamRepository;
    private final TransactionRepository transactionRepository;
    private final BranchRepository branchRepository;
    private final CardTypeRepository cardTypeRepository;
    private final DeliveryProviderRepository deliveryProviderRepository;
    private final AdminOrderViewMapper mapper;


    @Override
    public List<AdminOrderViewAdminPanelDto> findAllForAdminPanel() {
        return adminOrderViewRepository.findAll()
                .stream()
                .map(mapper::toAdminOrderViewAdminPanelDto)
                .toList();
    }

    @Override
    public Page<AdminOrderViewAdminPanelDto> searchForAdminPanel(AdminOrderFilter filter) {
        Specification<AdminOrderView> spec = buildSpecification(filter);

        Page<AdminOrderView> page = adminOrderViewRepository.findAll(spec, filter.getPageable());

        List<AdminOrderViewAdminPanelDto> dtos = page.getContent()
                .stream()
                .map(mapper::toAdminOrderViewAdminPanelDto)
                .toList();

        return new PageImpl<>(dtos, filter.getPageable(), page.getTotalElements());
    }

    @Override
    public void orderParamStatusUpdater(OrderParamStatusUpdater updater) {


    }

    private Specification<AdminOrderView> buildSpecification(AdminOrderFilter f) {
        return Specification.where(orderIdLike(f.getOrderId()))
                .and(userIdLike(f.getUserId()))
                .and(phoneLike(f.getPhone()))
                .and(pnflLike(f.getPnfl()))
                .and(fioLike(f.getFio()))
                .and(contractIdLike(f.getContractId()))
                .and(statusIn(f.getStatuses()))
                .and(bxmCodeIn(f.getBxmCodes()))
                .and(cardActionTypeIn(f.getCardActionTypes()))
                .and(deliveryTypeIn(f.getDeliveryTypes()))
                .and(paymentIsDoneEquals(f.getPaymentIsDone()))
                .and(provodkaIsDoneEquals(f.getProvodkaIsDone()))
                .and(orderedTimeBetween(f.getOrderedTimeFrom(), f.getOrderedTimeTo()));
    }

    // Text search (LIKE '%value%')
    private Specification<AdminOrderView> orderIdLike(String value) {
        return (root, query, cb) -> value == null ? cb.conjunction()
                : cb.like(root.get("orderParamId").as(String.class), "%" + value + "%");
    }

    private Specification<AdminOrderView> userIdLike(String value) {
        return (root, query, cb) -> value == null ? cb.conjunction()
                : cb.like(root.get("userId").as(String.class), "%" + value + "%");
    }

    private Specification<AdminOrderView> phoneLike(String value) {
        return (root, query, cb) -> value == null ? cb.conjunction()
                : cb.like(cb.lower(root.get("phone")), "%" + value.toLowerCase() + "%");
    }

    private Specification<AdminOrderView> pnflLike(String value) {
        return (root, query, cb) -> value == null ? cb.conjunction()
                : cb.like(root.get("pnfl"), "%" + value + "%");
    }

    private Specification<AdminOrderView> fioLike(String value) {
        return (root, query, cb) -> value == null ? cb.conjunction()
                : cb.like(cb.lower(root.get("fio")), "%" + value.toLowerCase() + "%");
    }

    private Specification<AdminOrderView> contractIdLike(String value) {
        return (root, query, cb) -> value == null ? cb.conjunction()
                : cb.like(root.get("contractId").as(String.class), "%" + value + "%");
    }

    // Dropdown / In
    private Specification<AdminOrderView> statusIn(List<OrderStatus> values) {
        return (root, query, cb) -> values == null || values.isEmpty() ? cb.conjunction()
                : root.get("status").in(values);
    }

    private Specification<AdminOrderView> bxmCodeIn(List<String> values) {
        return (root, query, cb) -> values == null || values.isEmpty() ? cb.conjunction()
                : root.get("bxmCode").in(values);
    }

    private Specification<AdminOrderView> cardActionTypeIn(List<CardActionType> values) {
        return (root, query, cb) -> values == null || values.isEmpty() ? cb.conjunction()
                : root.get("cardActionType").in(values);
    }

    private Specification<AdminOrderView> deliveryTypeIn(List<DeliveryType> values) {
        return (root, query, cb) -> values == null || values.isEmpty() ? cb.conjunction()
                : root.get("deliveryType").in(values);
    }

    // Boolean equals
//    private Specification<AdminOrderView> paymentIsDoneEquals(Boolean value) {
//        return (root, query, cb) -> value == null ? cb.conjunction()
//                : cb.equal(root.get("paymentIsDone"), value);
//    }
//
//    private Specification<AdminOrderView> provodkaIsDoneEquals(Boolean value) {
//        return (root, query, cb) -> value == null ? cb.conjunction()
//                : cb.equal(root.get("provodkaIsDone"), value);
//    }

    private Specification<AdminOrderView> paymentIsDoneEquals(Boolean value) {
        return (root, query, cb) -> {
            if (value == null) return cb.conjunction();

            if (value) {
                // paymentIsDone = true → paymentStatus = DONE
                return cb.equal(root.get("paymentStatus"), TransactionStatus.DONE);
            } else {
                // paymentIsDone = false → paymentStatus != DONE yoki null
                return cb.or(
                        cb.isNull(root.get("paymentStatus")),
                        cb.notEqual(root.get("paymentStatus"), TransactionStatus.DONE)
                );
            }
        };
    }

    private Specification<AdminOrderView> provodkaIsDoneEquals(Boolean value) {
        return (root, query, cb) -> {
            if (value == null) return cb.conjunction();

            if (value) {
                return cb.equal(root.get("provodkaStatus"), TransactionStatus.DONE);
            } else {
                return cb.or(
                        cb.isNull(root.get("provodkaStatus")),
                        cb.notEqual(root.get("provodkaStatus"), TransactionStatus.DONE)
                );
            }
        };
    }
    // Date range
    private Specification<AdminOrderView> orderedTimeBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return cb.conjunction();
            if (from == null) return cb.lessThanOrEqualTo(root.get("orderedTime"), to);
            if (to == null) return cb.greaterThanOrEqualTo(root.get("orderedTime"), from);
            return cb.between(root.get("orderedTime"), from, to);
        };
    }
}
