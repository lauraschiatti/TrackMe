package avila.schiatti.virdi.utils;

import avila.schiatti.virdi.exception.ValidationError;
import avila.schiatti.virdi.exception.ValidationException;
import avila.schiatti.virdi.model.data.Address;
import org.apache.commons.validator.EmailValidator;

public class Validator {
    private static final EmailValidator emailValidator = EmailValidator.getInstance();

    private Validator(){}

    public static Boolean isEmail(String email){
        return emailValidator.isValid(email);
    }

    public static void validateEmail(String email){
        if(email == null || !emailValidator.isValid(email)){
            throw new ValidationException(ValidationError.NOT_VALID_EMAIL);
        }
    }

    public static Boolean isNullOrEmpty(String string){
        return (string == null || string.isEmpty());
    }

    public static void isNullOrEmpty(String string, String fieldName){
        if(string == null || string.isEmpty()){
            String message = String.format(ValidationError.NOT_VALID_FIELD.getMessage(), fieldName);
            throw new ValidationException(message);
        }
    }

    public static void validateAddress(Address address){
        if(address == null ||
                isNullOrEmpty(address.getCity()) ||
                isNullOrEmpty(address.getCountry()) ||
                isNullOrEmpty(address.getProvince()) ){
            throw new ValidationException(ValidationError.NOT_VALID_ADDRESS);
        }
    }

}
