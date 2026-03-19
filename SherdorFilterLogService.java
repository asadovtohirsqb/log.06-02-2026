package uz.sqb.joyda.carddeliveryservice.service;

import uz.sqb.joyda.carddeliveryservice.payload.sherdor.SherdorFilterLoggerDTO;

public interface SherdorFilterLogService {
    Boolean loggingFilter(SherdorFilterLoggerDTO loggerDTO);
}
