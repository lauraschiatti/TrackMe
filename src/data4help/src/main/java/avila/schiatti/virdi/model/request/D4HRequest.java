package avila.schiatti.virdi.model.request;

import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.model.user.ThirdParty;
import org.bson.types.ObjectId;
import xyz.morphia.annotations.*;

@Entity("request")
@Indexes(@Index(fields = { @Field("thirdParty"), @Field("individual") }, options = @IndexOptions(unique = true)))
public class D4HRequest {
    @Id
    private ObjectId id;

    @Reference(idOnly = true)
    private ThirdParty thirdParty;
    @Reference(idOnly = true)
    private Individual individual;
    @Embedded
    private D4HRequestStatus status = D4HRequestStatus.PENDING;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
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
        if(this.status == null){
            this.status = D4HRequestStatus.PENDING;
        }
        return status;
    }

    public void setStatus(D4HRequestStatus status) {
        this.status = status;
    }
}
