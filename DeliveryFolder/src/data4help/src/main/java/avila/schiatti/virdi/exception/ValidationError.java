package avila.schiatti.virdi.exception;

public enum ValidationError {
    NOT_VALID_EMAIL("Provided email is not valid."),
    NOT_VALID_EMPTY_FIELD("Some mandatory fields are empty."),
    NOT_VALID_ADDRESS("Provided Country, Province or City are not valid."),
    NOT_VALID_FIELD("The field %s is not valid"),
    NOT_VALID_URL("The provided URL [%s] is not valid");

    private final String message;

    ValidationError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
