package com.livehealth.shared.utils;

import com.livehealth.shared.constant.CommonConstant;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class TimeUtil {

    private TimeUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(CommonConstant.APPLICATION_TIMEZONE);
    }

    public static LocalDate today() {
        return LocalDate.now(CommonConstant.APPLICATION_TIMEZONE);
    }

    public static LocalDateTime plusMinutes(int minutes) {
        return now().plusMinutes(minutes);
    }

    public static LocalDate plusDays(int days) {
        return today().plusDays(days);
    }
}
