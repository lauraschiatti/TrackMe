package avila.schiatti.virdi.model.request;

import xyz.morphia.annotations.Embedded;

@Embedded
public enum D4HRequestStatus {
    APPROVED, PENDING, REJECTED;

    public static D4HRequestStatus fromString(String status){
        if(D4HRequestStatus.APPROVED.toString().equalsIgnoreCase(status)){
            return D4HRequestStatus.APPROVED;
        } else if(D4HRequestStatus.REJECTED.toString().equalsIgnoreCase(status)){
            return D4HRequestStatus.REJECTED;
        } else if(D4HRequestStatus.PENDING.toString().equalsIgnoreCase(status)){
            return D4HRequestStatus.PENDING;
        }

        return null;
    }
}
