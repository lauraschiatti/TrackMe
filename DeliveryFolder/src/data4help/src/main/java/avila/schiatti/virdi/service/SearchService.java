package avila.schiatti.virdi.service;

import avila.schiatti.virdi.Data4HelpApp;
import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.model.data.BloodType;
import avila.schiatti.virdi.model.data.Data;
import avila.schiatti.virdi.model.data.Gender;
import avila.schiatti.virdi.model.subscription.D4HQuery;
import avila.schiatti.virdi.model.user.D4HUser;
import avila.schiatti.virdi.model.user.D4HUserRole;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.resource.D4HRequestResource;
import avila.schiatti.virdi.resource.DataResource;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.response.ResponseWrapper;
import avila.schiatti.virdi.utils.Validator;
import spark.Request;
import spark.Response;

import java.util.*;

import static spark.Spark.get;

public class SearchService extends Service {

    private DataResource dataResource;
    private D4HRequestResource requestResource;
    private UserResource userResource;

    public SearchService(DataResource dataResource, D4HRequestResource requestResource, UserResource userResource) {
        this.dataResource = dataResource;
        this.requestResource = requestResource;
    }

    private SearchService(){
        dataResource = DataResource.create();
        requestResource = D4HRequestResource.create();
        userResource = UserResource.create();
    }

    public static SearchService create(){
        return new SearchService();
    }

    @Override
    public void setupWebEndpoints() {
        get("/search", this::search, jsonTransformer::toJson);
    }

    private ResponseWrapper<List<Data>> search(Request req, Response res){
        String thirdPartyId = req.headers(Data4HelpApp.USER_ID);
        D4HUser user = userResource.getById(thirdPartyId);

        if(!D4HUserRole.THIRD_PARTY.equals(user.getRole())){
            throw new TrackMeException(TrackMeError.UNAUTHORIZED_USER);
        }

        String ssn = req.queryParams("ssn");

        List<Data> data;

        if(!Validator.isNullOrEmpty(ssn)){
            Individual individual = userResource.getBySSN(ssn);

            if(individual == null){
                throw new TrackMeException(TrackMeError.NOT_VALID_USER);
            }

            requestResource.checkApprovedRequest(individual.getId(), user.getId());

            data = getIndividualData(individual);
        } else {
            String country = req.queryParams("country");
            String city = req.queryParams("city");
            String province = req.queryParams("province");
            Integer minAge = !Validator.isNullOrEmpty(req.queryParams("minAge")) ? Integer.valueOf(req.queryParams("minAge")) : null;
            Integer maxAge = !Validator.isNullOrEmpty(req.queryParams("maxAge")) ? Integer.valueOf(req.queryParams("maxAge")) : null;
            Gender gender = !Validator.isNullOrEmpty(req.queryParams("gender")) ? Gender.valueOf(req.queryParams("gender")) : null;
            BloodType bloodType = !Validator.isNullOrEmpty(req.queryParams("bloodType")) ? BloodType.valueOf(req.queryParams("bloodType")) : null;

            D4HQuery query = new D4HQuery();
            query.setCity(city);
            query.setCountry(country);
            query.setProvince(province);
            query.setMinAge(minAge);
            query.setMinAge(maxAge);
            query.setBloodType(bloodType);
            query.setGender(gender);

            data = getAnonymizedDataFromQuery(query);
        }

        return new ResponseWrapper<>(data);
    }

    private List<Data> getIndividualData(Individual individual) {
        ArrayList<Individual> individuals = new ArrayList<>(Collections.emptyList());
        individuals.add(individual);

        HashMap<String, Boolean> projections = new HashMap<>();
        projections.put(DataResource.Projections.INDIVIDUAL, Boolean.FALSE);

        return dataResource.getByIndividualList(individuals, projections);
    }

    private List<Data> getAnonymizedDataFromQuery(D4HQuery query){
        List<Individual> individuals = userResource.getByQuery(query);
        List<Data> data = dataResource.getAnonymizeByIndividualList(individuals);

        if(data.size() < StaticConfiguration.MINIMUM_ANONYMIZE_SIZE){
            data = Collections.emptyList();
        }

        return data;
    }
}
