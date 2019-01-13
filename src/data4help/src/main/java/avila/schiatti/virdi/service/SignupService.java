package avila.schiatti.virdi.service;

import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.exception.ValidationError;
import avila.schiatti.virdi.exception.ValidationException;
import avila.schiatti.virdi.model.data.Address;
import avila.schiatti.virdi.model.data.Data;
import avila.schiatti.virdi.model.user.*;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.authentication.AuthenticationManager;
import avila.schiatti.virdi.service.authentication.ThirdPartyApiAuth;
import avila.schiatti.virdi.service.authentication.UserWebAuth;
import avila.schiatti.virdi.service.request.IndividualSignupRequest;
import avila.schiatti.virdi.service.request.ThirdPartySignupRequest;
import avila.schiatti.virdi.service.response.SignupResponse;
import avila.schiatti.virdi.utils.Validator;
import spark.Request;
import spark.Response;

import java.time.LocalDateTime;

import static spark.Spark.post;

public class SignupService extends Service {

    private UserResource userResource;
    private AuthenticationManager authManager;

    private SignupService(){
        userResource = UserResource.create();
        authManager = AuthenticationManager.getInstance();
    }

    /**
     * Only for testing.
     * @param userResource
     * @param authManager
     */
    public SignupService(UserResource userResource, AuthenticationManager authManager){
        this.authManager = authManager;
        this.userResource = userResource;
    }

    public static SignupService create(){
        return new SignupService();
    }

    private SignupResponse signupIndividual(Request req, Response res){
        try {
            // parse the request to an IndividualSignupRequest
            IndividualSignupRequest body = jsonTransformer.fromJson(req.body(), IndividualSignupRequest.class);

            // validate the request
            validateIndividualSignupRequest(body);

            // create the individual
            Individual individual = buildIndividual(body);

            // create and authenticate the individual
            UserWebAuth auth = createAndAuthenticateUser(individual);

            // getInstance the SignupResponse
            return createSignupResponse(auth, individual.getRole());
        } catch(ValidationException validationEx){
            String msg = String.format(TrackMeError.VALIDATION_ERROR.getMessage(), validationEx.getMessage());
            throw new TrackMeException(TrackMeError.VALIDATION_ERROR, msg);
        }
    }

    private SignupResponse signupThirdParty(Request req, Response res){
        try {
            // parse the request to an IndividualSignupRequest
            ThirdPartySignupRequest body = jsonTransformer.fromJson(req.body(), ThirdPartySignupRequest.class);

            // validate the request
            validateThirdPartySignupRequest(body);

            // create the individual
            ThirdParty thirdParty = buildThirdParty(body);

            // create and authenticate the thirdParty
            UserWebAuth auth = createAndAuthenticateUser(thirdParty);

            // getInstance the SignupResponse.
            return createSignupResponse(auth, thirdParty.getRole());
        } catch(ValidationException validationEx){
            String msg = String.format(TrackMeError.VALIDATION_ERROR.getMessage(), validationEx.getMessage());
            throw new TrackMeException(TrackMeError.VALIDATION_ERROR, msg);
        }
    }

    private SignupResponse createSignupResponse(UserWebAuth auth, D4HUserRole role) {
        // getInstance the SignupResponse
        SignupResponse response = new SignupResponse();
        response.setUserId(auth.getUserId());
        response.setAccessToken(auth.getAccessToken());
        response.setRole(role);

        // return the SignupResponse
        return response;
    }

    private UserWebAuth createAndAuthenticateUser(D4HUser user) {
        // add the individual to the database
        userResource.add(user);
        // create access credentials for the newly created user
        return authManager.setUserAccessToken(user);
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
        individual.setPassword(AuthenticationManager.hashPassword(request.getPassword()));
        individual.setBirthDate(request.getBirthDate());
        individual.setBloodType(request.getBloodType());
        individual.setGender(request.getGender());
        individual.setName(request.getName());
        individual.setSsn(request.getSsn());
        individual.setWeight(request.getWeight());
        individual.setHeight(request.getHeight());
        individual.setAddress(new Address());
        individual.getAddress().setCity(request.getAddress().getCity());
        individual.getAddress().setCountry(request.getAddress().getCountry());
        individual.getAddress().setProvince(request.getAddress().getProvince());
        return individual;
    }

    private void validateThirdPartySignupRequest(ThirdPartySignupRequest request){
        Validator.validateEmail(request.getEmail());
        Validator.isNullOrEmpty(request.getName(), "Business Name");
        Validator.isNullOrEmpty(request.getTaxCode(), "Tax Code");
        Validator.isNullOrEmpty(request.getPhone(), "Phone");
        Validator.isNullOrEmpty(request.getPassword(), "Password");
        //TODO Certificate is not validated, and it is set to empty string.
        request.setCertificate("");
    }

    private ThirdParty buildThirdParty(ThirdPartySignupRequest request){
        ThirdParty thirdParty = new ThirdParty();
        thirdParty.setEmail(request.getEmail());
        thirdParty.setPassword(AuthenticationManager.hashPassword(request.getPassword()));
        thirdParty.setCertificate(request.getCertificate());
        thirdParty.setName(request.getName());
        thirdParty.setPhone(request.getPhone());
        thirdParty.setTaxCode(request.getTaxCode());
        thirdParty.setConfig(request.getConfig());
        // creates the APP_ID and SECRET_KEY to be used by the third party.
        String objSeed = thirdParty.toString() + String.valueOf(Math.random()) + LocalDateTime.now().toString();
        ThirdPartyApiAuth apiKeys = authManager.setThirdPartySecretKey(objSeed);

        thirdParty.setSecretKey(apiKeys.getSecretKey());
        thirdParty.setAppId(apiKeys.getAppId());

        return thirdParty;
    }

    @Override
    public void setupWebEndpoints() {
        post("/individual/signup", this::signupIndividual, jsonTransformer::toJson);

        post("/thirdparty/signup", this::signupThirdParty, jsonTransformer::toJson);
    }

}
