package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.model.data.Data;
import avila.schiatti.virdi.model.user.Individual;
import org.bson.types.ObjectId;
import xyz.morphia.Datastore;
import xyz.morphia.query.Query;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DataResource extends Resource<Data> {

    public final static class Projections {
        public final static String INDIVIDUAL = "individual";
        public final static String ID = "_id";
        public final static String LOCATION = "location";
    }

    public DataResource(Datastore datastore) {
        super(datastore, Data.class);
    }

    private DataResource() {
        super(Data.class);
    }

    public static DataResource create() {
        return new DataResource();
    }

    public Data getByIndividualId(Individual i){
        return datastore.find(Data.class)
                .field("individual")
                .equal(i)
                .get();
    }

    public Data getByIndividual(Individual individual){
        Data data = getByIndividualId(individual);

        if(data == null){
            data = new Data();
            data.setIndividual(individual);
        }
        return data;
    }

    public Collection<Data> getByIndividualList(Collection<Individual> individuals, Map<String, Boolean> projections){
        Query<Data> query = datastore.find(Data.class)
                .field("individual")
                .hasAnyOf(individuals);

        if(projections != null){
            projections.forEach(query::project);
        }

        return query.asList();
    }

    public Collection<Data> getAnonymizeByIndividualList(Collection<Individual> individuals){
        HashMap<String, Boolean> projections = new HashMap<>();
        projections.put(DataResource.Projections.INDIVIDUAL, Boolean.FALSE);
        projections.put(DataResource.Projections.LOCATION, Boolean.FALSE);
        projections.put(DataResource.Projections.ID, Boolean.FALSE);

        return getByIndividualList(individuals, projections);
    }
}
