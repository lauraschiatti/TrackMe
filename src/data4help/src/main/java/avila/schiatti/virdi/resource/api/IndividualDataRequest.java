package avila.schiatti.virdi.resource.api;

import avila.schiatti.virdi.model.data.Data;

public class IndividualDataRequest {
    private Data data;
    private String ssn;

    public IndividualDataRequest(String ssn, Data data) {
        this.data = data;
        this.ssn = ssn;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }
}
