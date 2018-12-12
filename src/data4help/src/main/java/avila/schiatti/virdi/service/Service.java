package avila.schiatti.virdi.service;

import avila.schiatti.virdi.utils.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

public abstract class Service {
    Gson jsonTransformer = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    public abstract void setupWebEndpoints();
    public abstract void setupApiEndpoints();
    public abstract void setupExceptionHandlers();
}
