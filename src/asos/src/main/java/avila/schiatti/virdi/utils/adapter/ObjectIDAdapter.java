package avila.schiatti.virdi.utils.adapter;

import com.google.gson.*;
import org.bson.types.ObjectId;

import java.lang.reflect.Type;

public class ObjectIDAdapter implements JsonDeserializer<ObjectId>, JsonSerializer<ObjectId> {
    @Override
    public JsonElement serialize(ObjectId objectId, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(objectId.toString());
    }

    @Override
    public ObjectId deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String id = jsonElement.getAsJsonPrimitive().getAsString();
        return new ObjectId(id);
    }
}
