package avila.schiatti.virdi.service;

import avila.schiatti.virdi.model.data.Data;
import avila.schiatti.virdi.model.data.HealthStatus;
import avila.schiatti.virdi.model.health.HealthParameter;
import avila.schiatti.virdi.model.health.Status;
import avila.schiatti.virdi.model.health.Threshold;
import avila.schiatti.virdi.model.user.ASOSUser;
import avila.schiatti.virdi.model.user.Address;
import avila.schiatti.virdi.model.user.EmergencyContact;
import avila.schiatti.virdi.resource.*;
import avila.schiatti.virdi.service.request.IndividualDataRequest;
import avila.schiatti.virdi.service.request.NotificationRequest;
import avila.schiatti.virdi.service.response.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

import static spark.Spark.get;
import static spark.Spark.post;

public class DataService extends Service {
    private static Logger logger = LoggerFactory.getLogger(DataService.class);
    private static final String APPROVED_REQUEST = "APPROVED";

    private UserResource userResource;
    private EmergencyContactResource contactResource;
    private ThresholdResource thresholdResource;
    private AddressResource addressResource;
    private APIManager apiManager;

    private DataService() {
        apiManager = APIManager.create();
        contactResource = EmergencyContactResource.create();
        userResource = UserResource.create();
        addressResource = AddressResource.create();
        thresholdResource = ThresholdResource.getInstance();
    }

    public static DataService create() {
        return new DataService();
    }

    @Override
    public void setupApiEndpoints() {
        post("/data", this::getIndividualData, jsonTransformer::toJson);

        post("/notification", this::getRequestNotification, jsonTransformer::toJson);

        get("/address", this::getAddresses, jsonTransformer::toJson);
    }

    private ResponseWrapper<Collection<Address>> getAddresses(Request request, Response response) {
        Collection<Address> addresses = contactResource.getAll()
                .stream()
                .map(EmergencyContact::getAddress)
                .collect(Collectors.toList());

        logger.info("Get addresses: {}", jsonTransformer.toJson(addresses));
        return new ResponseWrapper<>(addresses);
    }

    private ResponseWrapper<String> getRequestNotification(Request request, Response response) {
        NotificationRequest notification = jsonTransformer.fromJson(request.body(), NotificationRequest.class);
        logger.info("Got following notification: {}", jsonTransformer.toJson(notification));

        // approved request
        if (APPROVED_REQUEST.equalsIgnoreCase(notification.getStatus())) {
            ASOSUser individual = notification.getIndividual();

            Address addr = addressResource.get(individual.getAddress());
            individual.setAddress(addr);

            EmergencyContact contact = contactResource.getByAddress(addr);
            individual.setContact(contact);

            userResource.add(individual);
            return new ResponseWrapper<>("OK");
        }
        // if the request was not approved, it was rejected.
        ASOSUser individual = userResource.getBySSN(notification.getIndividual().getSsn());
        if (individual != null) {
            userResource.remove(individual);
        }

        return new ResponseWrapper<>("KO");
    }

    private ResponseWrapper<String> getIndividualData(Request req, Response res) {
        IndividualDataRequest dataRequest = jsonTransformer.fromJson(req.body(), IndividualDataRequest.class);
        logger.info("Got following individual data: {}", jsonTransformer.toJson(dataRequest));

        ASOSUser individual = userResource.getBySSN(dataRequest.getSsn());

        if (individual == null) {
            return new ResponseWrapper<>("KO");
        }

        if (Status.CRITICAL.equals(getStatusFromData(dataRequest.getData(), individual))) {
            apiManager.sendNotification(individual, dataRequest.getData());
        }

        return new ResponseWrapper<>("OK");
    }

    private Status getStatusFromData(Data data, ASOSUser individual) {
        int age = (LocalDate.now().getYear() - individual.getBirthDate().getYear());
        HealthStatus health = data.getHealthStatus();

        return (compareThresholds(age, health) ? Status.CRITICAL : Status.STABLE);
    }

    private Boolean compareThresholds(Integer age, HealthStatus health) {
        HashMap<HealthParameter, Threshold> thresholds = thresholdResource.get(age);

        return (   (thresholdResource.compare(Double.valueOf(health.getBloodOxygen()), thresholds.get(HealthParameter.BLOOD_OXYGEN)))
                || (thresholdResource.compare(health.getBodyTemperature(), thresholds.get(HealthParameter.TEMPERATURE)))
                || (thresholdResource.compare(Double.valueOf(health.getHeartRate()), thresholds.get(HealthParameter.HART_RATE)))
                || (thresholdResource.compare(health.getDiastolic(), thresholds.get(HealthParameter.DIASTOLIC)))
                || (thresholdResource.compare(health.getSystolic(), thresholds.get(HealthParameter.SYSTOLIC))) );
    }
}
