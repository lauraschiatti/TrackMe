package avila.schiatti.virdi.exception;

public enum TrackMeError {
    // BAD REQUEST 400
    NOT_VALID_USER(40001, "The provided user id or ssn is not valid or null"),
    NOT_VALID_SIGNUP_REQUEST(40002, "The provided fields for signup are not valid."),
    VALIDATION_ERROR(40002, "The following error has occurred: %s"),
    NOT_VALID_REQUEST_ID(40003, "The provided request id is not valid"),
    NULL_OR_NOT_VALID_FILTER(40004, "The provided filter is not valid"),
    NOT_VALID_SUBSCRIPTION(40005, "The provided subscription id does not exist or does not belong to the third party"),
    NO_REQUEST_FOUND(40006, "Should send a request to the individual to access his data"),
    NO_APPROVED_REQUEST(40007, "The request has not been approved yet"),
    CANNOT_ANONYMIZE_DATA(40008, "Cannot anonymize data. Few individuals involved"),
    DUPLICATE_KEY(40009, "The value %s already exist in our database"),

    // FORBIDDEN
    NOT_VALID_TOKEN(40301, "The provided token does not exist"),
    NULL_TOKEN(40302, "Token is mandatory"),
    NOT_VALID_SESSION(40303, "Session is not valid any more"),
    NOT_VALID_SECRET_KEY(40304, "The provided secret key is invalid"),
    NOT_VALID_EMAIL_OR_PASSWORD(40305, "The provided email or password are invalid"),
    UNAUTHORIZED_USER(40306, "The current user is not authorized to perform the operation");

    private final Integer code;
    private final String message;

    TrackMeError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    // Http Status code
    public Integer getStatus(){
        return ( this.getCode()/100 );
    }

    @Override
    public String toString() {
        return "[ " + code + " ]: " + message;
    }
}
