package avila.schiatti.virdi.utils;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;

public class Validator {
    private static final EmailValidator emailValidator = EmailValidator.getInstance();
    private static final UrlValidator urlValidator = UrlValidator.getInstance();

    private Validator(){}

    public static Boolean isEmail(String email){
        return emailValidator.isValid(email);
    }

    public static Boolean isNullOrEmpty(String string){
        return (string == null || string.isEmpty());
    }

    public static Boolean isURL(String url){
        return urlValidator.isValid(url);
    }

}
