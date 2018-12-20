package avila.schiatti.virdi.resource.api;

import avila.schiatti.virdi.model.data.Data;
import avila.schiatti.virdi.model.user.Individual;

public class IndividualDataRequest {
    private Data data;
    private String ssn;

    public IndividualDataRequest(Individual individual) {
        data = individual.getData();
        ssn = individual.getSsn();
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
