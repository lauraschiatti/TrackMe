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

    public D4HReqResponse(D4HRequest request) {
        // remove third party private attributes
        request.getThirdParty().setAppId(null);
        request.getThirdParty().setConfig(null);
        request.getThirdParty().setPhone(null);
        request.getThirdParty().setSecretKey(null);
        request.getThirdParty().setTaxCode(null);
        request.getThirdParty().setCode(null);
        request.getThirdParty().setEmail(null);
        request.getThirdParty().setPassword(null);
        request.getThirdParty().setCertificate(null);

        // remove individual private attributes
        request.getIndividual().setPassword(null);
        request.getIndividual().setSsn(null);
        request.getIndividual().setAddress(null);
        request.getIndividual().setBirthDate(null);
        request.getIndividual().setData(null);
        request.getIndividual().setGender(null);
        request.getIndividual().setEmail(null);
        request.getIndividual().setBloodType(null);

        this.id = request.getId().toString();
        this.thirdParty = request.getThirdParty();
        this.individual = request.getIndividual();
        this.status = request.getStatus();
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
