package avila.schiatti.virdi.utils;

import avila.schiatti.virdi.exception.ValidationError;
import avila.schiatti.virdi.exception.ValidationException;
import avila.schiatti.virdi.model.data.Address;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;

public class Validator {
    private static final EmailValidator emailValidator = EmailValidator.getInstance();
    private static final UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES | UrlValidator.ALLOW_LOCAL_URLS | UrlValidator.ALLOW_2_SLASHES);

    private Validator() {
    }

    public static Boolean isEmail(String email) {
        return emailValidator.isValid(email);
    }

    public static void validateEmail(String email) {
        if (email == null || !emailValidator.isValid(email)) {
            throw new ValidationException(ValidationError.NOT_VALID_EMAIL);
        }
    }

    public static Boolean isNullOrEmpty(String string) {
        return (string == null || string.isEmpty());
    }

    public static void isNullOrEmpty(String string, String fieldName) {
        if (string == null || string.isEmpty()) {
            String message = String.format(ValidationError.NOT_VALID_FIELD.getMessage(), fieldName);
            throw new ValidationException(message);
        }
    }

    public static void validateAddress(Address address) {
        if (address == null ||
                isNullOrEmpty(address.getCity()) ||
                isNullOrEmpty(address.getCountry()) ||
                isNullOrEmpty(address.getProvince())) {
            throw new ValidationException(ValidationError.NOT_VALID_ADDRESS);
        }
    }

    public static Boolean isURL(String url) {
        return urlValidator.isValid(url);
    }

    public static void validateURL(String url) {
        if (url != null && !url.isEmpty() && !isURL(url)) {
            String msg = String.format(ValidationError.NOT_VALID_URL.getMessage(), url);
            throw new ValidationException(msg);
        }
    }

}
