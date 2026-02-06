package uz.sqb.joyda.carddeliveryservice.annotation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uz.sqb.joyda.carddeliveryservice.annotation.validation.handler.ValidOrderParamStatusUpdaterValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidOrderParamStatusUpdaterValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOrderParamStatusUpdater {
    String message() default "INVALID_ORDER_PARAM_STATUS_UPDATER. OrderParamStatusUpdater error.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
