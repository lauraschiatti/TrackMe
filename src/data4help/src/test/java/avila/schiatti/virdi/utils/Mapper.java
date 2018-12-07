package avila.schiatti.virdi.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.ObjectMapper;

public class Mapper implements ObjectMapper {
    Gson jsonTransformer = new GsonBuilder().create();

    @Override
    public <T> T readValue(String s, Class<T> aClass) {
        return jsonTransformer.fromJson(s, aClass);
    }

    @Override
    public String writeValue(Object o) {
        return jsonTransformer.toJson(o);
    }
}
