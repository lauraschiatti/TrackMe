package avila.schiatti.virdi.exception;

import avila.schiatti.virdi.service.response.ErrorResponse;
import com.google.gson.Gson;
import com.mongodb.DuplicateKeyException;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TrackMeException extends RuntimeException {
    private TrackMeError error;

    public TrackMeException(TrackMeError error) {
        super(error.getMessage());

        this.error = error;
    }

    public TrackMeException(TrackMeError error, String message) {
        super(message);

        this.error = error;
    }

    public Integer getStatusCode(){
        return error.getStatus();
    }

    public Integer getCode(){
        return error.getCode();
    }

    public String toJsonString(){
        Gson gson = new Gson();
        return gson.toJson(new ErrorResponse(this));
    }

    public static TrackMeException transformFromMongoException(DuplicateKeyException dkex){
        // Example: Write failed with error code 11000 and error message 'E11000 duplicate key error collection: trackme.user index: ssn_1 dup key: { : "999999999" }'
        Pattern pattern = Pattern.compile("\"(.*?)\"");
        Matcher matcher = pattern.matcher(dkex.getMessage());
        String key = matcher.find() ? matcher.group() : "";
        String message = String.format(TrackMeError.DUPLICATE_KEY.getMessage(), key.replace("\"", ""));

        return new TrackMeException(TrackMeError.DUPLICATE_KEY, message);
    }
}
