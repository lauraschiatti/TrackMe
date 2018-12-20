package avila.schiatti.virdi.resource.api;

import avila.schiatti.virdi.model.data.Data;

import java.util.ArrayList;
import java.util.Collection;

public class BulkDataRequest {
    private Collection<Data> data = new ArrayList<>();

    public BulkDataRequest(Collection<Data> bulkData) {
        // TODO we should avoid this.
        bulkData.forEach( (d) -> data.add(cloneData(d)) );
    }

    private Data cloneData(Data d) {
        Data data = new Data();
        data.setHealthStatus(d.getHealthStatus());
        data.setTimestamp(d.getTimestamp());
        return data;
    }

    public Collection<Data> getData() {
        return data;
    }

    public void setData(Collection<Data> data) {
        this.data = data;
    }
}
