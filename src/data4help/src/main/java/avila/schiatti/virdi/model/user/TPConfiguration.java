package avila.schiatti.virdi.model.user;

import xyz.morphia.annotations.Embedded;

@Embedded
public class TPConfiguration {
    private String individualPushUrl = "";
    private String bulkPushUrl = "";
    private String notificationUrl = "";

    public String getIndividualPushUrl() {
        return individualPushUrl;
    }

    public void setIndividualPushUrl(String individualPushUrl) {
        this.individualPushUrl = individualPushUrl;
    }

    public String getBulkPushUrl() {
        return bulkPushUrl;
    }

    public void setBulkPushUrl(String bulkPushUrl) {
        this.bulkPushUrl = bulkPushUrl;
    }

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl) {
        this.notificationUrl = notificationUrl;
    }
}
