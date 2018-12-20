package avila.schiatti.virdi.resource.api;

import avila.schiatti.virdi.model.request.D4HRequest;
import avila.schiatti.virdi.model.request.D4HRequestStatus;
import avila.schiatti.virdi.model.user.Individual;

public class NotificationRequest {
    private Individual individual;
    private D4HRequestStatus status;
    private String requestId;

    public NotificationRequest(D4HRequest request) {
        individual = cloneIndividual(request.getIndividual());
        status = request.getStatus();
        requestId = request.getId().toString();
    }

    private Individual cloneIndividual(Individual individual){
        Individual i = new Individual();
        i.setBloodType(individual.getBloodType());
        i.setId(individual.getId());
        i.setGender(individual.getGender());
        i.setBirthDate(individual.getBirthDate());
        i.setAddress(individual.getAddress());
        i.setSsn(individual.getSsn());
        i.setName(individual.getName());

        return i;
    }

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = cloneIndividual(individual);
    }

    public D4HRequestStatus getStatus() {
        return status;
    }

    public void setStatus(D4HRequestStatus status) {
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
