package avila.schiatti.virdi.exception;

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

    public Integer getCode(){
        return error.getCode();
    }

    public String toJsonString(){
        HashMap<String, String> json = new HashMap<>();
        json.put("code", String.valueOf(this.getCode()));
        json.put("message", this.getMessage());

        Gson gson = new Gson();
        return gson.toJson(json);
    }
}
