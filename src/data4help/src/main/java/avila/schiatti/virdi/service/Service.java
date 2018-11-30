package avila.schiatti.virdi.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

abstract class Service {
    Gson jsonTransformer = new GsonBuilder().setDateFormat("dd-MM-yyyy").setPrettyPrinting().create();

    abstract void setupWebEndpoints();
    abstract void setupApiEndpoints();
    abstract void setupExceptionHandlers();
}
