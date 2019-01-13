package avila.schiatti.virdi.service.request;

import avila.schiatti.virdi.model.request.D4HRequestStatus;

public class D4HReqRequest {
    private String ssn;
    private D4HRequestStatus status;

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public D4HRequestStatus getStatus() {
        return status;
    }

    public void setStatus(D4HRequestStatus status) {
        this.status = status;
    }
}
