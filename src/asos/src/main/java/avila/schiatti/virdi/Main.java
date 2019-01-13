package avila.schiatti.virdi;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.service.DataService;

public class Main {
    private static final StaticConfiguration config = StaticConfiguration.getInstance();

    public static void main(String[] args) {
        ASOSApp.getInstance()
                .createServer(config.getPort())
                .registerService(DataService.create())
                .init();
    }
}