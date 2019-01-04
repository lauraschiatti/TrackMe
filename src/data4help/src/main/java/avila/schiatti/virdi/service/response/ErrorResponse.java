package avila.schiatti.virdi.service.response;

import avila.schiatti.virdi.exception.TrackMeException;

public class ErrorResponse {
    private Integer code;
    private String message;

    public ErrorResponse(){};

    public ErrorResponse(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public ErrorResponse(TrackMeException exception) {
        code = exception.getCode();
        message = exception.getMessage();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
