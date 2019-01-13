package avila.schiatti.virdi.utils;

import avila.schiatti.virdi.utils.adapter.LocalDateAdapter;
import avila.schiatti.virdi.utils.adapter.ObjectIDAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bson.types.ObjectId;
import unirest.ObjectMapper;

import java.time.LocalDate;

public class JSONObjectMapper implements ObjectMapper {
    public static final Gson jsonTransformer = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .setPrettyPrinting()
            .registerTypeAdapter(ObjectId.class, new ObjectIDAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @Override
    public <T> T readValue(String s, Class<T> aClass) {
        return jsonTransformer.fromJson(s, aClass);
    }

    @Override
    public String writeValue(Object o) {
        return jsonTransformer.toJson(o);
    }
}
