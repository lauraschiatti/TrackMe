package avila.schiatti.virdi;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.jobs.DataScheduler;
import avila.schiatti.virdi.jobs.MigratorJob;
import avila.schiatti.virdi.service.*;

public class Main {
    private final static String STATIC_FILES_PATH = "public";
    private static final StaticConfiguration config = StaticConfiguration.getInstance();

    public static void main(String[] args) {
        Data4HelpApp.getInstance()
                .createServer(config.getPort())
                .setPublicPath(STATIC_FILES_PATH)
                .registerService(UserService.create())
                .registerService(LoginService.create())
                .registerService(SignupService.create())
                .registerService(D4HRequestService.create())
                .registerService(SubscriptionService.create())
                .registerService(SearchService.create())
                .registerService(DataService.create())
                .setAuthHandlers()
                .registerJob(DataScheduler.create())
//                .registerJob(MigratorJob.create())
                .init();
    }
}
