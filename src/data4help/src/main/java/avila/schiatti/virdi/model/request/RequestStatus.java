package avila.schiatti.virdi.model.request;

import xyz.morphia.annotations.Embedded;

@Embedded
public enum RequestStatus {
    APPROVED, PENDING, REJECTED
}
