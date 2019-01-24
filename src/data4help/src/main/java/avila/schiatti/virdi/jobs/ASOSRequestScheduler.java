package avila.schiatti.virdi.jobs;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.model.data.Address;
import avila.schiatti.virdi.model.request.D4HRequest;
import avila.schiatti.virdi.model.subscription.D4HQuery;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.model.user.ThirdParty;
import avila.schiatti.virdi.resource.APIManager;
import avila.schiatti.virdi.resource.D4HRequestResource;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.utils.JSONObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unirest.Unirest;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ASOSRequestScheduler implements Scheduler {
    private static Logger logger = LoggerFactory.getLogger(ASOSRequestScheduler.class);

    private static Integer INITIAL_DELAY = 1;
    private static Integer PERIOD = 1;
    private static TimeUnit TIME_UNIT = TimeUnit.MINUTES;

    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> scheduler;
    private Job job = new Job();

    private ASOSRequestScheduler(){
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    public static ASOSRequestScheduler create(){
        return new ASOSRequestScheduler();
    }

    public ASOSRequestScheduler setDelay(Integer delay){
        INITIAL_DELAY = delay;
        return this;
    }

    public ASOSRequestScheduler setPeriod(Integer period){
        PERIOD = period;
        return this;
    }

    public ASOSRequestScheduler setTimeUnit(TimeUnit unit){
        TIME_UNIT = unit;
        return this;
    }

    @Override
    public void start(){
        logger.info("Start the scheduler: delay {} period {} {}", INITIAL_DELAY, PERIOD, TIME_UNIT.toString());
        scheduler = executor.scheduleAtFixedRate(job, INITIAL_DELAY, PERIOD, TIME_UNIT);
    }

    @Override
    public void stop(){
        scheduler.cancel(true);
    }


    private class Job implements Runnable {
        private final String ASOS_EMAIL = "automatedsos@data4help.com";
        private final String ASOS_ADDRESS_URL = StaticConfiguration.getInstance().getASOSAddressURL();
        private final UserResource userResource;
        private final D4HRequestResource requestResource;
        private final APIManager apiManager;
        private final ThirdParty asos;

        Job(){
            Unirest.config().setObjectMapper(new JSONObjectMapper());
            userResource = UserResource.create();
            requestResource = D4HRequestResource.create();
            apiManager = APIManager.create();

            asos = (ThirdParty)userResource.getByEmail(ASOS_EMAIL);
        }

        @Override
        public void run() {
            // find all the available addresses
            Unirest.get(ASOS_ADDRESS_URL)
                    .asJsonAsync(response -> {
                        List<Object> list = response.getBody().getObject().getJSONArray("data").toList();
                        List<Address> addresses = list.stream().map(o -> {
                            HashMap<String, String> map = (HashMap<String, String>) o;
                            Address a = new Address();
                            a.setCity(map.get("city"));
                            a.setProvince(map.get("province"));
                            a.setCountry(map.get("country"));
                            return a;
                        }).collect(Collectors.toList());

                        // for each address we should get all the older individuals
                        addresses.forEach(a -> {
                            D4HQuery query = new D4HQuery();
                            query.setAddress(a);
                            query.setMinAge(60);
//                            query.setMaxAge(1000);

                            List<Individual> individuals = userResource.getByQuery(query);
                            // for each of those individuals, we need to find out if asos has already sent a request.
                            individuals.forEach(i -> {
                                D4HRequest asosRequest = requestResource.getByUserIdAndThirdPartyId(i.getId(), asos.getId());
                                // if no request was sent, we should create the request.
                                if(asosRequest == null){
                                    asosRequest = new D4HRequest();
                                    asosRequest.setThirdParty(asos);
                                    asosRequest.setIndividual(i);

                                    requestResource.add(asosRequest);
                                }
                            });
                        });
                    });

        }
    }
}
