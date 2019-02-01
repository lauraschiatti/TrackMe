package avila.schiatti.virdi.service.request;

import avila.schiatti.virdi.model.user.ASOSUser;

public class NotificationRequest {
    private ASOSUser individual;
    private String status;


    public ASOSUser getIndividual() {
        return individual;
    }

    public void setIndividual(ASOSUser individual) {
        this.individual = individual;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
