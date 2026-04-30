package com.livehealth.shared.constant;

import java.time.ZoneId;

public class CommonConstant {
    // Common
    public static final String ADMIN_EMAIL = "livehealth@gmail.com";

    // Sorting
    public static final String SORT_TYPE_ASC = "ASC";
    public static final String SORT_TYPE_DESC = "DESC";

    // Pagination
    public static final Integer PAGE_SIZE_DEFAULT = 10;

    // Numeric Values
    public static final Integer ZERO_INT_VALUE = 0;
    public static final Integer ONE_INT_VALUE = 1;
    public static final Long ZERO_VALUE = 0L;
    public static final Long ONE_VALUE = 1L;

    // String Values
    public static final String EMPTY_STRING = "";
    public static final String BEARER_TOKEN = "Bearer";

    // Date/Time
    public static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final ZoneId APPLICATION_TIMEZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    // OTP Configuration
    public static final int OTP_EXPIRATION_MINUTES = 5;
    public static final int OTP_LENGTH = 6;
    public static final int OTP_MIN_VALUE = 100000;
    public static final int OTP_MAX_VALUE = 999999;

    // Security
    public static final int BCRYPT_STRENGTH = 10;
}
