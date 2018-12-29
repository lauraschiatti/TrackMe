package avila.schiatti.virdi.service.request;

import avila.schiatti.virdi.model.subscription.D4HQuery;

public class SubscriptionRequest {
    private D4HQuery filter;
    private Integer timeSpan;

    public D4HQuery getFilter() {
        return filter;
    }

    public void setFilter(D4HQuery filter) {
        this.filter = filter;
    }

    public Integer getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(Integer timeSpan) {
        this.timeSpan = (timeSpan < 6 ? 6 : timeSpan);
    }
}
