package avila.schiatti.virdi.service.response;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ResponseWrapper<T> {
    private Long timestamp;
    private T data;

    public ResponseWrapper(T data) {
        this.data = data;
        this.timestamp = Timestamp.valueOf(LocalDateTime.now()).getTime();
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
