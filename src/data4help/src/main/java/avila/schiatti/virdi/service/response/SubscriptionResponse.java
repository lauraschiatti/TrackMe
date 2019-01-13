package avila.schiatti.virdi.service.response;

import avila.schiatti.virdi.model.subscription.D4HQuery;

public class SubscriptionResponse {
    private String subscriptionId;
    private D4HQuery filter;

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public D4HQuery getFilter() {
        return filter;
    }

    public void setFilter(D4HQuery filter) {
        this.filter = filter;
    }
}
