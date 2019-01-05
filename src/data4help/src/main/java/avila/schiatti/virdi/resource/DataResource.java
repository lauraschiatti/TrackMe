package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.model.data.Data;
import avila.schiatti.virdi.model.subscription.D4HQuery;
import avila.schiatti.virdi.model.user.Individual;
import org.bson.types.ObjectId;
import xyz.morphia.Datastore;
import xyz.morphia.query.Query;

import java.util.Collection;
import java.util.stream.Collectors;

public class DataResource extends Resource<Data> {

    public DataResource(Datastore datastore) {
        super(datastore, Data.class);
    }

    private DataResource() {
        super(Data.class);
    }

    public static DataResource create() {
        return new DataResource();
    }

    public Data getByIndividualId(ObjectId id){
        return datastore.find(Data.class)
                .field("id")
                .equal(id)
                .get();
    }

    public Data getByIndividual(Individual individual){
        Data data = getByIndividualId(individual.getId());

        if(data == null){
            data = new Data();
            data.setIndividual(individual);
        }
        return data;
    }

    public Collection<Data> getByIndividualList(Collection<Individual> individuals){
        return datastore.find(Data.class)
                .field("individual")
                .in( individuals.stream()
                        .map(Individual::getId)
                        .collect(Collectors.toList()) )
                .asList();
    }
}
