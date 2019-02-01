package avila.schiatti.virdi.jobs;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.model.data.Data;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.resource.APIManager;
import avila.schiatti.virdi.resource.DataResource;
import avila.schiatti.virdi.resource.SubscriptionResource;
import avila.schiatti.virdi.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DataScheduler implements Scheduler {
    private static Logger logger = LoggerFactory.getLogger(DataScheduler.class);

    private static Integer INITIAL_DELAY = 0;
    private static Integer PERIOD = 1;
    private static TimeUnit TIME_UNIT = TimeUnit.MINUTES;

    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> scheduler;
    private Job job = new Job();

    private DataScheduler(){
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    public static DataScheduler create(){
        return new DataScheduler();
    }

    public DataScheduler setDelay(Integer delay){
        INITIAL_DELAY = delay;
        return this;
    }

    public DataScheduler setPeriod(Integer period){
        PERIOD = period;
        return this;
    }

    public DataScheduler setTimeUnit(TimeUnit unit){
        TIME_UNIT = unit;
        return this;
    }

    public void start(){
        logger.info("Start the scheduler: delay {} period {} {}", INITIAL_DELAY, PERIOD, TIME_UNIT.toString());
        scheduler = executor.scheduleAtFixedRate(job, INITIAL_DELAY, PERIOD, TIME_UNIT);
    }

    public void stop(){
        scheduler.cancel(true);
    }

    private class Job implements Runnable {
        private SubscriptionResource subscriptionResource;
        private UserResource userResource;
        private DataResource dataResource;
        private APIManager apiManager;

        Job(){
            subscriptionResource = SubscriptionResource.create();
            userResource = UserResource.create();
            dataResource = DataResource.create();
            apiManager = APIManager.create();
        }

        @Override
        public void run() {
            logger.info("Run the Scheduler at: {} ", LocalDateTime.now().toString());
            subscriptionResource.getToBeExecutedSubscriptions().forEach(s -> {
                Collection<Individual> individuals = userResource.getByQuery(s.getFilter());
                Collection<Data> data = dataResource.getAnonymizeByIndividualList(individuals);

                if(data.size() >= StaticConfiguration.MINIMUM_ANONYMIZE_SIZE) {
                    logger.info("Send data to the following third party: {} . Subscription: {}", s.getThirdParty().getId(), s.getId());
                    apiManager.sendData(s.getThirdParty(), data);
                }

                s.calculateNetxtExecution();

                logger.info("Subscription next execution should be on {}", s.getNextExecution());
                subscriptionResource.update(s);
            });
        }
    }
}
