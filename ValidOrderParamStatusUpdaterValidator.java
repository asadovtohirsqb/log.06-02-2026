package uz.sqb.joyda.carddeliveryservice.annotation.validation.handler;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uz.sqb.joyda.carddeliveryservice.annotation.validation.ValidOrderParamStatusUpdater;
import uz.sqb.joyda.carddeliveryservice.payload.adminOrderView.OrderParamStatusUpdater;
import uz.sqb.joyda.carddeliveryservice.repository.card_delivery.OrderParamRepository;

public class ValidOrderParamStatusUpdaterValidator implements ConstraintValidator<ValidOrderParamStatusUpdater, OrderParamStatusUpdater> {
    private final OrderParamRepository orderParamRepository;

    public ValidOrderParamStatusUpdaterValidator(OrderParamRepository orderParamRepository) {
        this.orderParamRepository = orderParamRepository;
    }

    @Override
    public boolean isValid(OrderParamStatusUpdater updater, ConstraintValidatorContext context) {
        if (!orderParamRepository.existsById(updater.orderId())) {
            String customMessage = "INVALID_ORDER_PARAM_STATUS_UPDATER. Berilgan id li order_param mavjud emas.";
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(customMessage)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
