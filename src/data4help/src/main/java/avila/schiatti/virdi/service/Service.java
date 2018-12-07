package avila.schiatti.virdi.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Service {
    Gson jsonTransformer = new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create();

    public abstract void setupWebEndpoints();
    public abstract void setupApiEndpoints();
    public abstract void setupExceptionHandlers();
}
