package avila.schiatti.virdi.model.subscription;

import avila.schiatti.virdi.model.request.D4HRequest;
import avila.schiatti.virdi.model.user.ThirdParty;
import org.bson.types.ObjectId;
import xyz.morphia.annotations.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity("subscription")
//@Indexes({@Index(fields = {@Field("thirdParty")}), @Index(fields = {@Field("request")})})
public class Subscription {
    @Id
    private ObjectId id;

    @Indexed
    @Reference(idOnly = true)
    private ThirdParty thirdParty;
    @Embedded
    private D4HQuery filter;
    private Integer timeSpan = 6;
    private LocalDateTime nextExecution;

    @Indexed
    @Reference(idOnly = true)
    private D4HRequest request;

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

    public D4HQuery getFilter() {
        if (filter == null) {
            filter = new D4HQuery();
        }
        return filter;
    }

    public void setFilter(D4HQuery filter) {
        this.filter = filter;
    }

    public Integer getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(Integer timeSpan) {
        this.timeSpan = (timeSpan == null || timeSpan < 6 ? 6 : timeSpan);
        calculateNetxtExecution();
    }

    public LocalDateTime getNextExecution() {
        return nextExecution;
    }

    public void setNextExecution(LocalDateTime next) {
        this.nextExecution = next;
    }

    public void calculateNetxtExecution() {
        this.nextExecution = LocalDateTime.now().plus(this.timeSpan, ChronoUnit.MINUTES);
    }

    public D4HRequest getRequest() {
        return request;
    }

    public void setRequest(D4HRequest request) {
        this.request = request;
    }
}
