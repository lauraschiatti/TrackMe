package avila.schiatti.virdi.service;

import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.exception.ValidationError;
import avila.schiatti.virdi.exception.ValidationException;
import avila.schiatti.virdi.model.data.Address;
import avila.schiatti.virdi.model.data.Data;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.authentication.AuthenticationManager;
import avila.schiatti.virdi.service.authentication.UserWebAuth;
import avila.schiatti.virdi.service.request.IndividualSignupRequest;
import avila.schiatti.virdi.service.response.SignupResponse;
import avila.schiatti.virdi.utils.Validator;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class SignupService extends Service {

    private static SignupService _instance;
    private UserResource userResource = UserResource.getInstance();
    private AuthenticationManager authManager = AuthenticationManager.getInstance();

    public static SignupService getInstance(){
        if(_instance == null){
            _instance = new SignupService();
        }
        return _instance;
    }

    private SignupResponse signupIndividual(Request req, Response res){
        try {
            // parse the request to an IndividualSignupRequest
            IndividualSignupRequest body = jsonTransformer.fromJson(req.body(), IndividualSignupRequest.class);

            // validate the request
            validateIndividualSignupRequest(body);

            // create the individual
            Individual individual = buildIndividual(body);

            // add the individual to the database
            userResource.add(individual);

            // create access credentials for the newly created user
            UserWebAuth auth = authManager.setUserAccessToken(individual);

            // build the SignupResponse
            SignupResponse response = new SignupResponse();
            response.setUserId(auth.getUserId());
            response.setAccessToken(auth.getAccessToken());

            // return the SignupResponse
            return response;
        } catch(ValidationException validationEx){
            String msg = String.format(TrackMeError.NOT_VALID_SIGNUP_REQUEST_FROM_VALIDATION.getMessage(), validationEx.getMessage());
            throw new TrackMeException(TrackMeError.NOT_VALID_SIGNUP_REQUEST_FROM_VALIDATION, msg);
        }
    }

    private SignupResponse signupThirdParty(Request req, Response res){
        return null;
    }

    private void validateIndividualSignupRequest(IndividualSignupRequest request) throws ValidationException {
        Validator.isNullOrEmpty(request.getSsn(), "SSN");
        Validator.isNullOrEmpty(request.getName(), "Name");
        Validator.validateEmail(request.getEmail());
        Validator.isNullOrEmpty(request.getPassword(), "Password");
        Validator.validateAddress(request.getAddress());

        if(request.getBirthDate() == null || request.getBloodType() == null || request.getGender() == null){
            throw new ValidationException(ValidationError.NOT_VALID_EMPTY_FIELD);
        }
    }

    private Individual buildIndividual(IndividualSignupRequest request){
        Individual individual = new Individual();
        individual.setEmail(request.getEmail());
        individual.setPassword(authManager.hashPassword(request.getPassword()));
        individual.setBirthDate(request.getBirthDate());
        individual.setBloodType(request.getBloodType());
        individual.setGender(request.getGender());
        // set up an empty data object for the first time.
        individual.setData(new Data());
        individual.setName(request.getName());
        individual.setSsn(request.getSsn());
        individual.setAddress(new Address());
        individual.getAddress().setCity(request.getAddress().getCity());
        individual.getAddress().setCountry(request.getAddress().getCountry());
        individual.getAddress().setProvince(request.getAddress().getProvince());
        return individual;
    }

    @Override
    public void setupWebEndpoints() {
        post("/individual/signup", this::signupIndividual, jsonTransformer::toJson);
    }

    @Override
    public void setupApiEndpoints() {

    }

    @Override
    public void setupExceptionHandlers() {

    }
}
