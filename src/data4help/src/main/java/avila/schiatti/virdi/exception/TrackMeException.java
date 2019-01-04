package avila.schiatti.virdi.exception;

import avila.schiatti.virdi.service.response.ErrorResponse;
import com.google.gson.Gson;

import java.util.HashMap;

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
}
