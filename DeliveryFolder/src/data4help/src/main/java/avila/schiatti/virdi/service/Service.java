package avila.schiatti.virdi.service;

import avila.schiatti.virdi.utils.adapter.LocalDateAdapter;
import avila.schiatti.virdi.utils.adapter.ObjectIDAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bson.types.ObjectId;

import java.time.LocalDate;

public abstract class Service {
    Gson jsonTransformer = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .setPrettyPrinting()
            .registerTypeAdapter(ObjectId.class, new ObjectIDAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    public void setupWebEndpoints(){

    }

    public void setupApiEndpoints(){

    }

    public void setupInternalEndpoints(){
        // this should be implemented if needed.
    }

    public void setupExceptionHandlers() {
        // only implemented if needed
    }
}
