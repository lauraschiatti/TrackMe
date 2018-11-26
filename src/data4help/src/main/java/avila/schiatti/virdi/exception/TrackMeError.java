package avila.schiatti.virdi.exception;

import org.eclipse.jetty.http.HttpStatus;

public enum TrackMeError {
    // BAD REQUEST 400
    NOT_VALID_USER(40001, "The provided user id is not valid or null"),
    NOT_VALID_SIGNUP_REQUEST(40002, "The provided fields for signup are not valid."),
    NOT_VALID_SIGNUP_REQUEST_FROM_VALIDATION(40002, "The following error has occurred: %s"),

    // FORBIDDEN
    NOT_VALID_TOKEN(40301, "The provided token does not exist"),
    NULL_TOKEN(40302, "Token is mandatory"),
    NOT_VALID_SESSION(40303, "Session is not valid any more"),
    NOT_VALID_SECRET_KEY(40304, "The provided secret key is invalid"),
    NOT_VALID_EMAIL_OR_PASSWORD(40305, "The provided email or password are invalid");

    private final Integer code;
    private final String message;

    TrackMeError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    // TODO - return a valid HTTP status based on the code of the Error
    public Integer getStatus(){
        return HttpStatus.OK_200;
    }

    @Override
    public String toString() {
        return "[ " + code + " ]: " + message;
    }
}
