package avila.schiatti.virdi;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.service.*;

public class Main {
    private final static String STATIC_FILES_PATH = "public";
    private static final StaticConfiguration config = StaticConfiguration.getInstance();

    public static void main(String[] args) {
        Data4HelpApp.getInstance()
                .createServer(config.getPort())
                .setPublicPath(STATIC_FILES_PATH)
                .registerService(LoginService.create())
                .registerService(SignupService.create())
                .registerService(D4HRequestService.create())
                .registerService(SubscriptionService.create())
                .registerService(DataService.create())
                .setAuthHandlers()
                .init();
    }
}
