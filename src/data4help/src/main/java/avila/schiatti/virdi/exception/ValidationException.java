package avila.schiatti.virdi.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(ValidationError error) {
        super(error.getMessage());
    }

    public ValidationException(String message) {
        super(message);
    }
}
