package avila.schiatti.virdi.service;

import avila.schiatti.virdi.utils.JSONObjectMapper;
import avila.schiatti.virdi.utils.adapter.LocalDateAdapter;
import avila.schiatti.virdi.utils.adapter.ObjectIDAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bson.types.ObjectId;

import java.time.LocalDate;

public abstract class Service {
    Gson jsonTransformer = JSONObjectMapper.jsonTransformer;

    public void setupApiEndpoints(){

    }
}
