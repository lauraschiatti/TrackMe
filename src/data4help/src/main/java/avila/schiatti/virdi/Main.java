package avila.schiatti.virdi;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.service.LoginService;
import avila.schiatti.virdi.service.SignupService;
import avila.schiatti.virdi.utils.Data4HelpApp;

public class Main {
    private final static String STATIC_FILES_PATH = "public";
    private static final StaticConfiguration config = StaticConfiguration.getInstance();

    public static void main(String[] args) {
        Data4HelpApp.getInstance()
                .createServer(config.getPort())
                .setPublicPath(STATIC_FILES_PATH)
                .registerService(LoginService.create())
                .registerService(SignupService.create())
                .setAuthHandlers()
                .init();
    }
}
