package avila.schiatti.virdi.utils;

import com.google.gson.Gson;

public final class JsonUtil {
    private final static Gson jsonTransformer = new Gson();

    private JsonUtil(){ }

    public static String toJson( Object obj){
        return jsonTransformer.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> classOfT){
        return jsonTransformer.fromJson(json, classOfT);
    }
}

