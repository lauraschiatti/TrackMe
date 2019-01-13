package avila.schiatti.virdi.service.response;

import avila.schiatti.virdi.model.request.D4HRequest;
import avila.schiatti.virdi.model.request.D4HRequestStatus;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.model.user.ThirdParty;

public class D4HReqResponse {
    private String id;
    private ThirdParty thirdParty;
    private Individual individual;
    private D4HRequestStatus status;

    public D4HReqResponse(D4HRequest d4hreq) {
        id = d4hreq.getId().toString();
        thirdParty = new ThirdParty();
        thirdParty.setName(d4hreq.getThirdParty().getName());
        individual = new Individual();
        individual.setName(d4hreq.getIndividual().getName());
        status = d4hreq.getStatus();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ThirdParty getThirdParty() {
        return thirdParty;
    }

    public void setThirdParty(ThirdParty thirdParty) {
        this.thirdParty = thirdParty;
    }

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public D4HRequestStatus getStatus() {
        return status;
    }

    public void setStatus(D4HRequestStatus status) {
        this.status = status;
    }
}
