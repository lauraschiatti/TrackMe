package avila.schiatti.virdi.jobs;

import avila.schiatti.virdi.jobs.migration.IndividualMigration;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.resource.UserResource;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;

public class MigratorJob implements Scheduler {
    private final static String INDIVIDUAL_FILE = "migration/individuals.json";
    private static Logger logger = LoggerFactory.getLogger(MigratorJob.class);
    private Gson gson = new Gson();
    private Thread thread;

    private MigratorJob() {
        Job job;
        try {
            FileReader fd = new FileReader(INDIVIDUAL_FILE);
            BufferedReader bufferedReader = new BufferedReader(fd);

            JsonArray info = gson.fromJson(bufferedReader, JsonArray.class);
            job = new Job(info);
            thread = new Thread(job);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

    }

    public static MigratorJob create() {
        return new MigratorJob();
    }


    public void start() {
        // start the import in a new different thread.
        if (thread != null) thread.start();
    }

    public void stop() {
        if (thread != null) thread.interrupt();
    }

    private class Job implements Runnable {
        private UserResource userResource;
        private JsonArray migrationInfo;

        Job(JsonArray info) {
            userResource = UserResource.create();
            migrationInfo = info;
        }

        @Override
        public void run() {
            migrationInfo.iterator().forEachRemaining(jsonElement -> {
                try {
                    IndividualMigration mig = gson.fromJson(jsonElement.toString(), IndividualMigration.class);
                    Individual i = mig.buildIndividual();
                    userResource.add(i);
                } catch (Exception ex) {
                    // only log the error and continue.
                    logger.error(ex.getMessage());
                }
            });
        }
    }
}
