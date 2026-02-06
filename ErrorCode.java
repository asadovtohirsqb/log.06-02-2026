package uz.sqb.joyda.carddeliveryservice.exception;

public final class ErrorCode {

    public static final String INPUT_CAN_NOT_BE_NULL = "1";
    public static final String USER_SERVICE_ERROR = "2";
    public static final String CARD_TYPE_NOT_FOUND = "3";
    public static final String BRANCH_NOT_FOUND = "4";
    public static final String INTERNAL_SERVER_ERROR = "5";
    public static final String CARD_SERVICE_ERROR = "6";
    public static final String CARD_BALANCE_ENOUGH = "7";
    public static final String CARD_STATUS_ERROR = "8";
    public static final String CARD_TYPE_ERROR = "9";
    public static final String UZCARD_SERVICE_ERROR = "10";
    public static final String OPERATION_PARAM_NOT_FOUND = "11";
    public static final String OPERATION_PARAM_STATUS_INACTIVE = "12";
    public static final String MY_ID_NOT_REGISTRATION_ERROR = "13";
    public static final String ORDER_PARAM_NOT_FOUND = "14";
    public static final String USER_CARD_LIMIT_ERROR = "16";
    public static final String CARD_TYPE_ALREADY_EXISTS = "17";
    public static final String NO_RESIDENT_USER = "18";
    public static final String LOCAL_CARD_COUNT_ERROR = "19";
    public static final String HAVE_ACTIVE_ORDER_FOR_THIS_USERS = "20";
    public static final String PROVIDER_NOT_FOUND = "21";
    public static final String USER_CARD_LIMIT_ERROR_20 = "22";
    public static final String SERVICE_TEMPORARILY_UNAVAILABLE = "23";
    public static final String REFERRAL_CODE_INVALID_ERROR = "24";
    public static final String REFERRAL_CODE_CHECKING_ERROR = "25";
    public static final String ADMIN_ORDER_PARAM_STATUS_CHANGING_ERROR = "26";

    private ErrorCode() {
        throw new IllegalStateException("Utility class");
    }
}
